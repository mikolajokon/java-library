package library.models;

import java.time.LocalDate;
import library.exceptions.OverdueException;
import library.interfaces.Loanable;

public class Book extends Item implements Loanable {
    private String author;
    private String genre;
    private boolean available;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public Book(String title, String author, String genre, int yearOfPublication) {
        super(title, yearOfPublication);
        this.author = author;
        this.genre = genre;
        this.available = true;
    }

    @Override
    public void displayDetails() {
        System.out.printf("Książka: %s, Autor: %s, Gatunek: %s, Rok: %d%n", 
            getTitle(), author, genre, getYearOfPublication());
    }

    @Override
    public void borrow() {
        if (!available) {
            throw new IllegalStateException("Książka jest już wypożyczona");
        }
        available = false;
        borrowDate = LocalDate.now();
        dueDate = borrowDate.plusDays(30); // Domyślny okres wypożyczenia: 30 dni
    }

    @Override
    public void returnItem() throws OverdueException {
        if (isOverdue()) {
            throw new OverdueException("Książka jest przetrzymana o " + 
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
            throw new IllegalStateException("Nie można przedłużyć terminu - książka nie jest wypożyczona");
        }
        if (days < 0) {
            throw new IllegalArgumentException("Liczba dni nie może być ujemna");
        }
        dueDate = dueDate.plusDays(days);
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }

    public long daysToReturn() {
        return LocalDate.now().until(dueDate).getDays();
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }
}