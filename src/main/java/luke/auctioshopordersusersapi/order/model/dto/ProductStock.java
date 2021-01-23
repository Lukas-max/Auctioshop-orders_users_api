package luke.auctioshopordersusersapi.order.model.dto;

import java.util.Objects;

public class ProductStock {

    private Long productId;
    private boolean active;
    private int unitsInStock;

    public ProductStock() {
    }

    public ProductStock(Product product) {
        this.productId = product.getProductId();
        this.active = product.isActive();
        this.unitsInStock = product.getUnitsInStock();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getUnitsInStock() {
        return unitsInStock;
    }

    public void setUnitsInStock(int unitsInStock) {
        this.unitsInStock = unitsInStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductStock that = (ProductStock) o;
        return active == that.active &&
                unitsInStock == that.unitsInStock &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, active, unitsInStock);
    }

    @Override
    public String toString() {
        return "ProductStock{" +
                "productId=" + productId +
                ", active=" + active +
                ", unitsInStock=" + unitsInStock +
                '}';
    }
}
