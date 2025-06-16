package com.minimart.controller;

import com.minimart.model.Cart;
import com.minimart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public Cart addToCart(@RequestParam Long userId,
                          @RequestParam Long productId,
                          @RequestParam int quantity) {

        return cartService.addToCart(userId, productId, quantity);
    }

    @GetMapping("/cartItems/{userId}")
    public List<Cart> cartItems(@PathVariable Long userId) {
        return cartService.getCartByUser(userId);
    }

    @DeleteMapping("/removeCartItem/{cartId}")
    public void removeCartItem(@PathVariable Long cartId) {
        cartService.removeCartItem(cartId);
    }
}
