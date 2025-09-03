package br.com.solnascentegoias.api.repositories;

import br.com.solnascentegoias.api.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Métodos CRUD básicos já estão inclusos nesta interface.
}
