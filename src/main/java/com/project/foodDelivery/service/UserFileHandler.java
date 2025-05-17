package com.project.foodDelivery.service;


import com.project.foodDelivery.model.Customer;
import com.project.foodDelivery.model.RestaurantOwner;
import com.project.foodDelivery.model.User;

import java.io.*; 
import java.util.ArrayList;
import java.util.List; 

public class UserFileHandler {
    private static final String FILE_PATH = "users.txt"; 

    
    public static void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) { 
            writer.write(user.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static List<User> readAllUsers() {
        List<User> users = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 7) continue; 
                String id       = p[0];
                String userName = p[1];
                String email    = p[2];
                String pwd      = p[3];
                String role     = p[4];
                String addr     = p[5];
                String phone    = p[6];

                if ("owner".equals(role)) {
                    users.add(new RestaurantOwner(id, userName, email, pwd, addr, phone));
                } else {// default = customer
                    users.add(new Customer(id, userName, email, pwd, addr, phone));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    
    public static User findUserById(String id) {
        return readAllUsers().stream()
                .filter(u -> u.getId().equals(id)) 
                .findFirst() 
                .orElse(null);
    }
    
    public static void updateUser(User updatedUser) {
        List<User> users = readAllUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                if (user.getId().equals(updatedUser.getId())) {
                    writer.write(updatedUser.toFileString());
                } else {
                    writer.write(user.toFileString());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteUser(String id) {
        List<User> users = readAllUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                if (!user.getId().equals(id)) {
                    writer.write(user.toFileString()); 
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

