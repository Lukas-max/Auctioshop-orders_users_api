package luke.auctioshopordersusersapi.order.service;

import luke.auctioshopordersusersapi.order.model.dto.CustomerOrderRequest;
import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<CustomerOrder> getAllPageable(Pageable pageable);

    CustomerOrder getOrder(Long id);

    void deleteCustomerOrderByOrderId(Long id);

    CustomerOrder addOrder(CustomerOrderRequest orderRequest);
}
