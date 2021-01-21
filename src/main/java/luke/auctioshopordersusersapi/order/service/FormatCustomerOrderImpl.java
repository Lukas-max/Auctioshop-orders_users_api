package luke.auctioshopordersusersapi.order.service;

import luke.auctioshopordersusersapi.order.model.dto.Product;
import luke.auctioshopordersusersapi.ProductRepository;
import luke.auctioshopordersusersapi.order.model.dto.CustomerOrderRequest;
import luke.auctioshopordersusersapi.order.model.embeddable.CartItem;
import luke.auctioshopordersusersapi.order.model.entity.Customer;
import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class FormatCustomerOrderImpl implements FormatCustomerOrder {

    private final ProductRepository productRepository;

    public FormatCustomerOrderImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Maps the CartItemValidateDto class from CustomerOrderRequest to CartItem. Class composed to CustomerOrder
     * during persisting data.
     */
    public List<CartItem> getCartItems(CustomerOrderRequest orderRequest) {
        List<CartItem> items = new LinkedList<>();
        Arrays.stream(orderRequest.getCartItems())
                .forEach(i -> items.add(new CartItem(i)));

        return items;
    }

    /**
     * This two methods map customer data to Customer class.
     * After this the entity Customer is ready to be persisted.
     */
    public Customer getCustomerObject(CustomerOrderRequest orderRequest) {
        return new Customer(
                orderRequest.getCustomer().getFirstName(),
                orderRequest.getCustomer().getLastName(),
                orderRequest.getCustomer().getTelephone(),
                orderRequest.getCustomer().getEmail(),
                orderRequest.getCustomer().getCountry(),
                orderRequest.getCustomer().getStreet(),
                orderRequest.getCustomer().getHouseNumber(),
                orderRequest.getCustomer().getApartmentNumber(),
                orderRequest.getCustomer().getPostalCode(),
                orderRequest.getCustomer().getCity()
        );
    }

    /**
     * This first refactores the items in the cart, and decrements the database stock
     *                                                -> refactorCartItems(List<CartItem> cartItems).
     * Then it creates CustomerOrder. If the cart has been refactored, the total price and quantity is recounted.
     * After these operations CustomerOrder is ready to be persisted
     */
    public CustomerOrder getCustomerOrder(List<CartItem> items, CustomerOrderRequest orderRequest) {
        boolean isRefactored = refactorCartItems(items);
        CustomerOrder order = new CustomerOrder(items, orderRequest);

        if (isRefactored)
            recountPriceAndQuantity(order);

        return order;
    }

    /**
     * If the number of a purchase item is higher than items available, then decrement the items by setting
     * the quantityToBuy equal with the quantity of items in stock.
     * So if only 2 items in database. QuantityToBuy = 2.
     * After that we decrement the database item count.
     */
    protected synchronized boolean refactorCartItems(List<CartItem> cartItems) {
        boolean isRefactored = false;

        for (CartItem item : cartItems) {
            Product product = getProductById(item.getProductId());
            int quantityToBuy = item.getQuantity();
            int inStock = product.getUnitsInStock();

            if (quantityToBuy > inStock) {
                quantityToBuy = inStock;
                isRefactored = true;

                item.setQuantity(quantityToBuy);
            }

            setUnitsInStock(product, quantityToBuy);
        }
        return isRefactored;
    }

    /**
     * If we bought 5 items of a product we then decrease the items in the stock by 5.
     * If items in stock are set to 0, we set the product inactive.
     */
    private void setUnitsInStock(Product product, int itemsBought){
        product.setUnitsInStock(product.getUnitsInStock() - itemsBought);
        if (product.getUnitsInStock() < 1)
            product.setActive(false);

        productRepository.save(product);
    }

    /**
     * After cart refactoring if the cart item quantity we have to count again total price and quantity.
     */
    private void recountPriceAndQuantity(CustomerOrder order) {
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.valueOf(0);

        for (CartItem i : order.getCartItems()) {
            int quantity = i.getQuantity();
            totalQuantity += quantity;
            totalPrice = totalPrice.add(
                    i.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(quantity)));
        }
        order.setTotalQuantity(totalQuantity);
        order.setTotalPrice(totalPrice);
    }

    private Product getProductById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nie znaleziono produktu o Id: " + id));
    }
}
