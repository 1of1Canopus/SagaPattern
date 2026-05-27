package io.housedevinci.sagapattern.choreography.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_stock")
public class ProductStock {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(nullable = false)
    private int quantity;

    protected ProductStock() {}

    public ProductStock(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }

    public boolean reserve(int amount) {
        if (amount > quantity) return false;
        quantity -= amount;
        return true;
    }
}
