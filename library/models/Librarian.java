package library.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Klasa reprezentująca bibliotekarza w systemie bibliotecznym.
 * Rozszerza klasę Human i zawiera funkcjonalności związane z zarządzaniem wypożyczeniami.
 */
public class Librarian extends Human {
    private static final Logger LOGGER = Logger.getLogger(Librarian.class.getName());
    private double salary;
    private String position;
    private List<String> processedTransactions;
    private static final int MAX_LOANS_PER_USER = 5;

    /**
     * Tworzy nowego bibliotekarza z określonymi danymi.
     * @param firstName Imię bibliotekarza
     * @param lastName Nazwisko bibliotekarza
     * @param salary Wynagrodzenie bibliotekarza
     * @param position Stanowisko bibliotekarza
     */
    public Librarian(String firstName, String lastName, double salary, String position) {
        super(firstName, lastName);
        this.salary = salary;
        this.position = position;
        this.processedTransactions = new ArrayList<>();
    }

    /**
     * Przetwarza wypożyczenie przedmiotu przez użytkownika.
     * @param user Użytkownik wypożyczający przedmiot
     * @param item Przedmiot do wypożyczenia
     * @throws InvalidItemException gdy przedmiot nie istnieje lub nie jest dostępny
     */
    public void processItemLoan(User user, Item item) throws InvalidItemException {
        if (item == null) {
            throw new InvalidItemException("Przedmiot nie istnieje w systemie");
        }
        
        if (user.getBorrowedItems().size() >= MAX_LOANS_PER_USER) {
            throw new IllegalStateException("Użytkownik osiągnął limit wypożyczeń");
        }

        if (item instanceof Loanable && !((Loanable) item).isAvailable()) {
            throw new InvalidItemException("Przedmiot jest obecnie niedostępny");
        }

        user.borrowItem(item);
        String transaction = String.format("Wypożyczenie: %s -> %s %s (Data: %s)",
            item.getTitle(), user.getFirstName(), user.getLastName(), 
            java.time.LocalDateTime.now());
        processedTransactions.add(transaction);
        LOGGER.info(transaction);
    }

    /**
     * Przetwarza zwrot przedmiotu przez użytkownika.
     * @param user Użytkownik zwracający przedmiot
     * @param item Przedmiot do zwrotu
     * @throws InvalidItemException gdy przedmiot nie istnieje
     */
    public void processItemReturn(User user, Item item) throws InvalidItemException {
        if (item == null) {
            throw new InvalidItemException("Przedmiot nie istnieje w systemie");
        }

        if (!user.getBorrowedItems().contains(item)) {
            throw new InvalidItemException("Ten przedmiot nie został wypożyczony przez tego użytkownika");
        }

        user.returnItem(item);
        String transaction = String.format("Zwrot: %s <- %s %s (Data: %s)",
            item.getTitle(), user.getFirstName(), user.getLastName(),
            java.time.LocalDateTime.now());
        processedTransactions.add(transaction);
        LOGGER.info(transaction);
    }

    /**
     * Wyświetla historię transakcji przetworzonych przez bibliotekarza.
     */
    public void displayTransactionHistory() {
        System.out.println("Historia transakcji:");
        processedTransactions.forEach(System.out::println);
    }

    @Override
    public void displayInfo() {
        System.out.printf("Bibliotekarz: %s %s (ID: %s)%nStanowisko: %s%nLiczba transakcji: %d%n", 
            getFirstName(), getLastName(), getId(), position, processedTransactions.size());
    }

    // Gettery i settery pozostają bez zmian...
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
}