package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserDao userDao = new UserDao();
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = null;
        try {
            user = userDao.getDetails(request.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UserResponse.Builder userResponseBuilder = UserResponse
                .newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()));

        logger.info("Creating the managed channel and calling the order client");
        // get orders by invoking the client
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50053").usePlaintext().build();
        OrderClient orderClient = new OrderClient(channel);
        List<Order> orders = orderClient.getOrders(userResponseBuilder.getId());

        userResponseBuilder.setNoOfOrders(orders.size());

        // If you want to make multiple calls, keep the channel open otherwise shut the channel
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Failed to shutdown the channel", e);
        }

        UserResponse userResponse = userResponseBuilder.build();

        responseObserver.onNext(userResponse); // Method to return the user response back to the client
        responseObserver.onCompleted(); // This method ensures that the RPC call gets completed
    }
}
