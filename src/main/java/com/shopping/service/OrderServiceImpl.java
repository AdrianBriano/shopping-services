package com.shopping.service;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());

        List<com.shopping.stubs.order.Order> responseOrders =
                orders.stream().map(order -> com.shopping.stubs.order.Order.newBuilder()
                    .setOrderId(order.getOrderId())
                    .setUserId(order.getUserId())
                    .setNoOfItems(order.getNoOfItems())
                    .setTotalAmount(order.getAmount())
                    .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime())).build()).collect(Collectors.toList());

        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(responseOrders).build();
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}
