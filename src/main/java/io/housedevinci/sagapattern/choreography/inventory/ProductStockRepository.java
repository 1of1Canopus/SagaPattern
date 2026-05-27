package io.housedevinci.sagapattern.choreography.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, String> {}
