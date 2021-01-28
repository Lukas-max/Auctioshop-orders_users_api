package luke.auctioshopordersusersapi.order.service;

import luke.auctioshopordersusersapi.order.model.dto.*;
import luke.auctioshopordersusersapi.order.model.embeddable.CartItem;
import luke.auctioshopordersusersapi.order.model.entity.Customer;
import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import luke.auctioshopordersusersapi.feign.ProductClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class FormatCustomerOrderImplTest {

    @Mock
    private ProductClient productClient;
    @InjectMocks
    private FormatCustomerOrderImpl formatCustomerOrder;
    @Captor
    private ArgumentCaptor<Set<ProductStock>> productCaptor;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }


    /**
     * Testing FormatCustomerOrderImpl.getCartItems(CustomerOrderRequest orderRequest).
     * <p>
     * Formatting DTO to CartItem entity object.
     */
    @Test
    void getCartItemsShouldReturnNotEmptyCartItemList() {
        //given
        CustomerOrderRequest orderRequest = getCustomerOrderRequest();

        //when
        List<CartItem> cartItems = formatCustomerOrder.getCartItems(orderRequest);

        //then
        assertAll(
                () -> assertThat(cartItems, not(empty())),
                () -> assertThat(cartItems, hasSize(2)),
                () -> assertThat(cartItems.get(0).getProductId(), equalTo(orderRequest.getCartItems()[0].getProductId())),
                () -> assertThat(cartItems.get(0).getName(), equalTo(orderRequest.getCartItems()[0].getName())),
                () -> assertThat(cartItems.get(0).getQuantity(), equalTo(orderRequest.getCartItems()[0].getQuantity())),
                () -> assertThat(cartItems.get(0).getUnitPrice(), equalTo(orderRequest.getCartItems()[0].getUnitPrice())),
                () -> assertThat(cartItems.get(1).getProductId(), equalTo(orderRequest.getCartItems()[1].getProductId())),
                () -> assertThat(cartItems.get(1).getName(), equalTo(orderRequest.getCartItems()[1].getName())),
                () -> assertThat(cartItems.get(1).getQuantity(), equalTo(orderRequest.getCartItems()[1].getQuantity())),
                () -> assertThat(cartItems.get(1).getUnitPrice(), equalTo(orderRequest.getCartItems()[1].getUnitPrice()))
        );
    }

    /**
     * Testing FormatCustomerOrderImpl.getCustomerObject(CustomerOrderRequest orderRequest).
     * <p>
     * Formatting CustomerDto to Customer entity.
     */
    @Test
    void getCustomerObjectShouldReturnAValidCustomer() {
        //given
        CustomerOrderRequest orderRequest = getCustomerOrderRequest();

        //when
        Customer customer = formatCustomerOrder.getCustomerObject(orderRequest);

        //then
        assertAll(
                () -> assertThat(customer, not(sameInstance(orderRequest.getCustomer()))),
                () -> assertThat(customer.getFirstName(), equalTo(orderRequest.getCustomer().getFirstName())),
                () -> assertThat(customer.getLastName(), equalTo(orderRequest.getCustomer().getLastName())),
                () -> assertThat(customer.getTelephone(), equalTo(orderRequest.getCustomer().getTelephone())),
                () -> assertThat(customer.getEmail(), equalTo(orderRequest.getCustomer().getEmail())),
                () -> assertThat(customer.getCountry(), equalTo(orderRequest.getCustomer().getCountry())),
                () -> assertThat(customer.getStreet(), equalTo(orderRequest.getCustomer().getStreet())),
                () -> assertThat(customer.getHouseNumber(), equalTo(orderRequest.getCustomer().getHouseNumber())),
                () -> assertThat(customer.getApartmentNumber(), equalTo(orderRequest.getCustomer().getApartmentNumber())),
                () -> assertThat(customer.getPostalCode(), equalTo(orderRequest.getCustomer().getPostalCode())),
                () -> assertThat(customer.getCity(), equalTo(orderRequest.getCustomer().getCity()))
        );

        //telephone and address were not set by the "customer"
        assertAll(
                () -> assertThat(customer.getTelephone(), is(nullValue())),
                () -> assertThat(customer.getApartmentNumber(), is(nullValue()))
        );
    }

    /**
     * Testing FormatCustomerOrderImpl.refactorCartItems(List<CartItem> cartItems).
     * <p>
     * Items in cart should not drop if stock levels are higher than purchase. They should stay the same.
     * So there should be no refactoring of the Cart.
     * Also stock should be decrement by the items bought.
     */
    @Test
    void refactorCartItemsShouldNotRefactorItemsWhenStockIsHigh() {
        //given cart items quantity 4 and 6
        List<CartItem> cartItems = getCartItems();
        given(productClient.getProductById(1L)).willReturn(getProductOneWithAboveStockCount());
        given(productClient.getProductById(2L)).willReturn(getProductTwoWithAboveStockCount());

        //when
        boolean isRefactored = formatCustomerOrder.refactorCartItems(cartItems);

        //then item quantity to buy should stay the same (no refactoring). Price also stays the same.
        then(productClient).should(times(2)).updateProductStock(productCaptor.capture());

        assertAll(
                () -> assertThat(cartItems.get(0).getQuantity(), equalTo(4)),
                () -> assertThat(cartItems.get(0).getUnitPrice(), equalTo(BigDecimal.valueOf(49.99))),
                () -> assertThat(cartItems.get(1).getQuantity(), equalTo(6)),
                () -> assertThat(cartItems.get(1).getUnitPrice(), equalTo(BigDecimal.valueOf(199.99))),
                () -> assertThat(isRefactored, is(false))
        );

        ProductStock stock1 = productCaptor.getValue()
                .stream()
                .filter(stock -> stock.getProductId().equals(1L))
                .toArray(ProductStock[]::new)[0];

        ProductStock stock2 = productCaptor.getValue()
                .stream()
                .filter(stock -> stock.getProductId().equals(2L))
                .toArray(ProductStock[]::new)[0];

        assertAll(
                () -> assertThat(productCaptor.getValue().size(), equalTo(2)),
                () -> assertThat(stock1.isActive(), is(true)),
                () -> assertThat(stock1.getUnitsInStock(), equalTo(6)),
                () -> assertThat(stock2.isActive(), is(true)),
                () -> assertThat(stock2.getUnitsInStock(), equalTo(5))
        );
    }

    /**
     * Testing FormatCustomerOrderImpl.refactorCartItems(List<CartItem> cartItems).
     * <p>
     * Items in cart should drop to the levels equal to stock level. You can't buy more that in stock.
     * So there should be refactoring of the Cart.
     * Also stock should be decrement by the items bought and if item in stock drops to 0, it should be set to
     * not active.
     */
    @Test
    void refactorCartItemsShouldRefactorAndDecreaseItemsWhenStockToLow() {
        //given cart items quantity 4 and 6
        List<CartItem> cartItems = getCartItems();
        given(productClient.getProductById(1L)).willReturn(getProductOneWithUnderStockCount());
        given(productClient.getProductById(2L)).willReturn(getProductTwoWithUnderStockCount());

        //when
        boolean isRefactored = formatCustomerOrder.refactorCartItems(cartItems);

        //then item quantity to buy should drop down, cause of low stock. Price stays the same.
        then(productClient).should(times(2)).updateProductStock(productCaptor.capture());

        assertAll(
                () -> assertThat(cartItems.get(0).getQuantity(), is(1)),
                () -> assertThat(cartItems.get(0).getUnitPrice(), equalTo(BigDecimal.valueOf(49.99))),
                () -> assertThat(cartItems.get(1).getQuantity(), is(2)),
                () -> assertThat(cartItems.get(1).getUnitPrice(), equalTo(BigDecimal.valueOf(199.99))),
                () -> assertThat(isRefactored, is(true))
        );

        ProductStock stock1 = productCaptor.getValue()
                .stream()
                .filter(stock -> stock.getProductId().equals(1L))
                .toArray(ProductStock[]::new)[0];

        ProductStock stock2 = productCaptor.getValue()
                .stream()
                .filter(stock -> stock.getProductId().equals(2L))
                .toArray(ProductStock[]::new)[0];

        // stock is low, it will buy out all the items, and set them inactive
        assertAll(
                () -> assertThat(productCaptor.getValue().size(), is(equalTo(2))),
                () -> assertThat(stock1.isActive(), is(false)),
                () -> assertThat(stock1.getUnitsInStock(), equalTo(0)),
                () -> assertThat(stock2.isActive(), is(false)),
                () -> assertThat(stock2.getUnitsInStock(), equalTo(0))
        );
    }

    /**
     * Testing FormatCustomerOrderImpl.getCustomerOrder(List<CartItem> items, CustomerOrderRequest orderRequest).
     *
     * If there was no refactoring of the Cart and items stay at the same level, also total quantity and price
     * should not change either.
     */
    @Test
    void getCustomerOrderShouldNotRecountPriceAndQuantityIfNotRefactored(){
        //given
        CustomerOrderRequest orderRequest = getCustomerOrderRequest();
        List<CartItem> cartItems = formatCustomerOrder.getCartItems(orderRequest);
        given(productClient.getProductById(1L)).willReturn(getProductOneWithAboveStockCount());
        given(productClient.getProductById(2L)).willReturn(getProductTwoWithAboveStockCount());

        //when
        CustomerOrder order = formatCustomerOrder.getCustomerOrder(cartItems, orderRequest);

        //then
        assertAll(
                () -> assertThat(order.getTotalPrice(), equalTo(orderRequest.getTotalPrice())),
                () -> assertThat(order.getTotalQuantity(), equalTo(orderRequest.getTotalQuantity())),
                () -> assertThat(order.getCartItems().size(), equalTo(orderRequest.getCartItems().length))
        );
    }

    /**
     * Testing FormatCustomerOrderImpl.getCustomerOrder(List<CartItem> items, CustomerOrderRequest orderRequest).
     * <p>
     * If the Cart was refactored so should total quantity and price be recounted and drop lower. Item count
     * should drop to the level of item that was in stock.
     */
    @Test
    void getCustomerOrderShouldRecountPriceAndQuantityIfRefactored() {
        //given
        CustomerOrderRequest orderRequest = getCustomerOrderRequestWithTooManyProductsBought();
        List<CartItem> cartItems = formatCustomerOrder.getCartItems(orderRequest);
        given(productClient.getProductById(1L)).willReturn(getProductOneWithUnderStockCount());
        given(productClient.getProductById(2L)).willReturn(getProductTwoWithUnderStockCount());

        //when
        CustomerOrder order = formatCustomerOrder.getCustomerOrder(cartItems, orderRequest);

        //then
        assertAll(
                () -> assertThat(order.getTotalPrice(), lessThan(orderRequest.getTotalPrice())),
                () -> assertThat(order.getTotalQuantity(), lessThan(orderRequest.getTotalQuantity())),
                () -> assertThat(order.getCartItems().get(0).getQuantity(), equalTo(1)),
                () -> assertThat(order.getCartItems().get(1).getQuantity(), equalTo(2)),
                () -> assertThat(order.getTotalQuantity(), equalTo(3)),
                () -> assertThat(order.getTotalPrice(), equalTo(BigDecimal.valueOf(449.97)))
        );
    }


    /**
     * Helper methods for creating fake order from customer.
     * <p>
     * First five methods create Dto objects.
     */
    private CustomerOrderRequest getCustomerOrderRequest() {
        CustomerOrderRequest request = new CustomerOrderRequest();
        request.setCustomer(getCustomerDto());
        request.setCartItems(getCartItemValidateDtoArray());
        request.setTotalQuantity(3);
        request.setTotalPrice(BigDecimal.valueOf(449.97));
        return request;
    }

    private CustomerOrderRequest getCustomerOrderRequestWithTooManyProductsBought() {
        CustomerOrderRequest request = new CustomerOrderRequest();
        request.setCustomer(getCustomerDto());
        request.setCartItems(getCartItemValidateDtoArrayWithToManyProducts());
        request.setTotalQuantity(30);
        request.setTotalPrice(BigDecimal.valueOf(3749.7));
        return request;
    }

    private CustomerDto getCustomerDto() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("Marek");
        customerDto.setLastName("Jagniak");
        customerDto.setEmail("mojmail@gmail.com");
        customerDto.setCountry("Poland");
        customerDto.setHouseNumber(47);
        customerDto.setPostalCode("00-101");
        customerDto.setCity("Kuźnice");
        return customerDto;
    }

    private CartItemValidateDto[] getCartItemValidateDtoArray() {
        CartItemValidateDto item1 = new CartItemValidateDto();
        item1.setProductId(1L);
        item1.setName("God of War 4");
        item1.setUnitPrice(BigDecimal.valueOf(49.99));
        item1.setQuantity(1);

        CartItemValidateDto item2 = new CartItemValidateDto();
        item2.setProductId(2L);
        item2.setName("Final Fantasy VII Remake");
        item2.setUnitPrice(BigDecimal.valueOf(199.99));
        item2.setQuantity(2);

        return new CartItemValidateDto[]{item1, item2};
    }

    private CartItemValidateDto[] getCartItemValidateDtoArrayWithToManyProducts() {
        CartItemValidateDto item1 = new CartItemValidateDto();
        item1.setProductId(1L);
        item1.setName("God of War 4");
        item1.setUnitPrice(BigDecimal.valueOf(49.99));
        item1.setQuantity(15);

        CartItemValidateDto item2 = new CartItemValidateDto();
        item2.setProductId(2L);
        item2.setName("Final Fantasy VII Remake");
        item2.setUnitPrice(BigDecimal.valueOf(199.99));
        item2.setQuantity(15);

        return new CartItemValidateDto[]{item1, item2};
    }

    private List<CartItem> getCartItems() {
        CartItem cartItem1 = new CartItem();
        cartItem1.setProductId(1L);
        cartItem1.setName("God of War 4");
        cartItem1.setUnitPrice(BigDecimal.valueOf(49.99));
        cartItem1.setQuantity(4);

        CartItem cartItem2 = new CartItem();
        cartItem2.setProductId(2L);
        cartItem2.setName("Final Fantasy VII Remake");
        cartItem2.setUnitPrice(BigDecimal.valueOf(199.99));
        cartItem2.setQuantity(6);

        return new ArrayList<>(List.of(cartItem1, cartItem2));
    }

    /**
     * This two methods are for mock response. They simulate the database return of products, when the database/stock
     * item count is higher than customer order quantity.
     */
    private ResponseEntity<Product> getProductOneWithAboveStockCount() {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setUnitsInStock(10);
        product1.setActive(true);
        return ResponseEntity.ok().body(product1);
    }

    private ResponseEntity<Product> getProductTwoWithAboveStockCount() {
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setUnitsInStock(11);
        product2.setActive(true);
        return ResponseEntity.ok().body(product2);
    }

    /**
     * This two methods are for mock response. They simulate the database return of products, when the database/stock
     * item count is lower than customer order quantity.
     */
    private ResponseEntity<Product> getProductOneWithUnderStockCount() {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setUnitsInStock(1);
        product1.setActive(true);
        return ResponseEntity.status(HttpStatus.OK).body(product1);
    }

    private ResponseEntity<Product> getProductTwoWithUnderStockCount() {
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setUnitsInStock(2);
        product2.setActive(true);
        return ResponseEntity.status(HttpStatus.OK).body(product2);
    }
}
