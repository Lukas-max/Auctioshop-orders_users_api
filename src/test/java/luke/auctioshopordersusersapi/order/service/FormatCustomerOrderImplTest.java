package luke.auctioshopordersusersapi.order.service;

import luke.auctioshopordersusersapi.ProductRepository;
import luke.auctioshopordersusersapi.order.model.dto.CartItemValidateDto;
import luke.auctioshopordersusersapi.order.model.dto.CustomerDto;
import luke.auctioshopordersusersapi.order.model.dto.CustomerOrderRequest;
import luke.auctioshopordersusersapi.order.model.dto.Product;
import luke.auctioshopordersusersapi.order.model.embeddable.CartItem;
import luke.auctioshopordersusersapi.order.model.entity.Customer;
import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class FormatCustomerOrderImplTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private FormatCustomerOrderImpl formatCustomerOrder;

    @BeforeEach
    public void setupMocks(){
        MockitoAnnotations.openMocks(this);
    }


    /**
     *Testing FormatCustomerOrderImpl.getCartItems(CustomerOrderRequest orderRequest).
     *
     * Formatting DTO to CartItem entity object.
     */
    @Test
    void getCartItemsShouldReturnNotEmptyCartItemList(){
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
     *Testing FormatCustomerOrderImpl.getCustomerObject(CustomerOrderRequest orderRequest).
     *
     * Formatting CustomerDto to Customer entity.
     */
    @Test
    void getCustomerObjectShouldReturnAValidCustomer(){
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
     *
     * Items in cart should not drop if stock levels are higher than purchase. They should stay the same.
     * So there should be no refactoring of the Cart.
     * Also stock should be decrement by the items bought.
     */
    @Test
    void refactorCartItemsShouldNotRefactorItemsWhenStockIsHigh(){
        //given cart items quantity 4 and 6
        List<CartItem> cartItems = getCartItems();
        given(productRepository.findById(1L)).willReturn(Optional.of(getProductOneWithAboveStockCount()));
        given(productRepository.findById(2L)).willReturn(Optional.of(getProductTwoWithAboveStockCount()));
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        //when
        boolean isRefactored = formatCustomerOrder.refactorCartItems(cartItems);

        //then item quantity to buy should stay the same (no refactoring). Price also stays the same.
        then(productRepository).should(times(2)).save(productCaptor.capture());

        assertAll(
                () -> assertThat(cartItems.get(0).getQuantity(), equalTo(4)),
                () -> assertThat(cartItems.get(0).getUnitPrice(), equalTo(BigDecimal.valueOf(49.99))),
                () -> assertThat(cartItems.get(1).getQuantity(), equalTo(6)),
                () -> assertThat(cartItems.get(1).getUnitPrice(), equalTo(BigDecimal.valueOf(199.99))),
                () -> assertThat(isRefactored, is(false))
        );

        assertAll(
                () -> assertThat(productCaptor.getAllValues().get(0).isActive(), is(true)),
                () -> assertThat(productCaptor.getAllValues().get(0).getUnitsInStock(), equalTo(6)),
                () -> assertThat(productCaptor.getAllValues().get(1).isActive(), is(true)),
                () -> assertThat(productCaptor.getAllValues().get(1).getUnitsInStock(), equalTo(5))
        );
    }

    /**
     * Testing FormatCustomerOrderImpl.refactorCartItems(List<CartItem> cartItems).
     *
     * Items in cart should drop to the levels equal to stock level. You can't buy more that in stock.
     * So there should be refactoring of the Cart.
     * Also stock should be decrement by the items bought and if item in stock drops to 0, it should be set to
     * not active.
     */
    @Test
    void refactorCartItemsShouldRefactorAndDecreaseItemsWhenStockToLow(){
        //given cart items quantity 4 and 6
        List<CartItem> cartItems = getCartItems();
        given(productRepository.findById(1L)).willReturn(Optional.of(getProductOneWithUnderStockCount()));
        given(productRepository.findById(2L)).willReturn(Optional.of(getProductTwoWithUnderStockCount()));
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        //when
        boolean isRefactored = formatCustomerOrder.refactorCartItems(cartItems);

        //then item quantity to buy should drop down, cause of low stock. Price stays the same.
        then(productRepository).should(times(2)).save(productCaptor.capture());

        assertAll(
                () -> assertThat(cartItems.get(0).getQuantity(), is(1)),
                () -> assertThat(cartItems.get(0).getUnitPrice(), equalTo(BigDecimal.valueOf(49.99))),
                () -> assertThat(cartItems.get(1).getQuantity(), is(2)),
                () -> assertThat(cartItems.get(1).getUnitPrice(), equalTo(BigDecimal.valueOf(199.99))),
                () -> assertThat(isRefactored, is(true))
        );

        // stock is low, it will buy out all the items, and set them inactive
        assertAll(
                () -> assertThat(productCaptor.getAllValues().get(0).isActive(), is(false)),
                () -> assertThat(productCaptor.getAllValues().get(0).getUnitsInStock(), equalTo(0)),
                () -> assertThat(productCaptor.getAllValues().get(1).isActive(), is(false)),
                () -> assertThat(productCaptor.getAllValues().get(1).getUnitsInStock(), equalTo(0))
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
        given(productRepository.findById(1L)).willReturn(Optional.of(getProductOneWithAboveStockCount()));
        given(productRepository.findById(2L)).willReturn(Optional.of(getProductTwoWithAboveStockCount()));

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
     *
     * If the Cart was refactored so should total quantity and price be recounted and drop lower. Item count
     * should drop to the level of item that was in stock.
     */
    @Test
    void getCustomerOrderShouldRecountPriceAndQuantityIfRefactored(){
        //given
        CustomerOrderRequest orderRequest = getCustomerOrderRequestWithTooManyProductsBought();
        List<CartItem> cartItems = formatCustomerOrder.getCartItems(orderRequest);
        given(productRepository.findById(1L)).willReturn(Optional.of(getProductOneWithUnderStockCount()));
        given(productRepository.findById(2L)).willReturn(Optional.of(getProductTwoWithUnderStockCount()));

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
     *
     * First five methods create Dto objects.
     */
    private CustomerOrderRequest getCustomerOrderRequest(){
        CustomerOrderRequest request = new CustomerOrderRequest();
        request.setCustomer(getCustomerDto());
        request.setCartItems(getCartItemValidateDtoArray());
        request.setTotalQuantity(3);
        request.setTotalPrice(BigDecimal.valueOf(449.97));
        return request;
    }

    private CustomerOrderRequest getCustomerOrderRequestWithTooManyProductsBought(){
        CustomerOrderRequest request = new CustomerOrderRequest();
        request.setCustomer(getCustomerDto());
        request.setCartItems(getCartItemValidateDtoArrayWithToManyProducts());
        request.setTotalQuantity(30);
        request.setTotalPrice(BigDecimal.valueOf(3749.7));
        return request;
    }

    private CustomerDto getCustomerDto(){
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

    private CartItemValidateDto[] getCartItemValidateDtoArray(){
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

    private CartItemValidateDto[] getCartItemValidateDtoArrayWithToManyProducts(){
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

    private List<CartItem> getCartItems(){
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

        return new ArrayList<>(List.of(cartItem1,cartItem2));
    }

    /**
     * This two methods are for mock response. They simulate the database return of products, when the database/stock
     * item count is higher than customer order quantity.
     */
    private Product getProductOneWithAboveStockCount(){
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setUnitsInStock(10);
        product1.setActive(true);
        return  product1;
    }

    private Product getProductTwoWithAboveStockCount(){
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setUnitsInStock(11);
        product2.setActive(true);
        return product2;
    }

    /**
     * This two methods are for mock response. They simulate the database return of products, when the database/stock
     * item count is lower than customer order quantity.
     */
    private Product getProductOneWithUnderStockCount(){
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setUnitsInStock(1);
        product1.setActive(true);
        return product1;
    }

    private Product getProductTwoWithUnderStockCount(){
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setUnitsInStock(2);
        product2.setActive(true);
        return product2;
    }
}
