package luke.auctioshopordersusersapi.order.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

public class Product implements Serializable {

    private Long productId;
    private String sku;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private byte[] productImage;
    private boolean active;
    private int unitsInStock;
    private Timestamp dateTimeCreated;
    private Timestamp dateTimeUpdated;
    private ProductCategory productCategory;

    public Product() {
    }

    private Product(ProductBuilder builder) {
        this.productId = builder.productId;
        this.sku = builder.sku;
        this.name = builder.name;
        this.description = builder.description;
        this.unitPrice = builder.unitPrice;
        this.productImage = builder.productImage;
        this.active = builder.active;
        this.unitsInStock = builder.unitsInStock;
        this.dateTimeCreated = builder.dateTimeCreated;
        this.dateTimeUpdated = builder.dateTimeUpdated;
        this.productCategory = builder.productCategory;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public byte[] getProductImage() {
        return productImage;
    }

    public void setProductImage(byte[] productImage) {
        this.productImage = productImage;
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

    public Timestamp getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(Timestamp dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Timestamp getDateTimeUpdated() {
        return dateTimeUpdated;
    }

    public void setDateTimeUpdated(Timestamp dateTimeUpdated) {
        this.dateTimeUpdated = dateTimeUpdated;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return active == product.active &&
                unitsInStock == product.unitsInStock &&
                Objects.equals(productId, product.productId) &&
                Objects.equals(sku, product.sku) &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(unitPrice, product.unitPrice) &&
                Arrays.equals(productImage, product.productImage) &&
                Objects.equals(dateTimeCreated, product.dateTimeCreated) &&
                Objects.equals(dateTimeUpdated, product.dateTimeUpdated) &&
                Objects.equals(productCategory, product.productCategory);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(productId, sku, name, description, unitPrice, active, unitsInStock,
                dateTimeCreated, dateTimeUpdated, productCategory);
        result = 31 * result + Arrays.hashCode(productImage);
        return result;
    }

    @Override
    public String toString() {
        return "Product[" +
                "productId=" + productId +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", productImage=" + Arrays.toString(productImage) +
                ", active=" + active +
                ", unitsInStock=" + unitsInStock +
                ", dateTimeCreated=" + dateTimeCreated +
                ", dateTimeUpdated=" + dateTimeUpdated +
                ", productCategory=" + productCategory +
                ']';
    }

    public static class ProductBuilder {
        private Long productId;
        private String sku;
        private String name;
        private String description;
        private BigDecimal unitPrice;
        private byte[] productImage;
        private boolean active;
        private int unitsInStock;
        private Timestamp dateTimeCreated;
        private Timestamp dateTimeUpdated;
        private ProductCategory productCategory;

        public Product build() {
            return new Product(this);
        }

        public ProductBuilder buildId(Long productId) {
            this.productId = productId;
            return this;
        }

        public ProductBuilder buildSku(String sku) {
            this.sku = sku;
            return this;
        }

        public ProductBuilder buildName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder buildDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder buildUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public ProductBuilder buildProductImage(byte[] productImage) {
            this.productImage = productImage;
            return this;
        }

        public ProductBuilder buildActive(boolean active) {
            this.active = active;
            return this;
        }

        public ProductBuilder buildUnitsInStock(int unitsInStock) {
            this.unitsInStock = unitsInStock;
            return this;
        }

        public ProductBuilder buildDateTimeCreated(Timestamp dateTimeCreated) {
            this.dateTimeCreated = dateTimeCreated;
            return this;
        }

        public ProductBuilder buildDateTimeUpdated(Timestamp dateTimeUpdated) {
            this.dateTimeUpdated = dateTimeUpdated;
            return this;
        }

        public ProductBuilder buildProductCategory(ProductCategory category) {
            this.productCategory = category;
            return this;
        }
    }
}
