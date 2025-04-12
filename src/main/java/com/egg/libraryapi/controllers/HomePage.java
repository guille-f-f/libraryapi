package com.egg.libraryapi.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.egg.libraryapi.entities.Libro;


@Controller
public class HomePage {
    @GetMapping("/")
    public String mostrarHome(Model model) {
        model.addAttribute("titulo", "Biblioteca Central");
        model.addAttribute("subtitulo", "Un espacio para descubrir, aprender y crecer");
        model.addAttribute("descripcion", "Accedé a miles de libros, novedades y recursos desde tu biblioteca local.");
        
        List<Libro> novedades = List.of(
            new Libro("Rayuela", "Una obra maestra de Julio Cortázar", "rayuela.jpg"),
            new Libro("1984", "Distopía clásica de George Orwell", "1984.jpg")
        );
    
        model.addAttribute("novedades", novedades);
        return "index";
    }
}
