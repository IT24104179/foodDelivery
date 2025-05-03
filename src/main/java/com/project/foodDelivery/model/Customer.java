package com.project.foodDelivery.model;

public class Customer extends User {
    public Customer(String id, String username, String email, String password, String address, String phoneNumber) {
        super(id, username, email, password, "customer", address, phoneNumber);
    }
}
