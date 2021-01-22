package luke.auctioshopordersusersapi.order.model.dto;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ProductRequest {

    private Long productId;
    @NotEmpty
    @Size(max = 255, message = "Pole SKU nie może mieć więcej niż 255 znaków")
    private String sku;
    @NotEmpty
    @Size(max = 255, message = "Pole nazwa produktu nie może mieć więcej niż 255 znaków")
    private String name;
    private String description;
    @NotNull(message = "Cena musi być podana")
    @Min(value = 0, message = "Cena nie może być mniejsza niż zero")
    private BigDecimal unitPrice;
    private String productImage;
    private boolean active;
    @NotNull(message = "Pole ilość produktów musi być wypełnione")
    @Min(value = 0, message = "Najmniejsza ilość przedmiotów wynosi 0")
    private int unitsInStock;
    @NotNull(message = "Nie przesłano czasu stworzenia produktu")
    private Timestamp dateTimeCreated;
    private Timestamp dateTimeUpdated;
    @NotNull(message = "Nie przesłano kategorii produktu")
    private Long productCategoryId;

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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
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

    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "productId=" + productId +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", productImage='" + productImage + '\'' +
                ", active=" + active +
                ", unitsInStock=" + unitsInStock +
                ", dateTimeCreated=" + dateTimeCreated +
                ", dateTimeUpdated=" + dateTimeUpdated +
                ", productCategoryId=" + productCategoryId +
                '}';
    }
}
