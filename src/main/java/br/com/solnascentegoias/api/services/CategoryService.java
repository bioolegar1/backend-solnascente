package br.com.solnascentegoias.api.services;

import br.com.solnascentegoias.api.dtos.CategoryDTO;
import br.com.solnascentegoias.api.entities.Category;
import br.com.solnascentegoias.api.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setName(dto.getName());
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }
}
