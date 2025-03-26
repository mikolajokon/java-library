package library;

import library.models.*;
import library.exceptions.*;
import library.interfaces.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;

/**
 * Główna klasa zarządzająca systemem bibliotecznym.
 */
public class Library implements Serializable {
    private Map<String, Item> items;
    private List<User> users;
    private List<Librarian> librarians;
    private static final String SAVE_FILE = "library_data.ser";
    private Map<String, Set<Item>> categories;
    private static final String ITEMS_DATA_FILE = "items_data.dat";
    private static final String USERS_DATA_FILE = "users_data.ser";

    public Library() {
        items = new HashMap<>();
        users = new ArrayList<>();
        librarians = new ArrayList<>();
        categories = new HashMap<>();
    }

    /**
     * Dodaje nowy przedmiot do biblioteki.
     * @param item przedmiot do dodania
     */
    public void addItem(Item item) {
        items.put(item.getId(), item);
    }

    /**
     * Rejestruje nowego użytkownika.
     * @param user użytkownik do zarejestrowania
     */
    public void registerUser(User user) {
        users.add(user);
    }

    /**
     * Zatrudnia nowego bibliotekarza.
     * @param librarian bibliotekarz do zatrudnienia
     */
    public void hirePerson(Librarian librarian) {
        librarians.add(librarian);
    }

    /**
     * Wyszukuje przedmioty po tytule.
     * @param query fraza do wyszukania
     * @return lista znalezionych przedmiotów
     */
    public List<Item> searchItems(String query) {
        return items.values().stream()
            .filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }

    /**
     * Zapisuje stan biblioteki do pliku.
     */
    public void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania stanu biblioteki: " + e.getMessage());
        }
    }

    /**
     * Wczytuje stan biblioteki z pliku.
     * @return wczytana instancja biblioteki lub nowa jeśli wystąpił błąd
     */
    public static Library loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            return (Library) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Nie można wczytać stanu biblioteki: " + e.getMessage());
            return new Library();
        }
    }

    /**
     * Generuje raport o aktualnych wypożyczeniach.
     * @return tekst raportu
     */
    public String generateLoanReport() {
        StringBuilder report = new StringBuilder("=== Raport Wypożyczeń ===\n\n");
        for (User user : users) {
            List<Item> borrowedItems = user.getBorrowedItems();
            if (!borrowedItems.isEmpty()) {
                report.append(String.format("Czytelnik: %s %s\n", user.getFirstName(), user.getLastName()));
                for (Item item : borrowedItems) {
                    report.append(String.format("- %s (termin zwrotu: %s)\n", 
                        item.getTitle(), 
                        ((Loanable)item).getDueDate()));
                }
                report.append("\n");
            }
        }
        return report.toString();
    }

    /**
     * Dodaje przedmiot do kategorii.
     * @param categoryName nazwa kategorii
     * @param item przedmiot do dodania
     */
    public void addToCategory(String categoryName, Item item) {
        categories.computeIfAbsent(categoryName, k -> new HashSet<>()).add(item);
    }

    /**
     * Wyszukuje przedmioty w danej kategorii.
     * @param categoryName nazwa kategorii
     * @return lista przedmiotów w kategorii
     */
    public Set<Item> getItemsByCategory(String categoryName) {
        return categories.getOrDefault(categoryName, new HashSet<>());
    }

    /**
     * Zapisuje dane o przedmiotach do pliku binarnego.
     * @return true jeśli zapis się powiódł, false w przeciwnym razie
     */
    public boolean saveItemsToDataFile() {
        File file = new File(ITEMS_DATA_FILE);
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            
            dos.writeInt(items.size());
            for (Item item : items.values()) {
                // Zapis podstawowych informacji
                dos.writeUTF(item.getId());
                dos.writeUTF(item.getTitle());
                dos.writeInt(item.getYearOfPublication());
                
                if (item instanceof Book book) {
                    dos.writeUTF("BOOK");
                    dos.writeUTF(book.getAuthor());
                    dos.writeUTF(book.getGenre());
                    dos.writeBoolean(book.isAvailable());
                } else if (item instanceof Magazine magazine) {
                    dos.writeUTF("MAGAZINE");
                    dos.writeInt(magazine.getIssueNumber());
                    dos.writeUTF(magazine.getPublisher());
                    dos.writeBoolean(magazine.isAvailable());
                }
            }
            return true;
        } catch (IOException e) {
            Logger.getLogger(Library.class.getName())
                  .log(Level.SEVERE, "Błąd zapisu przedmiotów", e);
            return false;
        }
    }

    /**
     * Wczytuje dane o przedmiotach z pliku binarnego.
     * @return true jeśli odczyt się powiódł, false w przeciwnym razie
     */
    public boolean loadItemsFromDataFile() {
        File file = new File(ITEMS_DATA_FILE);
        if (!file.exists()) {
            System.out.println("Plik z danymi przedmiotów nie istnieje.");
            return false;
        }

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            
            items.clear();
            int itemCount = dis.readInt();
            
            for (int i = 0; i < itemCount; i++) {
                String id = dis.readUTF();
                String title = dis.readUTF();
                int year = dis.readInt();
                String type = dis.readUTF();
                
                Item item = switch (type) {
                    case "BOOK" -> {
                        String author = dis.readUTF();
                        String genre = dis.readUTF();
                        boolean available = dis.readBoolean();
                        Book book = new Book(title, author, genre, year);
                        if (!available) book.borrow();
                        yield book;
                    }
                    case "MAGAZINE" -> {
                        int issueNumber = dis.readInt();
                        String publisher = dis.readUTF();
                        boolean available = dis.readBoolean();
                        Magazine magazine = new Magazine(title, year, issueNumber, publisher);
                        if (!available) magazine.borrow();
                        yield magazine;
                    }
                    default -> throw new IOException("Nieznany typ przedmiotu: " + type);
                };
                items.put(id, item);
            }
            return true;
        } catch (IOException e) {
            Logger.getLogger(Library.class.getName())
                  .log(Level.SEVERE, "Błąd odczytu przedmiotów", e);
            return false;
        }
    }

    /**
     * Zapisuje dane o użytkownikach do pliku serializowanego.
     * @return true jeśli zapis się powiódł, false w przeciwnym razie
     */
    public boolean saveUsersToFile() {
        File file = new File(USERS_DATA_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(new ArrayList<>(users)); // Tworzymy kopię listy
            return true;
        } catch (IOException e) {
            Logger.getLogger(Library.class.getName())
                  .log(Level.SEVERE, "Błąd zapisu użytkowników", e);
            return false;
        }
    }

    /**
     * Wczytuje dane o użytkownikach z pliku serializowanego.
     * @return true jeśli odczyt się powiódł, false w przeciwnym razie
     */
    @SuppressWarnings("unchecked")
    public boolean loadUsersFromFile() {
        File file = new File(USERS_DATA_FILE);
        if (!file.exists()) {
            System.out.println("Plik z danymi użytkowników nie istnieje.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            users = (List<User>) ois.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(Library.class.getName())
                  .log(Level.SEVERE, "Błąd odczytu użytkowników", e);
            return false;
        }
    }

    // Metoda main do demonstracji działania systemu
    public static void main(String[] args) {
        Library library = new Library();
        library.loadItemsFromDataFile(); // Wczytaj przedmioty
        library.loadUsersFromFile();     // Wczytaj użytkowników
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== System Biblioteczny ===");
            System.out.println("1. Użytkownik");
            System.out.println("2. Bibliotekarz");
            System.out.println("3. Wyjście");
            System.out.print("Wybierz rolę: ");
            
            int roleChoice = scanner.nextInt();
            scanner.nextLine(); // Konsumuj znak nowej linii
            
            switch (roleChoice) {
                case 1 -> userMenu(library, scanner);
                case 2 -> librarianMenu(library, scanner);
                case 3 -> {
                    System.out.println("Do widzenia!");
                    return;
                }
                default -> System.out.println("Nieprawidłowa opcja!");
            }
        }
    }

    private static void userMenu(Library library, Scanner scanner) {
        while (true) {
            System.out.println("\n=== Menu Użytkownika ===");
            System.out.println("1. Wyszukaj pozycję");
            System.out.println("2. Wyświetl raport wypożyczeń");
            System.out.println("3. Wypożycz książkę");
            System.out.println("4. Zwróć książkę");
            System.out.println("5. Wyświetl wszystkie książki");
            System.out.println("6. Wyświetl wszystkie czasopisma");
            System.out.println("7. Powrót");
            System.out.print("Wybierz opcję: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Konsumuj znak nowej linii
            
            switch (choice) {
                case 1 -> {
                    System.out.print("Szukana fraza: ");
                    String query = scanner.nextLine();
                    library.searchItems(query).forEach(Item::displayDetails);
                }
                case 2 -> System.out.println(library.generateLoanReport());
                case 3 -> {
                    System.out.print("Podaj ID książki do wypożyczenia: ");
                    String itemId = scanner.nextLine();
                    Item item = library.items.get(itemId);
                    if (item instanceof Loanable) {
                        try {
                            ((Loanable) item).borrow();
                            System.out.println("Wypożyczono książkę: " + item.getTitle());
                        } catch (IllegalStateException e) {
                            System.out.println("Książka jest już wypożyczona.");
                        }
                    } else {
                        System.out.println("Nie znaleziono książki o podanym ID.");
                    }
                }
                case 4 -> {
                    System.out.print("Podaj ID książki do zwrotu: ");
                    String itemId = scanner.nextLine();
                    Item item = library.items.get(itemId);
                    if (item instanceof Loanable) {
                        try {
                            ((Loanable) item).returnItem();
                            System.out.println("Zwrócono książkę: " + item.getTitle());
                        } catch (OverdueException e) {
                            System.out.println("Książka jest przetrzymana: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Nie znaleziono książki o podanym ID.");
                    }
                }
                case 5 -> {
                    System.out.println("=== Wszystkie Książki ===");
                    library.items.values().stream()
                        .filter(item -> item instanceof Book)
                        .forEach(item -> System.out.printf("ID: %s, Tytuł: %s%n", item.getId(), item.getTitle()));
                }
                case 6 -> {
                    System.out.println("=== Wszystkie Czasopisma ===");
                    library.items.values().stream()
                        .filter(item -> item instanceof Magazine)
                        .forEach(item -> System.out.printf("ID: %s, Tytuł: %s%n", item.getId(), item.getTitle()));
                }
                case 7 -> {
                    return;
                }
                default -> System.out.println("Nieprawidłowa opcja!");
            }
        }
    }

    private static void librarianMenu(Library library, Scanner scanner) {
        while (true) {
            System.out.println("\n=== Menu Bibliotekarza ===");
            System.out.println("1. Dodaj książkę");
            System.out.println("2. Dodaj czasopismo");
            System.out.println("3. Zapisz stan");
            System.out.println("4. Zapisz dane przedmiotów");
            System.out.println("5. Zapisz dane użytkowników");
            System.out.println("6. Powrót");
            System.out.print("Wybierz opcję: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Konsumuj znak nowej linii
            
            switch (choice) {
                case 1 -> {
                    System.out.print("Tytuł: ");
                    String title = scanner.nextLine();
                    System.out.print("Autor: ");
                    String author = scanner.nextLine();
                    System.out.print("Gatunek: ");
                    String genre = scanner.nextLine();
                    System.out.print("Rok wydania: ");
                    int year = scanner.nextInt();
                    
                    Book book = new Book(title, author, genre, year);
                    library.addItem(book);
                    library.addToCategory(genre, book);
                    System.out.println("Dodano książkę!");
                }
                case 2 -> {
                    System.out.print("Tytuł: ");
                    String title = scanner.nextLine();
                    System.out.print("Rok wydania: ");
                    int year = scanner.nextInt();
                    System.out.print("Numer wydania: ");
                    int issue = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Wydawca: ");
                    String publisher = scanner.nextLine();
                    
                    Magazine magazine = new Magazine(title, year, issue, publisher);
                    library.addItem(magazine);
                    System.out.println("Dodano czasopismo!");
                }
                case 3 -> {
                    library.saveState();
                    System.out.println("Zapisano stan biblioteki!");
                }
                case 4 -> {
                    if (library.saveItemsToDataFile()) {
                        System.out.println("Pomyślnie zapisano dane przedmiotów!");
                    } else {
                        System.out.println("Wystąpił błąd podczas zapisu przedmiotów.");
                    }
                }
                case 5 -> {
                    if (library.saveUsersToFile()) {
                        System.out.println("Pomyślnie zapisano dane użytkowników!");
                    } else {
                        System.out.println("Wystąpił błąd podczas zapisu użytkowników.");
                    }
                }
                case 6 -> {
                    return;
                }
                default -> System.out.println("Nieprawidłowa opcja!");
            }
        }
    }
}