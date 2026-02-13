package com.comics.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comics")
public class Comic {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title; // título único

    private int number;
    private String publisher;
    private double price;

    public Comic() {}

    public Comic(String title, int number, String publisher, double price) {
        this.title = title;
        this.number = number;
        this.publisher = publisher;
        this.price = price;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
