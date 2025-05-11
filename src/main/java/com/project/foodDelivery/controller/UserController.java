package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Customer;
import com.project.foodDelivery.model.RestaurantOwner;
import com.project.foodDelivery.model.User;
import com.project.foodDelivery.service.UserFileHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserController {

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/owner-home")
    public String ownerHome() {
        return "ownerHome";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", UserFileHandler.readAllUsers());
        return "admin-dashboard";
    }

    @GetMapping("/update-details")
    public String updateDetailsPage() {
        return "update-details";
    }

    // API endpoints
    @PostMapping("/api/users/register")
    @ResponseBody
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

    @PostMapping("/api/users/registerAdmin")
    @ResponseBody
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

    @PostMapping("/api/users/login")
    @ResponseBody
    public String login(@RequestBody LoginRequest dto) {
        return UserFileHandler.readAllUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                        u.getPassword().equals(dto.getPassword()))
                .findFirst()
                .map(u -> "Login success: " + u.getUsername() + " (" + u.getRole() + ")")
                .orElse("Login failed");
    }

    @GetMapping("/api/users/customers")
    @ResponseBody
    public List<User> getCustomers() {
        return UserFileHandler.readAllUsers().stream()
                .filter(u -> "customer".equals(u.getRole()))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/users/all")
    @ResponseBody
    public List<User> getAll() {
        return UserFileHandler.readAllUsers();
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public User getById(@PathVariable String id) {
        return UserFileHandler.findUserById(id);
    }

    @PutMapping("/api/users/update")
    @ResponseBody
    public String updateUser(@RequestBody RegisterRequest dto) {
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

    @DeleteMapping("/api/users/delete/{id}")
    @ResponseBody
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
