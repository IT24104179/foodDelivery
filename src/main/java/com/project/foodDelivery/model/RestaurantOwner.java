package com.project.foodDelivery.model;

public class RestaurantOwner extends User {
    public RestaurantOwner(String id, String username, String email, String password, String address, String phoneNumber) {
        super(id, username, email, password, "owner", address, phoneNumber);
    }
}
