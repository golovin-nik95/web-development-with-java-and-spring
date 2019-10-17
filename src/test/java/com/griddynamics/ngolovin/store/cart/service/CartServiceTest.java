package com.griddynamics.ngolovin.store.cart.service;

import com.griddynamics.ngolovin.store.StoreTestUtils;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.exception.CartItemAlreadyExistsException;
import com.griddynamics.ngolovin.store.cart.exception.CartItemNotFoundException;
import com.griddynamics.ngolovin.store.cart.exception.InsufficientProductQuantityException;
import com.griddynamics.ngolovin.store.cart.exception.ProductNotFoundException;
import com.griddynamics.ngolovin.store.cart.web.dto.AddCartItemDto;
import com.griddynamics.ngolovin.store.cart.web.dto.UpdateCartItemDto;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import com.griddynamics.ngolovin.store.product.service.ProductService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private Cart cart;

    @Mock
    private ProductService productService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<Long, CartItem> cartItems;

    @Before
    public void setUp() {
        cartItems = StoreTestUtils.createCartItems();
        when(cart.getItems())
                .then(i -> cartItems);
    }

    @Test
    public void getCartTest() {
        Cart expected = cart;

        Cart actual = cartService.getCart();

        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(productService);
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void addCartItemTest() {
        ProductEntity product = getProductNotInCartItems(cartItems);
        Long productId = product.getId();
        long quantity = product.getAvailable() / 2;
        AddCartItemDto addCartItemDto = new AddCartItemDto(productId, quantity);
        when(productService.getProductById(productId))
                .thenReturn(Optional.of(product));

        CartItem actual = cartService.addCartItem(addCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart, times(2)).getItems();
        verifyNoMoreInteractions(cart);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
        assertThat(actual).isNotNull();
        assertThat(actual.getProduct()).isEqualTo(product);
        assertThat(actual.getQuantity()).isEqualTo(quantity);
        assertThat(cartItems).contains(Map.entry(productId, actual));
    }

    @Test
    public void addCartItem_CartItemAlreadyExistsTest() {
        Long productId = cartItems.keySet().iterator().next();
        AddCartItemDto addCartItemDto = new AddCartItemDto(productId, 1L);
        thrown.expect(CartItemAlreadyExistsException.class);

        cartService.addCartItem(addCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void addCartItem_ProductNotFoundTest() {
        Long productId = getProductNotInCartItems(cartItems).getId();
        AddCartItemDto addCartItemDto = new AddCartItemDto(productId, 1L);
        when(productService.getProductById(productId))
                .thenReturn(Optional.empty());
        thrown.expect(ProductNotFoundException.class);

        cartService.addCartItem(addCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void addCartItem_InsufficientProductQuantityTest() {
        ProductEntity product = getProductNotInCartItems(cartItems);
        Long productId = product.getId();
        long quantity = product.getAvailable() + 1;
        AddCartItemDto addCartItemDto = new AddCartItemDto(productId, quantity);
        when(productService.getProductById(productId))
                .thenReturn(Optional.of(product));
        thrown.expect(InsufficientProductQuantityException.class);

        cartService.addCartItem(addCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void updateCartItemTest() {
        CartItem cartItem = cartItems.values().iterator().next();
        ProductEntity product = cartItem.getProduct();
        Long productId = product.getId();
        long quantity = cartItem.getQuantity() + 1;
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(productId, quantity);
        when(productService.getProductById(productId))
                .thenReturn(Optional.of(product));

        CartItem actual = cartService.updateCartItem(updateCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart, times(2)).getItems();
        verifyNoMoreInteractions(cart);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
        assertThat(actual).isNotNull();
        assertThat(actual.getProduct()).isEqualTo(product);
        assertThat(actual.getQuantity()).isEqualTo(quantity);
        assertThat(cartItems).contains(Map.entry(productId, actual));
    }

    @Test
    public void updateCartItem_CartItemNotFoundTest() {
        Long productId = getProductNotInCartItems(cartItems).getId();
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(productId, 1L);
        thrown.expect(CartItemNotFoundException.class);

        cartService.updateCartItem(updateCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void updateCartItem_ProductNotFoundTest() {
        ProductEntity product = cartItems.values().iterator().next().getProduct();
        Long productId = product.getId();
        long quantity = product.getAvailable() + 1;
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(productId, quantity);
        when(productService.getProductById(productId))
                .thenReturn(Optional.empty());
        thrown.expect(ProductNotFoundException.class);

        cartService.updateCartItem(updateCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void updateCartItem_InsufficientProductQuantityTest() {
        ProductEntity product = cartItems.values().iterator().next().getProduct();
        Long productId = product.getId();
        long quantity = product.getAvailable() + 1;
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(productId, quantity);
        when(productService.getProductById(productId))
                .thenReturn(Optional.of(product));
        thrown.expect(InsufficientProductQuantityException.class);

        cartService.updateCartItem(updateCartItemDto);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void removeCartItemTest() {
        Long productId = cartItems.keySet().iterator().next();

        cartService.removeCartItem(productId);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(productService);
        assertThat(cartItems).doesNotContainKey(productId);
    }

    @Test
    public void removeCartItem_CartItemNotFoundTest() {
        Long productId = getProductNotInCartItems(cartItems).getId();
        thrown.expect(CartItemNotFoundException.class);

        cartService.removeCartItem(productId);

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(productService);
    }

    private ProductEntity getProductNotInCartItems(Map<Long, CartItem> cartItems) {
        return StoreTestUtils.createProducts().stream()
                .filter(product -> !cartItems.containsKey(product.getId()))
                .findAny()
                .orElseThrow(AssertionError::new);
    }
}