package luke.auctioshopordersusersapi.order.model.embeddable;

import luke.auctioshopordersusersapi.order.model.dto.CartItemValidateDto;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class CartItem {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String name;

    @Column(name = "unit_price_at_bought")
    private BigDecimal unitPrice;

    @Column(name = "quantity")
    private int quantity;

    public CartItem() {
    }

    public CartItem(CartItemValidateDto dto) {
        this.productId = dto.getProductId();
        this.name = dto.getName();
        this.unitPrice = dto.getUnitPrice();
        this.quantity = dto.getQuantity();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPriceAtBought) {
        this.unitPrice = unitPriceAtBought;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}
