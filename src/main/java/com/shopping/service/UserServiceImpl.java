package com.shopping.service;

import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.user.*;
import io.grpc.stub.StreamObserver;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserDao userDao = new UserDao();
        User user = userDao.getDetails(request.getUsername());

        UserResponse userResponse =
                UserResponse.newBuilder()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setGender(Gender.valueOf(user.getGender()))
                        .setAge(user.getAge()).build();

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
