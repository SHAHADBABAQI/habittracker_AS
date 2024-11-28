package com.example.habtrac;

public class Habit {
    private int id;
    private String name;
    private String description;
    private boolean isCompletedToday;

    public Habit(int id, String name, String description, boolean isCompletedToday) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isCompletedToday = isCompletedToday;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompletedToday() {
        return isCompletedToday;
    }

    public void setCompletedToday(boolean completedToday) {
        isCompletedToday = completedToday;
    }
}
