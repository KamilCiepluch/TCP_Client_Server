# TCP_Client_Server
Usługa Echo: serwer czeka na połączenie klienta. Po zaakceptowaniu połączenia oczekuje na wiadomość.

Polecenie ćwiczeniowe:
Programu udostępnia kilku użytkownikom jednocześnie usługę ECHO.
Zadaniem programu jest:
• udostępnianie usługi dla wybranych interfejsów sieciowych
(domyślnie dla wszystkich, a tylko nie localhost!),
• udostępnienie usługi ECHO dla protokołu TCP/IP na podanym porcie (wartość domyślna 7 ).
• udostępnienie możliwości wprowadzenia innego portu komunikacji,
• rejestrowanie wszystkich połączeń klientów, przypisywanie im numeru # i wyświetlanie w stałym
miejscu (znajdującym się 80% od lewej krawędzi okna oraz 10% od górnej krawędzi) na bieżąco
aktualizowanych informacji o podłączonych komputerach odległych – adres IP wraz z numerem portu
komunikacji, np. #1 192.168.0.10:34567
 #2 192.168.0.11:76543
• posiadać limit aktywnych połączeń do 3, a po jego przekroczeniu natychmiast rozłączać nadliczbowe
połączenie (po stronie klienta powinno być to interpretowane jako SERVER BUSY),
• odporność na niewłaściwe dane wpisane przez użytkownika, niewłaściwe zamknięcie gniazda
po stronie klienta i zwalnianie zasobów (w tym wszystkich wątków) w chwili zamykania programu,
• natychmiastowe odsyłanie wiernej kopii otrzymanej wiadomości (tzn. funkcja wysyłająca wysyła TYLKO
tyle danych, ile serwer otrzymał od klienta),
• informowanie użytkownika o każdej otrzymanej wiadomości z zaznaczeniem numeru # przypisanego
nadawcy wiadomości oraz treści i rozmiaru wiadomości,
• informowanie o akceptacji nowego połączenia (w tym nadliczbowego) oraz rozłączenia klienta.
