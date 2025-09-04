package br.com.solnascentegoias.api.dtos;

import br.com.solnascentegoias.api.entities.Product;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<ImageInfoDTO> images;
    private CategoryDTO category;

    //converte uma Entity em DTO
    public static ProductResponseDTO fromEntity(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImages(
            product.getImages().stream()
                    .map(ImageInfoDTO::fromEntity)
                    .sorted(Comparator.comparing(ImageInfoDTO::getDisplayOrder))
                    .toList()
                );

        if (product.getCategory() != null) {
            dto.setCategory(new CategoryDTO(product.getCategory()));
        }
        return dto;
    }





}
