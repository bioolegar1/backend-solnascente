//criação e atualização de produtos


package br.com.solnascentegoias.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "O nome não pode ser vazio.")
    @Size(max = 150, message = "O nome pode ter no máximo 150 caracteres.")
    private String name;

    private String description;


    @NotNull(message = "O ID da categoria é obrigatório.")
    private Long categoryId;
}
