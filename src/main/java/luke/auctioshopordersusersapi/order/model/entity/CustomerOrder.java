package luke.auctioshopordersusersapi.order.model.entity;


import luke.auctioshopordersusersapi.order.model.dto.CustomerOrderRequest;
import luke.auctioshopordersusersapi.order.model.embeddable.CartItem;
import luke.auctioshopordersusersapi.user.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "customer_order")
@NamedQueries({
        @NamedQuery(name = "CustomerOrder.getCustomerOrderByUserId",
                query = "Select o from CustomerOrder o where o.user.id = ?1"),
        @NamedQuery(name = "CustomerOrder.getCustomerOrderByOrderId",
                query = "Select o from CustomerOrder o where o.orderId = ?1"),
        @NamedQuery(name = "CustomerOrder.deleteCustomerFromCustomerOrderByUserId",
                query = "Delete from Customer c where c.customerId in " +
                        "(Select o.customer.customerId from CustomerOrder o where o.user.id =?1)"),
        @NamedQuery(name = "CustomerOrder.deleteCustomerOrderByUserId",
                query = "Delete from CustomerOrder o where o.orderId in " +
                        "(Select o.orderId from CustomerOrder o where o.user.id =?1)")
})
public class CustomerOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<CartItem> cartItems;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CustomerOrder() {
    }

    public CustomerOrder(List<CartItem> items, CustomerOrderRequest request) {
        this.cartItems = new LinkedList<>(items);
        this.totalPrice = request.getTotalPrice();
        this.totalQuantity = request.getTotalQuantity();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "orderId=" + orderId +
                ", cartItems=" + cartItems +
                ", totalPrice=" + totalPrice +
                ", totalQuantity=" + totalQuantity +
                ", customer=" + customer +
                ", user=" + user +
                '}';
    }
}
