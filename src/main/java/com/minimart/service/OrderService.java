package com.minimart.service;

import com.minimart.model.Cart;
import com.minimart.model.Order;
import com.minimart.model.OrderItem;
import com.minimart.model.User;
import com.minimart.repository.CartRepository;
import com.minimart.repository.OrderRepository;
import com.minimart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    public Order placeOrder(Long userId) {
        // get the user for whom the order needs to placed
        User user = userRepository.findById(userId).orElseThrow();

        // gather all the cart items that belong to this user
        List<Cart> cartItems = cartRepository.findByUser(user);

        // Okay if you don't find any items in the cart that belong to this user, then throw an exception stating that the cart is empty
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty!");

        // map product details of each item in the cart to OrderItem object and create a list of them all
        List<OrderItem> orderItems = cartItems.stream().map(cart ->
                OrderItem.builder()
                        .product(cart.getProduct())
                        .quantity(cart.getQuantity())
                        .price(cart.getProduct().getPrice())
                        .build()
        ).collect(Collectors.toList());

        // calculate total price of all the items belonging to this user
        double totalPrice = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // now map the order summary into Order object
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .items(orderItems)
                .build();

        // Okay this thing I don't know its significance, no worries let continue anyway
        // set order reference in each order item
        orderItems.forEach(item -> item.setOrder(order));

        // save order and clear cart
        Order saveOrder = orderRepository.save(order);
        cartRepository.deleteAll(cartItems);

        return saveOrder;
    }

    public List<Order> getOrderByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return orderRepository.findByUser(user);
    }
}
