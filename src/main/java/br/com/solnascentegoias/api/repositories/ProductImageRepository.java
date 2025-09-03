package br.com.solnascentegoias.api.repositories;

import br.com.solnascentegoias.api.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository <ProductImage, Long>{
}
