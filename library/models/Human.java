package library.models;

import java.io.Serializable;

public abstract class Human implements Serializable {
    private String firstName;
    private String lastName;
    private String id;
    
    protected Human(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateId();
    }
    
    private String generateId() {
        return String.format("%s-%d", lastName.substring(0, Math.min(3, lastName.length())), 
            System.currentTimeMillis() % 10000);
    }
    
    public abstract void displayInfo();
    
    // Gettery i settery
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getId() {
        return id;
    }
}
