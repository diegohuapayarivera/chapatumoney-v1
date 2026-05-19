package com.dhr.chapatumoney.controller;

import com.dhr.chapatumoney.dto.request.CreateOrderRequest;
import com.dhr.chapatumoney.dto.response.OrderResponse;
import com.dhr.chapatumoney.dto.response.PagedResponse;
import com.dhr.chapatumoney.entity.OrderStatus;
import com.dhr.chapatumoney.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, auth.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<PagedResponse<OrderResponse>> getMyOrders(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        OrderStatus status = estado != null ? OrderStatus.valueOf(estado) : null;
        return ResponseEntity.ok(orderService.getMyOrders(auth.getName(), status, page, size));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable UUID id,
            Authentication auth) {
        return ResponseEntity.ok(orderService.confirmOrder(id, auth.getName()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id,
            Authentication auth) {
        return ResponseEntity.ok(orderService.cancelOrder(id, auth.getName()));
    }
}
