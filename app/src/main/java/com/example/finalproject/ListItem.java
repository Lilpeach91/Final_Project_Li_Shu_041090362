package com.example.finalproject;

public class ListItem {
    private String title;
    private String description;

    public ListItem(String title, String description) {
        this.title = title;
        this.description = description;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title;
    }
}

