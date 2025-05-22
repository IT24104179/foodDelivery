package com.project.foodDelivery.service;

import com.project.foodDelivery.DSA.FoodItemQuickSort;
import com.project.foodDelivery.DSA.OrderQueue;
import com.project.foodDelivery.model.FoodItem;
import com.project.foodDelivery.model.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DSAService {
    private static DSAService instance;
    private OrderQueue orderQueue;
    private FoodItemQuickSort quickSort;
    
    @Autowired
    private FoodItemService foodItemService;
    
    public DSAService() {
        orderQueue = new OrderQueue();
        quickSort = new FoodItemQuickSort();
    }
    

    public static synchronized DSAService getInstance() {
        if (instance == null) {
            instance = new DSAService();
        }
        return instance;
    }

    public FoodItemService getFoodItemService() {
        return foodItemService;
    }
    

    public void addOrderToQueue(Order order) {
        // If the food price is not set, get it from the food item service
        if (order.getPrice() <= 0 && order.getFoodItemId() != null) {
            FoodItem foodItem = foodItemService.getFoodItemById(order.getFoodItemId());
            if (foodItem != null) {
                order.setPrice(foodItem.getPrice());
                // Recalculate total amount
                order.setTotalAmount(order.getPrice() * order.getQuantity());
            }
        }
        orderQueue.insert(order);
    }
    

    public Order processNextOrder() {
        if (orderQueue.isEmpty()) {
            return null;
        }
        return orderQueue.remove();
    }
    

    public Order peekNextOrder() {
        if (orderQueue.isEmpty()) {
            return null;
        }
        return orderQueue.peek();
    }

    public Order[] getAllQueuedOrders() {
        return orderQueue.toArray();
    }
    

    public int getQueueSize() {
        return orderQueue.size();
    }
    

    public boolean updateOrderStatus(String orderId, String newStatus) {
        return orderQueue.updateOrderStatus(orderId, newStatus);
    }
    

    public FoodItem[] sortFoodItemsByPrice(List<FoodItem> foodItems) {
        FoodItem[] itemsArray = foodItems.toArray(new FoodItem[0]);
        quickSort.sortByPrice(itemsArray);
        return itemsArray;
    }

    public FoodItem[] sortFoodItemsByPriceDescending(List<FoodItem> foodItems) {
        FoodItem[] itemsArray = foodItems.toArray(new FoodItem[0]);
        quickSort.sortByPriceDescending(itemsArray);
        return itemsArray;
    }

    public List<FoodItem> convertToList(FoodItem[] foodItems) {
        List<FoodItem> result = new ArrayList<>();
        for (FoodItem item : foodItems) {
            result.add(item);
        }
        return result;
    }
}
