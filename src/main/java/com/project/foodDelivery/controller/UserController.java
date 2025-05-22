package com.project.foodDelivery.controller;

import com.project.foodDelivery.model.Customer;
import com.project.foodDelivery.model.FoodItem;
import com.project.foodDelivery.model.Order;
import com.project.foodDelivery.model.RestaurantOwner;
import com.project.foodDelivery.model.User;
import com.project.foodDelivery.service.DSAService;
import com.project.foodDelivery.service.UserFileHandler;
import com.project.foodDelivery.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller //this keyword tells to spring boot, this class controls web requests and returns web pages.
@RequestMapping("/") // all URLs start from /
public class UserController {

    @Autowired
    private FoodItemService foodItemService;
    
    @Autowired
    private DSAService dsaService;

    //page routes
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("foodItems", foodItemService.getAllFoodItems());
        return "home";
    } //Model model lets send data to HTML pages

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

    @GetMapping("/customer-home")
    public String customerHome(Model model) {
        model.addAttribute("foodItems", foodItemService.getAllFoodItems());
        return "customerHome";
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

    @GetMapping("/orders")
    public String ordersPage() {
        return "orders";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    // API endpoints
    @PostMapping("/api/users/register") //Handles POST requests sent to URL
    @ResponseBody
    public String registerCustomer(@RequestBody RegisterRequest dto) {//Converts the incoming JSON into a Java object called dto (data transfer object)
        // prevent duplicate e‑mail
        //dto has all the user data
        boolean dup = UserFileHandler.readAllUsers().stream() // stream is an easy and powerful way to process data
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()));
        if (dup) return "E‑mail already in use";

        String id = UUID.randomUUID().toString(); //Creates a unique ID
        Customer customer = new Customer(id, dto.getUsername(), dto.getEmail(),
                dto.getPassword(), dto.getAddress(), dto.getPhoneNumber());
        UserFileHandler.saveUser(customer);
        return "Customer registered!";
    }

    @PostMapping("/api/users/registerAdmin")
    @ResponseBody
    public String registerAdmin(@RequestBody RegisterRequest dto) {
        System.out.println("Attempting to register admin with email: " + dto.getEmail());
        
        boolean adminExists = UserFileHandler.readAllUsers().stream()
                .anyMatch(u -> "owner".equals(u.getRole()));
        if (adminExists) {
            System.out.println("Admin registration failed: Admin already exists");
            return "Admin already exists";
        }

        String id = UUID.randomUUID().toString();
        RestaurantOwner owner = new RestaurantOwner(id, dto.getUsername(), dto.getEmail(),
                dto.getPassword(), dto.getAddress(),
                dto.getPhoneNumber());
                
        System.out.println("Creating new admin with role: " + owner.getRole());
        UserFileHandler.saveUser(owner);
        System.out.println("Admin account created successfully");
        return "Admin account created!";
    }

    @PostMapping("/api/users/login")
    @ResponseBody
    public LoginResponse login(@RequestBody LoginRequest dto) {
        List<User> users = UserFileHandler.readAllUsers();
        // Debug: Print all users and their roles
        System.out.println("All users:");
        for (User u : users) {
            System.out.println("Email: " + u.getEmail() + ", Role: " + u.getRole());
        }
        
        User matchedUser = users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                        u.getPassword().equals(dto.getPassword()))
                .findFirst()
                .orElse(null);
                
        if (matchedUser != null) {
            System.out.println("Login successful for user: " + matchedUser.getEmail() + ", Role: " + matchedUser.getRole());
            return new LoginResponse(true, matchedUser.getUsername(), matchedUser.getRole(), "", matchedUser.getId());
        } else {
            System.out.println("Login failed for email: " + dto.getEmail());
            return new LoginResponse(false, "", "", "Invalid credentials", "");
        }
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
    // PathVariable takes the value from {id} in the URL and gives it to this method.
    public User getById(@PathVariable String id) {
        return UserFileHandler.findUserById(id);
    }

    @PutMapping("/api/users/update")
    @ResponseBody
    public String updateUser(@RequestBody RegisterRequest dto) {
        User existing = UserFileHandler.findUserById(dto.getId());
        if (existing == null) return "User not found";

        User updated;
        if ("owner".equals(existing.getRole())) {
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
    
    // DSA-related endpoints
    
    @PostMapping("/api/orders/enqueue")
    @ResponseBody
    public String enqueueOrder(@RequestBody Order order) {
        // Generate a unique ID if not provided
        if (order.getId() == null || order.getId().isEmpty()) {
            order.setId(UUID.randomUUID().toString());
        }
        
        // Set the order date if not provided
        if (order.getOrderDate() == null || order.getOrderDate().isEmpty()) {
            order.setOrderDate(java.time.LocalDateTime.now().toString());
        }
        
        // Set initial status if not provided
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("Pending");
        }
        
        dsaService.addOrderToQueue(order);
        return "Order added to processing queue";
    }
    
    @GetMapping("/api/orders/queue")
    @ResponseBody
    public Order[] getQueuedOrders() {
        return dsaService.getAllQueuedOrders();
    }
    
    @GetMapping("/api/orders/queue/size")
    @ResponseBody
    public int getQueueSize() {
        return dsaService.getQueueSize();
    }
    
    @GetMapping("/api/orders/process/next")
    @ResponseBody
    public Order processNextOrder() {
        return dsaService.processNextOrder();
    }
    
    @GetMapping("/api/orders/peek")
    @ResponseBody
    public Order peekNextOrder() {
        return dsaService.peekNextOrder();
    }
    
    @PostMapping("/api/orders/update-status")
    @ResponseBody
    public String updateOrderStatus(@RequestParam String orderId, @RequestParam String status) {
        boolean updated = dsaService.updateOrderStatus(orderId, status);
        return updated ? "Order status updated" : "Order not found";
    }
    
    @GetMapping("/api/food-items/sort")
    @ResponseBody
    public List<FoodItem> getSortedFoodItems(@RequestParam(required = false) String order) {
        List<FoodItem> foodItems = foodItemService.getAllFoodItems();
        FoodItem[] sortedItems;
        
        if ("desc".equals(order)) {
            sortedItems = dsaService.sortFoodItemsByPriceDescending(foodItems);
        } else {
            sortedItems = dsaService.sortFoodItemsByPrice(foodItems);
        }
        
        return dsaService.convertToList(sortedItems);
    }


     // DTO (Data‑Transfer‑Object) classes
    //The client (frontend) would have to send unnecessary data like id, role, thats why this class has separated
    // dto classes

    private static class RegisterRequest {
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

     private static class LoginRequest {
        private String email, password;
        public String getEmail()            { return email; }
        public void setEmail(String e)      { this.email = e; }
        public String getPassword()         { return password; }
        public void setPassword(String p)   { this.password = p; }
    }

    private static class LoginResponse {
        private boolean success;
        private String username;
        private String role;
        private String message;
        private String id;

        public LoginResponse(boolean success, String username, String role, String message, String id) {
            this.success = success;
            this.username = username;
            this.role = role;
            this.message = message;
            this.id = id;
        }

        public boolean isSuccess() { return success; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getMessage() { return message; }
        public String getId() { return id; }
    }
}
