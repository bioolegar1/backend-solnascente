package br.com.solnascentegoias.api.dtos;

import br.com.solnascentegoias.api.entities.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "O nome da cetegoria é obrigatório.")
    private String name;

    public   CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
