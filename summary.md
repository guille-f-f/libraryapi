si querés trabajar con roles en el JWT y en Spring Security, deberías modificar tu entidad User y también el proceso de registro para incluir y manejar los roles. Te muestro cómo hacerlo paso a paso 👇

🧱 1. Modificar la entidad User
Agregale un campo de rol. Si por ahora vas con un solo rol por usuario, podés hacer algo así:

java
Copiar
Editar
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // 👇 Nuevo campo
    private String role; // ej: "ROLE_ADMIN" o "ROLE_USER"

    // Getters y setters
}
📝 Más adelante podés escalar esto a múltiples roles con una relación @ManyToMany.

🧾 2. Modificar el registro (RegisterRequest, servicio y controlador)
DTO RegisterRequest
java
Copiar
Editar
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // 👈 Nuevo
}
En tu servicio de autenticación
java
Copiar
Editar
public String register(RegisterRequest request) {
    var user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole()); // 👈 Guardás el rol

    userRepository.save(user);

    return jwtUtil.generateToken(user.getUsername(), user.getRole()); // 👈 Generás el token con el rol
}
🧠 3. Usar el rol al autenticar
Cuando validás el token y cargás el UserDetails, podés traer el rol desde la DB o directamente desde el JWT, como hicimos antes.

💥 ¿Y CustomUserDetailsService?
En tu clase CustomUserDetailsService, si la usás, podés hacer esto:

java
Copiar
Editar
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority(user.getRole())) // 👈 Convertís el rol a autoridad
    );
}
✅ Conclusión
Sí, para trabajar con roles:

Tenés que guardar el rol en la base de datos (entidad User).

Tenés que permitir asignarlo al registrarse.

Y luego usarlo al generar y validar el token.




Perfecto, ¡vamos a integrar los roles del JWT con Spring Security para que funcionen como GrantedAuthority! 🔐

💡 Objetivo:
Queremos que cuando alguien haga una request con un JWT que tiene el claim "role": "ROLE_ADMIN", Spring Security lo detecte como una autoridad y se puedan aplicar restricciones como:

java
Copiar
Editar
@PreAuthorize("hasRole('ADMIN')")
✅ Paso a paso
1. 🚀 Generar el token con el rol
En tu clase JwtUtil (o donde generás el token):

java
Copiar
Editar
public String generateToken(String username, String role) {
    return Jwts.builder()
        .setSubject(username)
        .claim("role", role) // 👈 agregás el rol
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
}
2. 🔍 Leer el rol desde el token
Agregá este método en tu JwtUtil:

java
Copiar
Editar
public String extractRole(String token) {
    return extractAllClaims(token).get("role", String.class);
}
3. 🛡️ Convertir ese rol en una autoridad de Spring Security
En tu JwtFilter, donde validás el token:

java
Copiar
Editar
if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (jwtUtil.isTokenValid(token, userDetails)) {
        String role = jwtUtil.extractRole(token);

        // 🔁 Convertir a una lista de GrantedAuthority
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities); // 👈 pasás los authorities

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
🔥 Si el token incluye múltiples roles, también podés mapearlos con roles.stream().map(SimpleGrantedAuthority::new).

4. 🧪 Probar con anotaciones
Ahora podés usar anotaciones como:

java
Copiar
Editar
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public ResponseEntity<String> getAdminPanel() {
    return ResponseEntity.ok("Bienvenido al panel de admin");
}
⚠️ Nota: hasRole('ADMIN') busca "ROLE_ADMIN" como GrantedAuthority. Asegurate de agregar el prefijo "ROLE_" si lo usás así.