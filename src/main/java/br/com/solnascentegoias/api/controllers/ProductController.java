package br.com.solnascentegoias.api.controllers;

import br.com.solnascentegoias.api.dtos.ProductRequestDTO;
import br.com.solnascentegoias.api.dtos.ProductResponseDTO;
import br.com.solnascentegoias.api.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        try {
            ProductResponseDTO product = productService.findProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // A anotação @PostMapping aqui define que este método SÓ ACEITA requisições 'multipart/form-data'
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestPart("product") @Valid ProductRequestDTO productDTO,
            @RequestPart("images") List<MultipartFile> images) {

        if (images == null || images.isEmpty() || images.stream().anyMatch(MultipartFile::isEmpty)) {
            return ResponseEntity.badRequest().build();
        }

        ProductResponseDTO createdProduct = productService.createProduct(productDTO, images);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);

            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}