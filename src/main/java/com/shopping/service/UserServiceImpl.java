package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.OrderDao;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserDao userDao = new UserDao();
        User user = userDao.getDetails(request.getUsername());

        UserResponse.Builder userResponseBuilder =
                UserResponse.newBuilder()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setGender(Gender.valueOf(user.getGender()))
                        .setAge(user.getAge());

        userResponseBuilder.setNoOfOrders(getOrders(user).size());

        responseObserver.onNext(userResponseBuilder.build());
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(User user) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052 ).usePlaintext().build();
        OrderClient orderClient = new OrderClient(channel);
        List<Order> orders = orderClient.getAllOrders(user.getId());

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Channel did not terminate", e);
        }
        return orders;
    }
}
