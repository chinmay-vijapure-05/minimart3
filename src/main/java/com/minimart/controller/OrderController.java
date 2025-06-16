package com.minimart.controller;

import com.minimart.model.Order;
import com.minimart.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder/{userId}")
    public Order placeOrder(@PathVariable Long userId) {
        return orderService.placeOrder(userId);
    }

    @GetMapping("/getOrders/{userId}")
    public List<Order> getOrders(@PathVariable Long userId) {
        return orderService.getOrderByUser(userId);
    }
}
