package model;

import java.time.LocalDate;

// Definiert ein Objekt, das eine einzelne Fahrt eines Fahrers speichert
public class Fahrt implements Comparable<Fahrt>{
    // Datum der Fahrt
    private final LocalDate datum;
    // Startort der Fahrt
    private final String startort;
    // Gefahrene Kilometer der Fahrt
    private final int kilometer;

    // Konstruktor: Erstellt eine neue Fahrt mit Datum, Startort und Kilometeranzahl
    public Fahrt(LocalDate datum, String startort, int kilometer) {
        this.datum = datum;
        this.startort = startort;
        this.kilometer = kilometer;
    }

    // Getter
    public LocalDate getDatum() {
        return datum;
    }
    public String getStartort() {
        return startort;
    }
    public int getKilometer() {
        return kilometer;
    }

    @Override
    // Vergleicht die Fahrten basierend auf das Datum und sortiert sie in absteigender Reihenfolge
    public int compareTo(Fahrt other) {
        return other.datum.compareTo(this.datum); // Absteigende Sortierung
    }
    @Override
    // Gibt die Fahrt als Text zurück
    public String toString() {
        return datum + " - " + startort + " - " + kilometer + " km";
    }
}
