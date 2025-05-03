package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Customer;
import com.project.foodDelivery.model.RestaurantOwner;
import com.project.foodDelivery.model.User;
import com.project.foodDelivery.service.UserFileHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {


    //CUSTOMER  REGISTRATION  (role enforced = customer)

    @PostMapping("/register")
    public String registerCustomer(@RequestBody RegisterRequest dto) {

        // prevent duplicate e‑mail
        boolean dup = UserFileHandler.readAllUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()));
        if (dup) return "E‑mail already in use";

        String id = UUID.randomUUID().toString();
        Customer customer = new Customer(id, dto.getUsername(), dto.getEmail(),
                dto.getPassword(), dto.getAddress(), dto.getPhoneNumber());
        UserFileHandler.saveUser(customer);
        return "Customer registered!";
    }


      //ADMIN  REGISTRATION  (only once)

    @PostMapping("/registerAdmin")
    public String registerAdmin(@RequestBody RegisterRequest dto) {

        boolean adminExists = UserFileHandler.readAllUsers().stream()
                .anyMatch(u -> "restaurant".equals(u.getRole()));
        if (adminExists) return "Admin already exists";

        String id = UUID.randomUUID().toString();
        RestaurantOwner owner = new RestaurantOwner(id, dto.getUsername(), dto.getEmail(),
                dto.getPassword(), dto.getAddress(),
                dto.getPhoneNumber());
        UserFileHandler.saveUser(owner);
        return "Admin account created!";
    }


    // LOGIN  (returns simple text)

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest dto) {
        return UserFileHandler.readAllUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                        u.getPassword().equals(dto.getPassword()))
                .findFirst()
                .map(u -> "Login success: " + u.getUsername() + " (" + u.getRole() + ")")
                .orElse("Login failed");
    }


// ADMIN‑ONLY  READ  ENDPOINTS


    /** List only customers (no admin).  GET /api/users/customers */
    @GetMapping("/customers")
    public List<User> getCustomers() {
        return UserFileHandler.readAllUsers().stream()
                .filter(u -> "customer".equals(u.getRole()))
                .collect(Collectors.toList());
    }

    /** List everyone (owner + customers).  GET /api/users/all */
    @GetMapping("/all")
    public List<User> getAll() {
        return UserFileHandler.readAllUsers();
    }


    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        return UserFileHandler.findUserById(id);
    }

    @PutMapping("/update")
    public String updateUser(@RequestBody RegisterRequest dto) {
        // role is derived from existing record
        User existing = UserFileHandler.findUserById(dto.getId());
        if (existing == null) return "User not found";

        User updated;
        if ("restaurant".equals(existing.getRole())) {
            updated = new RestaurantOwner(existing.getId(), dto.getUsername(), dto.getEmail(),
                    dto.getPassword(), dto.getAddress(), dto.getPhoneNumber());
        } else {
            updated = new Customer(existing.getId(), dto.getUsername(), dto.getEmail(),
                    dto.getPassword(), dto.getAddress(), dto.getPhoneNumber());
        }
        UserFileHandler.updateUser(updated);
        return "User updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        if (UserFileHandler.findUserById(id) == null) return "User not found";
        UserFileHandler.deleteUser(id);
        return "User deleted";
    }

    /* ============================================================
     *  DTO (Data‑Transfer‑Object) classes
     * ============================================================ */
    static class RegisterRequest {
        private String id;           // used only for update
        private String username;
        private String email;
        private String password;
        private String address;
        private String phoneNumber;  // getters & setters

        public String getId()               { return id; }
        public void setId(String id)        { this.id = id; }
        public String getUsername()         { return username; }
        public void setUsername(String u)   { this.username = u; }
        public String getEmail()            { return email; }
        public void setEmail(String e)      { this.email = e; }
        public String getPassword()         { return password; }
        public void setPassword(String p)   { this.password = p; }
        public String getAddress()          { return address; }
        public void setAddress(String a)    { this.address = a; }
        public String getPhoneNumber()      { return phoneNumber; }
        public void setPhoneNumber(String n){ this.phoneNumber = n; }
    }

    static class LoginRequest {
        private String email, password;
        public String getEmail()            { return email; }
        public void setEmail(String e)      { this.email = e; }
        public String getPassword()         { return password; }
        public void setPassword(String p)   { this.password = p; }
    }
}
