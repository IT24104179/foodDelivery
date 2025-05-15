package com.project.foodDelivery.model;

public class VegItem extends com.foodDelivery.project.model.FoodItem {
    private static final long serialVersionUID = 1L;
    
    private boolean containsDairy;
    
    public VegItem() {
        super();
        setType("veg");
    }
    
    public VegItem(String id, String name, double price, String description, String imageUrl, boolean available, boolean containsDairy) {
        super(id, name, price, description, imageUrl, available, "veg");
        this.containsDairy = containsDairy;
    }
    
    public boolean isContainsDairy() {
        return containsDairy;
    }
    
    public void setContainsDairy(boolean containsDairy) {
        this.containsDairy = containsDairy;
    }
    
    @Override
    public String toString() {
        return super.toString() + "," + containsDairy;
    }
}
