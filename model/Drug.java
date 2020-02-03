package com.example.lab1.model;

public class Drug {
    private int id;
    private int quantity;
    private String name;
    private String composition;
    private String use;
    private boolean recipe_required;
    private double price;

    public Drug(int id,int quantity,String name,String composition,String use,boolean recipe_required, double price)
    {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
        this.composition = composition;
        this.use = use;
        this.recipe_required = recipe_required;
        this.price = price;
    }

    public Drug()
    {
        this.id =-1;
        this.quantity = -1;
        this.name="";
        this.composition="";
        this.use ="";
        this.recipe_required = false;
        this.price = 0.0f;
    }

    public Drug(int id)
    {
        this.id = id;
    }

    public Drug(int id, int quantity)
    {
        this.id = id;
        this.quantity = quantity;
    }

    public Drug(int id,String name, int quantity)
    {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setQuantity(int newquantity)
    {
        this.quantity = newquantity;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComposition() {
        return composition;
    }

    public String getUse() {
        return use;
    }

    public boolean get_Recipe_required() {
        return recipe_required;
    }

    public String getName() {
        return name;
    }

    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "Drug{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", name='" + name + '\'' +
                ", composition='" + composition + '\'' +
                ", use='" + use + '\'' +
                ", recipe_required=" + recipe_required +
                ", price=" + price +
                '}';
    }
}
