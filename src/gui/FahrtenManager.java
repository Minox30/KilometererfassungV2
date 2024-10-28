package gui;

import static util.Fehlermeldung.zeigeFehlermeldung;
import model.Fahrer;
import model.Fahrt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Verwaltet die Fahrten eines ausgewählten Fahrers und aktualisiert die Darstellung in der GUI
public class FahrtenManager {

    // Tabelle zur Darstellung aller Fahrten eines Fahrers
    private final JTable fahrtenTable;
    // Label zur Anzeige der Gesamtkilometer eines Fahrers
    private final JLabel gesamtkilometerLabel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Konstruktor: Initialisiert den Fahrtenmanager mit der Tabelle und dem Gesamtkilometerlabel
    public FahrtenManager(JTable fahrtenTable, JLabel gesamtkilometerLabel) {
        // Tabelle zur Darstellung aller Fahrten eines Fahrers
        this.fahrtenTable = fahrtenTable;
        // Label zur Anzeige der Gesamtkilometer eines Fahrers
        this.gesamtkilometerLabel = gesamtkilometerLabel;
    }

    // Fügt dem ausgewählten Fahrer eine neue Fahrt hinzu und aktualisiert die GUI
    public void neueFahrtHinzufuegen(Fahrer ausgewaehlterFahrer, String datumString, String startort, String kilometer) {
        // Überprüft, ob ein gültiger Fahrer ausgewählt ist
        if (ausgewaehlterFahrer == null) {
            zeigeFehlermeldung("Bitte wählen Sie zuerst einen Fahrer aus.");
            return;
        }
        // Überprüft, ob alle Felder ausgefüllt sind
        if (datumString.isEmpty() || startort.isEmpty() || kilometer.isEmpty()) {
            zeigeFehlermeldung("Bitte füllen Sie alle Felder aus.");
            return;
        }
        try {
            // Konvertiert das Datum vom String in ein LocalDate-Objekt
            LocalDate datum = LocalDate.parse(datumString, DATE_FORMATTER);
            // Überprüft, ob das Datum in der Zukunft liegt
            if (datum.isAfter(LocalDate.now())) {
                zeigeFehlermeldung("Das Datum darf nicht in der Zukunft liegen.");
                return;
            }
            // Konvertiert die Kilometeranzahl vom String in einen Integer
            int km = Integer.parseInt(kilometer);
            // Überprüft, on die Kilometer negativ sind
            if (km < 0) {
                zeigeFehlermeldung("Gefahrene Kilometer dürfen nicht negativ sein.");
                return;
            }
            // Erstellt ein neues Fahrt-Objekt und fügt es dem Fahrer hinzu
            Fahrt neueFahrt = new Fahrt(datum, startort, km);
            ausgewaehlterFahrer.addFahrt(neueFahrt);

            // Aktualisiert die Fahrten-Tabelle und Gesamtkilometeranzeige
            updateFahrtenTabelle(ausgewaehlterFahrer);
            updateGesamtkilometer(ausgewaehlterFahrer);

            // zeigt eine Bestätigungsmeldung an
            JOptionPane.showMessageDialog(null, "Neue Fahrt wurde hinzugefügt.");
        } catch (DateTimeParseException e) {
            zeigeFehlermeldung("Bitte geben Sie das Datum im Format TT.MM.JJJJ ein.");
        } catch (NumberFormatException ex) {
            zeigeFehlermeldung("Bitte geben Sie eine gültige Zahl für die Kilometer ein.");
        }
    }

    // Aktualisiert die GUI-Anzeige für den ausgewählten Fahrer
    public void updateFahrerUI(Fahrer fahrer) {
        if (fahrer != null) {
            updateFahrtenTabelle(fahrer);
            updateGesamtkilometer(fahrer);
        } else {
            // Tabelle wird geleert und Gesamtkilometer auf 0 gesetzt, wenn kein Fahrer ausgewählt wurde
            clearFahrtenTabelle();
            gesamtkilometerLabel.setText("Gesamtkilometer: 0");
        }
    }

    // Aktualisiert die Fahrten-Tabelle für den ausgewählten Fahrer
    private void updateFahrtenTabelle(Fahrer fahrer) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) fahrtenTable.getModel();
            model.setRowCount(0);
            // Fügt jede Fahrt als neue Zeile in die Tabelle ein
            for (Fahrt fahrt : fahrer.getFahrten()) {
                model.addRow(new Object[]{fahrt.getDatum().format(DATE_FORMATTER), fahrt.getStartort(), fahrt.getKilometer()});
            }
        });
    }

    // Aktualisiert die Anzeige der Gesamtkilometer für den ausgewählten Fahrer
    private void updateGesamtkilometer(Fahrer fahrer) {
        SwingUtilities.invokeLater(() -> {
            // Berechnet die Gesamtkilometer des Fahrers und aktualisiert das Label
            int gesamtKilometer = fahrer.berechneGesamtKilometer();
            gesamtkilometerLabel.setText("Gesamtkilometer: " + gesamtKilometer);
        });
    }

    // Leert die Fahrten-Tabelle in der GUI
    private void clearFahrtenTabelle() {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) fahrtenTable.getModel();
            model.setRowCount(0);
        });
    }
}