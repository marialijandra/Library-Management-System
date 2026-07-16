package com.iacademy.library.model;

/** Mirrors a row in the `books` table (shared table - Book Catalog module owns full CRUD on it). */
public class Book {

    private String id;
    private String title;
    private String description;
    private int quantity;
    private String imageUrl;

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
