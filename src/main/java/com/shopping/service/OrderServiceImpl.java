package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private final OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());

        List<com.shopping.stubs.order.Order> ordersForUser = orders.stream().map(order ->
            com.shopping.stubs.order.Order.newBuilder()
                    .setUserId(order.getUserId())
                    .setOrderId(order.getOrderId())
                    .setNoOfItems(order.getNumberOfItems())
                    .setTotalAmount(order.getAmount())
                    .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                    .build()).toList();

        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(ordersForUser).build();

        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }


}
