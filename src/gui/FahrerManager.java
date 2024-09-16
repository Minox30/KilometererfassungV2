package gui;

import model.Fahrer;
import persistence.InterneFahrtenVerarbeitung;
import static util.Fehlermeldung.zeigeFehlermeldung;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FahrerManager {

    // Combobox zur Anzeige der Fahrer
    private JComboBox<String> fahrerComboBox;
    // Map zur Speicherung der Fahrerobjekte
    private Map<String, Fahrer> fahrerMap;
    private InterneFahrtenVerarbeitung interneFahrtenVerarbeitung;

    // Konstruktor
    public FahrerManager(JComboBox<String> fahrerComboBox, InterneFahrtenVerarbeitung interneFahrtenVerarbeitung) {
        this.fahrerComboBox = fahrerComboBox;
        this.interneFahrtenVerarbeitung = interneFahrtenVerarbeitung;
        this.fahrerMap = new ConcurrentHashMap<>();
        initialisiereFahrerDaten();
    }

    // Lädt die Fahrerdaten aus der CSV-Datei und füllt die Combobox mit den Fahrern
    private void initialisiereFahrerDaten() {
        List<Fahrer> fahrer = interneFahrtenVerarbeitung.loadData();
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

        int option = JOptionPane.showConfirmDialog(null, new Object[]{"Personalnummer:", personalnummerField, "Vorname:", vornameField, "Nachname:", nachnameField},
                "Neuen Fahrer hinzufügen", JOptionPane.OK_CANCEL_OPTION);

        // OK-Button zum Hinzufügen des neuen Fahrers
        if (option == JOptionPane.OK_OPTION) {
            String personalnummer = personalnummerField.getText().trim();
            String vorname = vornameField.getText().trim();
            String nachname = nachnameField.getText().trim();

            // Prüft auf Vollständigkeit der Eingaben
            if (!personalnummer.isEmpty() && !vorname.isEmpty() && !nachname.isEmpty()) {
                if (personalnummer.matches("\\d{10}")) {
                    Fahrer neuerFahrer = new Fahrer(personalnummer, vorname, nachname);
                    // Fügt den neuen Fahrer hinzu, falls dieser noch nicht existiert
                    if (!fahrerMap.containsKey(personalnummer)) {
                        fahrerMap.put(personalnummer, neuerFahrer);
                        fahrerComboBox.addItem(neuerFahrer.toString());
                        fahrerComboBox.setSelectedItem(neuerFahrer.toString());
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
        String ausgewaehlterFahrerString = (String) fahrerComboBox.getSelectedItem();
        // Überprüft, ob ein gültiger Fahrer ausgewählt ist
        if (ausgewaehlterFahrerString != null && !ausgewaehlterFahrerString.equals("Bitte Fahrer auswählen")) {
            String personalnummer = ausgewaehlterFahrerString.split(" ")[0];
            return fahrerMap.get(personalnummer);
        }
        return null;
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