package com.iacademy.library.model;

public class Book {
    private String id;
    private String title;
    private String description;
    private int quantity;
    private String imageUrl; // Optional field

    // Constructors
    public Book() {}

    public Book(String id, String title, String description, int quantity, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public Book(String title, String description, int quantity, String imageUrl) {
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}