package luke.auctioshopordersusersapi.order;

import luke.auctioshopordersusersapi.order.model.dto.CustomerOrderRequest;
import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import luke.auctioshopordersusersapi.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * OrderController().getAll() -> only for ROLE_ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<CustomerOrder>> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerOrder> customerOrderPage = orderService.getAllPageable(pageable);
        return ResponseEntity.ok().body(customerOrderPage);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority('ADMIN') or returnObject.body.user.username == authentication.name")
    public ResponseEntity<CustomerOrder> getOrderByOrderId(@PathVariable Long id){
        CustomerOrder order = orderService.getOrder(id);
        return ResponseEntity.ok().body(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteOrderByOrderId(@PathVariable Long id){
        orderService.deleteCustomerOrderByOrderId(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<CustomerOrder> saveOrder(@Valid @RequestBody CustomerOrderRequest orderRequest){
        CustomerOrder order = orderService.addOrder(orderRequest);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(order.getOrderId())
                .toUri();

        return ResponseEntity.created(uri).body(order);
    }
}
