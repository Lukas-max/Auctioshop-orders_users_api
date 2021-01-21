package luke.auctioshopordersusersapi.order.service;

import luke.auctioshopordersusersapi.order.model.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long>{
}
