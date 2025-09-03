package br.com.solnascentegoias.api.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String desciption;

    // cascade = CascadeType.ALL: Se salvarmos/deletarmos um produto, as imagens associadas também serão.
    // orphanRemoval = true: Se removermos uma imagem da lista deste produto, ela será deletada do banco.
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //Metodo auxiliar para sincronizar a relação
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }


}
