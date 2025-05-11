package com.project.foodDelivery.service;


import com.project.foodDelivery.model.Customer;
import com.project.foodDelivery.model.RestaurantOwner;
import com.project.foodDelivery.model.User;

import java.io.*; // bufferWriter/reader,filewritter/reader,IOexception
import java.util.ArrayList;
import java.util.List; //List and stream

public class UserFileHandler {
    private static final String FILE_PATH = "users.txt"; // since this is static,don't need to make an object to access it

    // create method
    public static void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) { //append mode, meaning new data is added to the end of the file instead of overwriting it
            writer.write(user.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read method
    public static List<User> readAllUsers() {
        List<User> users = new ArrayList<>(); //Creates an empty ArrayList to store User objects
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {//Reads the file line by line until reaching the end
                String[] p = line.split(",", -1);//Splits each line by comma and put them into an array.-1 means spaces are included.
                if (p.length < 7) continue; // it skips lines which has fields less than 7 to avoid errors

                String id       = p[0];
                String userName = p[1];
                String email    = p[2];
                String pwd      = p[3];
                String role     = p[4];
                String addr     = p[5];
                String phone    = p[6];

                if ("restaurant".equals(role)) {
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

    //find method
    public static User findUserById(String id) {
        return readAllUsers().stream()//Converts the list of users into a stream so we can search through it easily.
                .filter(u -> u.getId().equals(id)) //this has a lambda expression similar to arrow function
                .findFirst() //  find the first matching user
                .orElse(null);
    }
    //update method
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
    //delete method
    public static void deleteUser(String id) {
        List<User> users = readAllUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                if (!user.getId().equals(id)) {
                    writer.write(user.toFileString()); //overwrite all users except the one we want to delete.
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

