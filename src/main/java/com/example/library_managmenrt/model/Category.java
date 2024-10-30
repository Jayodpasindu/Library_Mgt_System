package com.example.library_managmenrt.model;

import javax.persistence.*;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    private int id;
    @Column(nullable = false)
    private String name;

    public Category() {}

    public Category(int id, String name) {
        this.id = id; // Set the id field
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
