package com.gokul.todoapplication.db;

public class Note {
    private int id;
    private String name;
    private int priority;
    private int status;

    public Note(int id, String name, int priority, int status) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
