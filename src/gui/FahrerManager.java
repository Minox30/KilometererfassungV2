package gui;

import model.Fahrer;
import persistence.InterneFahrtenVerarbeitung;
import static util.Fehlermeldung.zeigeFehlermeldung;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Verwalter für die Fahrer-Daten, einschließlich der Fahrer-Combobox und der Fahrer-Map
public class FahrerManager {

    // Combobox zur Anzeige der Fahrer in der GUI
    private final JComboBox<String> fahrerComboBox;
    // Map zur Speicherung der Fahrerobjekte
    private final Map<String, Fahrer> fahrerMap;
    private final InterneFahrtenVerarbeitung interneFahrtenVerarbeitung;

    // Konstruktor: Initialisiert den FahrerManager und lädt die Fahrerdaten
    public FahrerManager(JComboBox<String> fahrerComboBox, InterneFahrtenVerarbeitung interneFahrtenVerarbeitung) {
        // Referenz auf die ComboBox, die die Fahrer anzeigt
        this.fahrerComboBox = fahrerComboBox;
        this.interneFahrtenVerarbeitung = interneFahrtenVerarbeitung;
        // Map zur Speicherung der Fahrer (ConcurrentHashMap für Thread-Sicherheit)
        this.fahrerMap = new ConcurrentHashMap<>();
        // Lädt die Fahrerdaten beim Start
        initialisiereFahrerDaten();
    }

    // Lädt die Fahrerdaten aus der CSV-Datei und füllt die Combobox mit den Fahrern
    private void initialisiereFahrerDaten() {
        List<Fahrer> fahrer = interneFahrtenVerarbeitung.loadData();
        // Sortiert die Fahrer nach Personalnummer
        fahrer.sort(Comparator.comparing(Fahrer::getPersonalnummer));
        // Fügt die sortierten Fahrer in die Combobox ein
        for (Fahrer f : fahrer) {
            fahrerMap.put(f.getPersonalnummer(), f);
            fahrerComboBox.addItem(f.toString());
        }
    }

    // Öffnet einen Dialog, um einen neuen Fahrer hinzuzufügen und aktualisiert die Combobox
    public void neuerFahrerHinzufuegen() {
        // Dialogfelder zur Eingabe der Fahrerdaten
        JTextField personalnummerField = new JTextField();
        JTextField vornameField = new JTextField();
        JTextField nachnameField = new JTextField();

        // Zeigt einen Dialog zur Eingabe der neuen Fahrerinformationen
        int option = JOptionPane.showConfirmDialog(null, new Object[]{"Personalnummer:", personalnummerField, "Vorname:", vornameField, "Nachname:", nachnameField},
                "Neuen Fahrer hinzufügen", JOptionPane.OK_CANCEL_OPTION);

        // OK-Button zum Hinzufügen des neuen Fahrers
        if (option == JOptionPane.OK_OPTION) {
            String personalnummer = personalnummerField.getText().trim();
            String vorname = vornameField.getText().trim();
            String nachname = nachnameField.getText().trim();

            // Prüft auf Vollständigkeit der Eingaben
            if (!personalnummer.isEmpty() && !vorname.isEmpty() && !nachname.isEmpty()) {
                // Überprüft, ob die Personalnummer aus 10 Ziffern besteht
                if (personalnummer.matches("\\d{10}")) {
                    // Erstellt einen neuen Fahrer
                    Fahrer neuerFahrer = new Fahrer(personalnummer, vorname, nachname);
                    // Fügt den neuen Fahrer hinzu, falls dieser noch nicht existiert
                    if (!fahrerMap.containsKey(personalnummer)) {
                        fahrerMap.put(personalnummer, neuerFahrer);
                        // Aktualisiere die ComboBox mit den sortierten Fahrern
                        aktualisiereFahrerComboBox();
                        fahrerComboBox.setSelectedItem(neuerFahrer.toString());
                        // Zeigt Bestätigungsmeldung an
                        JOptionPane.showMessageDialog(null, "Neuer Fahrer wurde hinzugefügt.");
                    } else {
                        zeigeFehlermeldung("Dieser Fahrer ist bereits vorhanden.");
                    }
                } else {
                    zeigeFehlermeldung("Die Personalnummer muss aus 10 Ziffern bestehen.");
                }
            } else {
                zeigeFehlermeldung("Bitte füllen Sie alle Felder aus.");
            }
        }
    }

    // Gibt den derzeit ausgewählten Fahrer in der Combobox zurück
    public Fahrer getAusgewaehlterFahrer() {
        // Holt den ausgewählten Fahrer als String aus der ComboBox
        String ausgewaehlterFahrerString = (String) fahrerComboBox.getSelectedItem();
        // Überprüft, ob ein gültiger Fahrer ausgewählt ist
        if (ausgewaehlterFahrerString != null && !ausgewaehlterFahrerString.equals("Bitte Fahrer auswählen")) {
            // Extrahiert die Personalnummer aus dem String
            String personalnummer = ausgewaehlterFahrerString.split(" ")[0];
            // Gibt den Fahrer anhand der Personalnummer zurück
            return fahrerMap.get(personalnummer);
        }
        return null;
    }

    // Aktualisiert die Fahrer-ComboBox und sortiert die Fahrer nach Personalnummer
    private void aktualisiereFahrerComboBox() {
        // Entfernt alle Einträge aus der ComboBox
        fahrerComboBox.removeAllItems();
        // Sortiere die Fahrer nach Personalnummer
        List<Fahrer> fahrerListe = new ArrayList<>(fahrerMap.values());
        fahrerListe.sort((f1, f2) -> f1.getPersonalnummer().compareTo(f2.getPersonalnummer()));
        // Füge die sortierten Fahrer in die ComboBox ein
        for (Fahrer f : fahrerListe) {
            fahrerComboBox.addItem(f.toString());  // Zeigt Personalnummer, Vorname und Nachname an
        }
    }

    // Gibt eine Liste aller Fahrer zurück, die im System vorhanden sind
    public List<Fahrer> getAlleFahrer() {
        return new ArrayList<>(fahrerMap.values());
    }
    // Gibt die Map mit allen Fahrern zurück, dabei ist die Personalnummer der Schlüssel
    public Map<String, Fahrer> getFahrerMap() {
        return fahrerMap;
    }
}