package luke.auctioshopordersusersapi.order.service;

import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@Repository
@Transactional
public interface CustomerOrderRepository extends PagingAndSortingRepository<CustomerOrder, Long> {

    @Query(name = "CustomerOrder.getCustomerOrderByUserId")
    Page<CustomerOrder> getCustomerOrderByUserId(Long id, Pageable pageable);

    @Query(name = "CustomerOrder.getCustomerOrderByOrderId")
    Optional<CustomerOrder> getCustomerOrderByOrderId(Long id);

    @Modifying
    @Query(name = "CustomerOrder.deleteCustomerFromCustomerOrderByUserId")
    void deleteCustomerFromCustomerOrderByUserId(Long Id);

    @Modifying
    @Query(name = "CustomerOrder.deleteCustomerOrderByUserId")
    void deleteCustomerOrderByUserId(Long id);
}
