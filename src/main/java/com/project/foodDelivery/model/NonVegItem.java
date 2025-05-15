package com.foodDelivery.project.model;

public class NonVegItem extends FoodItem {
    private static final long serialVersionUID = 1L;
    
    private String meatType; // chicken, beef, pork, etc.
    
    public NonVegItem() {
        super();
        setType("non-veg");
    }
    
    public NonVegItem(String id, String name, double price, String description, String imageUrl, boolean available, String meatType) {
        super(id, name, price, description, imageUrl, available, "non-veg");
        this.meatType = meatType;
    }
    
    public String getMeatType() {
        return meatType;
    }
    
    public void setMeatType(String meatType) {
        this.meatType = meatType;
    }
    
    @Override
    public String toString() {
        return super.toString() + "," + meatType;
    }
}
