package com.egg.libraryapi.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.egg.libraryapi.models.ProductDTO;

import java.util.List;

@Service
public class ProductService {

    private final WebClient webClient;

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ProductDTO> getAllProducts() {
        return webClient.get()
                .uri("/products") // Ruta relativa
                .retrieve()
                .bodyToFlux(ProductDTO.class) // Viene un array
                .collectList() // Convertimos a List<ProductDTO>
                .block(); // Esperamos la respuesta (síncrono)
    }

    public ProductDTO getProduct(String idProduct) {
        return webClient.get()
                .uri("/products/" + idProduct) // Ruta relativa
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block(); // Esperamos la respuesta (síncrono)
    }
}
