package br.com.solnascentegoias.api.controllers;

import br.com.solnascentegoias.api.dtos.CategoryDTO;
import br.com.solnascentegoias.api.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO dto) {
        CategoryDTO createdCategory = categoryService.create(dto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

}
