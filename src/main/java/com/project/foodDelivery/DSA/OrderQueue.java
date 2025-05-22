package com.project.foodDelivery.DSA;

import com.project.foodDelivery.model.Order;

public class OrderQueue {
    private static final int DEFAULT_CAPACITY = 10;
    
    private Order[] elements;
    private int front;
    private int rear;
    private int size;
    private int capacity;


    public OrderQueue() {
        this(DEFAULT_CAPACITY);
    }

    public OrderQueue(int capacity) {
        this.elements = new Order[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
        this.capacity = capacity;
    }

    public void insert(Order order) {
        if (size == capacity) {
            System.out.println("queue is full");
        }

        if(rear==capacity-1)
            rear=-1;
        elements[++rear] = order;
        size++;
    }

    public Order remove() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        Order temp = elements[front++];
        if(front==capacity)
            front=0;
        size--;
        return temp;

    }

    public Order peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        
        return elements[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }



    public Order[] toArray() {
        Order[] orders = new Order[size];
        
        for (int i = 0; i < size; i++) {
            orders[i] = elements[(front + i) % capacity];
        }
        
        return orders;
    }

    public boolean updateOrderStatus(String orderId, String newStatus) {
        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            Order order = elements[index];
            
            if (order != null && order.getId().equals(orderId)) {
                order.setStatus(newStatus);
                return true;
            }
        }
        
        return false;
    }
}
