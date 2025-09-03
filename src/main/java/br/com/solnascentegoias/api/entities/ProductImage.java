package br.com.solnascentegoias.api.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //lado "dono" da relação. A coluna 'product_id' será criada nesta tabela.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore //evita loops infinitos
    private Product product;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private int displayOrder; // 1 = principal, 2 = nutricional, 3 = outros

    @CreationTimestamp
    private Instant createdAt;
}
