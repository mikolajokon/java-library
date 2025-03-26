package library.models;

import java.io.Serializable;
import java.util.UUID;

public abstract class Item implements Serializable {
    private final String id;
    private String title;
    private int yearOfPublication;

    protected Item(String title, int yearOfPublication) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.yearOfPublication = yearOfPublication;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYearOfPublication() {
        return yearOfPublication;
    }

    public abstract void displayDetails();
}