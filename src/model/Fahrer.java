package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Definiert ein Objekt, das die Informationen eines Fahrers speichert
public class Fahrer {
    // Personalnummer des Fahrers
    private final String personalnummer;
    // Vorname des Fahrers
    private final String vorname;
    // Nachname des Fahrers
    private String nachname;
    // Liste der Fahrten des Fahrers
    private final List<Fahrt> fahrten;

    // Konstruktor
    public Fahrer(String personalnummer, String vorname, String nachname) {
        this.personalnummer = personalnummer;
        this.vorname = vorname;
        this.nachname = nachname;
        this.fahrten = new ArrayList<>();
    }

    // Getter und Setter
    public String getPersonalnummer() {
        return personalnummer;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    // Setter für mögliche Namensänderungen
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public List<Fahrt> getFahrten() {
        return fahrten;
    }

    // Fügt eine neue Fahrt zur Liste der Fahrten hinzu und sortiert die Liste nach Datum
    public void addFahrt(Fahrt fahrt) {
        fahrten.add(fahrt);
        Collections.sort(fahrten);
    }

    // Berechnung der Gesamtkilometer aller Fahrten eines Fahrers
    public int berechneGesamtKilometer() {
        // Erzeugt einen Stream der Fahrten, extrahiert die Kilometeranzahl und summiert diese
        return fahrten.stream()
                .mapToInt(Fahrt::getKilometer)
                .sum();
    }

    @Override
    // Überschreibt die toString-Methode, um den Fahrer als Text darzustellen
    public String toString() {
        // Gibt die Personalnummer, den Vornamen und den Nachnamen des Fahrers zurück
        return personalnummer + " " + vorname + " " + nachname;
    }
}
