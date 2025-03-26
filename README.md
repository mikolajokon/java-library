# README

## System Biblioteczny

System Biblioteczny to aplikacja do zarządzania biblioteką, która umożliwia użytkownikom wyszukiwanie, wypożyczanie i zwracanie książek oraz czasopism. Bibliotekarze mogą dodawać nowe pozycje do biblioteki oraz zarządzać danymi użytkowników i przedmiotów.

### Struktura projektu

```
java-library/
├── library/
│   ├── exceptions/
│   │   └── OverdueException.java
│   ├── interfaces/
│   │   └── Loanable.java
│   ├── models/
│   │   ├── Book.java
│   │   ├── Item.java
│   │   ├── Magazine.java
│   │   ├── User.java
│   │   └── Librarian.java
│   └── Library.java
└── README.md
```

### Klasy

- **library.exceptions.OverdueException**: Wyjątek rzucany, gdy przedmiot jest przetrzymany.
- **library.interfaces.Loanable**: Interfejs definiujący zachowania dla przedmiotów możliwych do wypożyczenia.
- **library.models.Book**: Klasa reprezentująca książkę.
- **library.models.Item**: Abstrakcyjna klasa bazowa dla wszystkich przedmiotów w bibliotece.
- **library.models.Magazine**: Klasa reprezentująca czasopismo.
- **library.models.User**: Klasa reprezentująca użytkownika biblioteki.
- **library.models.Librarian**: Klasa reprezentująca bibliotekarza.
- **library.Library**: Główna klasa zarządzająca systemem bibliotecznym.

### Funkcjonalności

#### Użytkownik

- Wyszukiwanie pozycji
- Wyświetlanie raportu wypożyczeń
- Wypożyczanie książki
- Zwracanie książki

#### Bibliotekarz

- Dodawanie książki
- Dodawanie czasopisma
- Zapis stanu biblioteki
- Zapis danych przedmiotów
- Zapis danych użytkowników

### Uruchomienie projektu

1. Skompiluj projekt:

```bash
cd /Users/mikolajokon/java-library
javac library/Library.java
```

2. Uruchom projekt:

```bash
java library.Library
```

### Przykładowe użycie

Po uruchomieniu aplikacji użytkownik zostanie poproszony o wybór roli: użytkownik lub bibliotekarz. W zależności od wybranej roli, użytkownik będzie miał dostęp do odpowiednich funkcjonalności.

#### Menu główne

```
=== System Biblioteczny ===
1. Użytkownik
2. Bibliotekarz
3. Wyjście
Wybierz rolę:
```

#### Menu użytkownika

```
=== Menu Użytkownika ===
1. Wyszukaj pozycję
2. Wyświetl raport wypożyczeń
3. Wypożycz książkę
4. Zwróć książkę
5. Powrót
Wybierz opcję:
```

#### Menu bibliotekarza

```
=== Menu Bibliotekarza ===
1. Dodaj książkę
2. Dodaj czasopismo
3. Zapisz stan
4. Zapisz dane przedmiotów
5. Zapisz dane użytkowników
6. Powrót
Wybierz opcję:
```

### Autor

Mikołaj Okoń

### Licencja

Projekt jest dostępny na licencji MIT.
