package library.interfaces;

import java.time.LocalDate;
import library.exceptions.OverdueException;

/**
 * Interfejs definiujący zachowania dla przedmiotów możliwych do wypożyczenia.
 * Zawiera metody do zarządzania statusem i czasem wypożyczenia.
 */
public interface Loanable {
    /**
     * Wypożycza przedmiot, zmieniając jego status na niedostępny.
     * @throws IllegalStateException gdy przedmiot jest już wypożyczony
     */
    void borrow();

    /**
     * Zwraca przedmiot do biblioteki.
     * @throws OverdueException gdy przedmiot jest przetrzymany
     */
    void returnItem() throws OverdueException;

    /**
     * Sprawdza dostępność przedmiotu.
     * @return true jeśli przedmiot jest dostępny do wypożyczenia
     */
    boolean isAvailable();

    /**
     * Pobiera datę wypożyczenia.
     * @return data wypożyczenia lub null jeśli przedmiot nie jest wypożyczony
     */
    LocalDate getBorrowDate();

    /**
     * Pobiera termin zwrotu.
     * @return planowana data zwrotu lub null jeśli przedmiot nie jest wypożyczony
     */
    LocalDate getDueDate();

    /**
     * Przedłuża termin wypożyczenia.
     * @param days liczba dni o które przedłużamy wypożyczenie
     * @throws IllegalStateException gdy przedmiot nie jest wypożyczony
     * @throws IllegalArgumentException gdy liczba dni jest ujemna
     */
    void extend(int days);

    /**
     * Sprawdza czy przedmiot jest przetrzymany.
     * @return true jeśli minął termin zwrotu
     */
    default boolean isOverdue() {
        LocalDate dueDate = getDueDate();
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Oblicza liczbę dni pozostałych do zwrotu.
     * @return liczba dni do terminu zwrotu (ujemna jeśli termin minął)
     */
    default long daysToReturn() {
        LocalDate dueDate = getDueDate();
        if (dueDate == null) {
            return 0;
        }
        return LocalDate.now().until(dueDate).getDays();
    }
}