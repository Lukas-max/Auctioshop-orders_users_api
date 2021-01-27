package luke.auctioshopordersusersapi.feign;

import luke.auctioshopordersusersapi.order.model.dto.Product;
import luke.auctioshopordersusersapi.order.model.dto.ProductRequest;
import luke.auctioshopordersusersapi.order.model.dto.ProductStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@FeignClient(name = "auctioshop-products")
@RequestMapping("/api/products")
public interface ProductClient {

    @GetMapping
    ResponseEntity<Page<Product>> getAllProducts(@RequestParam(name = "page", defaultValue = "0") int pageNo,
                                                 @RequestParam(name = "size", defaultValue = "8") int size);

    @GetMapping(path = "/{id}")
    ResponseEntity<Product> getProductById(@PathVariable("id") Long id);

    @GetMapping(path = "/categoryId")
    ResponseEntity<Page<Product>> getProductsByCategoryId(@RequestParam(name = "categoryId") Long categoryId,
                                                          @RequestParam(name = "page", defaultValue = "0") int pageNo,
                                                          @RequestParam(name = "size", defaultValue = "8") int size);

    @GetMapping(path = "/name")
    ResponseEntity<Page<Product>> getProductsByName(@RequestParam(name = "keyWord") String name,
                                                    @RequestParam(name = "page", defaultValue = "0") int pageNo,
                                                    @RequestParam(name = "size", defaultValue = "8") int size);

    @DeleteMapping(path = "/{id}")
    ResponseEntity<?> deleteById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<Product> saveProduct(@Valid @RequestBody ProductRequest productRequest);

    @PutMapping
    ResponseEntity<Product> updateProduct(@Valid @RequestBody ProductRequest productRequest);

    @PutMapping("/stock")
    ResponseEntity<?> updateProductStock(@RequestBody Set<ProductStock> productStock);
}
