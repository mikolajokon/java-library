package library.models;

import java.time.LocalDate;
import library.exceptions.OverdueException;
import library.interfaces.Loanable;

public class Magazine extends Item implements Loanable {
    private int issueNumber;
    private String publisher;
    private boolean available;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public Magazine(String title, int yearOfPublication, int issueNumber, String publisher) {
        super(title, yearOfPublication);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.available = true;
    }

    @Override
    public void displayDetails() {
        System.out.printf("Czasopismo: %s, Numer wydania: %d, Wydawca: %s, Rok: %d%n", 
            getTitle(), issueNumber, publisher, getYearOfPublication());
    }

    @Override
    public void borrow() {
        if (!available) {
            throw new IllegalStateException("Czasopismo jest już wypożyczone");
        }
        available = false;
        borrowDate = LocalDate.now();
        dueDate = borrowDate.plusDays(30); // Domyślny okres wypożyczenia: 30 dni
    }

    @Override
    public void returnItem() throws OverdueException {
        if (isOverdue()) {
            throw new OverdueException("Czasopismo jest przetrzymane o " + 
                Math.abs(daysToReturn()) + " dni");
        }
        available = true;
        borrowDate = null;
        dueDate = null;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public void extend(int days) {
        if (available) {
            throw new IllegalStateException("Nie można przedłużyć terminu - czasopismo nie jest wypożyczone");
        }
        if (days < 0) {
            throw new IllegalArgumentException("Liczba dni nie może być ujemna");
        }
        dueDate = dueDate.plusDays(days);
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public String getPublisher() {
        return publisher;
    }
}