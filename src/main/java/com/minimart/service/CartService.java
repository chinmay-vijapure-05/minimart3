package com.minimart.service;

import com.minimart.model.Cart;
import com.minimart.model.Product;
import com.minimart.model.User;
import com.minimart.repository.CartRepository;
import com.minimart.repository.ProductRepository;
import com.minimart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        Cart cart = Cart.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();
        return cartRepository.save(cart);
    }

    public List<Cart> getCartByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return cartRepository.findByUser(user);
    }

    public void removeCartItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
