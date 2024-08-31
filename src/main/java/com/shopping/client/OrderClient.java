package com.shopping.client;

import com.shopping.stubs.order.Order;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.Channel;

import java.util.List;

public class OrderClient {
    // get a stub object
    // call service method

    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    public OrderClient(Channel channel) {
        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public List<Order> getOrders(int userId) {
        OrderRequest orderRequest = OrderRequest
                .newBuilder()
                .setUserId(userId)
                .build();
        OrderResponse orderResponse = orderServiceBlockingStub.getOrdersForUser(orderRequest);
        return orderResponse.getOrderList();
    }
}
