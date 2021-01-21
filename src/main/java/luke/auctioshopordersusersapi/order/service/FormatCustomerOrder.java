package luke.auctioshopordersusersapi.order.service;


import luke.auctioshopordersusersapi.order.model.dto.CustomerOrderRequest;
import luke.auctioshopordersusersapi.order.model.embeddable.CartItem;
import luke.auctioshopordersusersapi.order.model.entity.Customer;
import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;

import java.util.List;

public interface FormatCustomerOrder {

    List<CartItem> getCartItems(CustomerOrderRequest orderRequest);
    Customer getCustomerObject(CustomerOrderRequest orderRequest);
    CustomerOrder getCustomerOrder(List<CartItem> items, CustomerOrderRequest orderRequest);
}
