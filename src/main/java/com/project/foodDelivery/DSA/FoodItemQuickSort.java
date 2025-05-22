package com.project.foodDelivery.DSA;

import com.project.foodDelivery.model.FoodItem;

public class FoodItemQuickSort {

    private void quickSort(FoodItem[] foodItems, int low, int high) {
        if (low < high) {

            int pivotIndex = partition(foodItems, low, high);


            quickSort(foodItems, low, pivotIndex - 1);
            quickSort(foodItems, pivotIndex + 1, high);
        }
    }

    private int partition(FoodItem[] foodItems, int low, int high) {

        double pivot = foodItems[high].getPrice();


        int i = low - 1;

        for (int j = low; j < high; j++) {

            if (foodItems[j].getPrice() <= pivot) {
                i++;


                swap(foodItems, i, j);
            }
        }


        swap(foodItems, i + 1, high);

        return i + 1;
    }

    private void swap(FoodItem[] foodItems, int i, int j) {
        FoodItem temp = foodItems[i];
        foodItems[i] = foodItems[j];
        foodItems[j] = temp;
    }

    public void sortByPrice(FoodItem[] foodItems) {
        if (foodItems == null || foodItems.length <= 1) {
            return;
        }
        
        quickSort(foodItems, 0, foodItems.length - 1);
    }

    public void sortByPriceDescending(FoodItem[] foodItems) {
        if (foodItems == null || foodItems.length <= 1) {
            return;
        }
        
        quickSortDescending(foodItems, 0, foodItems.length - 1);
    }

    private void quickSortDescending(FoodItem[] foodItems, int low, int high) {
        if (low < high) {

            int pivotIndex = partitionDescending(foodItems, low, high);
            

            quickSortDescending(foodItems, low, pivotIndex - 1);
            quickSortDescending(foodItems, pivotIndex + 1, high);
        }
    }

    private int partitionDescending(FoodItem[] foodItems, int low, int high) {

        double pivot = foodItems[high].getPrice();
        

        int i = low - 1;

        for (int j = low; j < high; j++) {

            if (foodItems[j].getPrice() >= pivot) {
                i++;

                swap(foodItems, i, j);
            }
        }

        swap(foodItems, i + 1, high);
        
        return i + 1;
    }
    

}
