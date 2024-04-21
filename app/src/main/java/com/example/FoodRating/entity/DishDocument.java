package com.example.FoodRating.entity;

import java.util.List;

public class DishDocument {
    private List<Dish> dishes;

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public DishDocument(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
