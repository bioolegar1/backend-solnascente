package br.com.solnascentegoias.api.dtos;

import br.com.solnascentegoias.api.entities.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoDTO {
    private String imageUrl;
    private int displayOrder;

    public static ImageInfoDTO fromEntity(ProductImage image) {
        return new ImageInfoDTO(image.getImageUrl(), image.getDisplayOrder());
    }
}
