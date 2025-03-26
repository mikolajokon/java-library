package library.models;

import java.util.ArrayList;
import java.util.List;

public class User extends Human {
    private List<Item> borrowedItems;
    private List<Item> borrowingHistory;
    
    public User(String firstName, String lastName) {
        super(firstName, lastName);
        this.borrowedItems = new ArrayList<>();
        this.borrowingHistory = new ArrayList<>();
    }
    
    public void borrowItem(Item item) {
        if (item instanceof Loanable) {
            ((Loanable) item).borrow();
            borrowedItems.add(item);
            borrowingHistory.add(item);
        }
    }
    
    public void returnItem(Item item) {
        if (item instanceof Loanable) {
            ((Loanable) item).returnItem();
            borrowedItems.remove(item);
        }
    }
    
    @Override
    public void displayInfo() {
        System.out.printf("Czytelnik: %s %s (ID: %s)%n", 
            getFirstName(), getLastName(), getId());
        System.out.println("Aktualnie wypożyczone pozycje:");
        borrowedItems.forEach(item -> System.out.println("- " + item.getTitle()));
    }
    
    public List<Item> getBorrowedItems() {
        return new ArrayList<>(borrowedItems);
    }
    
    public List<Item> getBorrowingHistory() {
        return new ArrayList<>(borrowingHistory);
    }
}