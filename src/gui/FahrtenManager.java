package gui;

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
    private JTable fahrtenTable;
    // Label zur Anzeige der Gesamtkilometer
    private JLabel gesamtkilometerLabel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Konstruktor
    public FahrtenManager(JTable fahrtenTable, JLabel gesamtkilometerLabel) {
        this.fahrtenTable = fahrtenTable;
        this.gesamtkilometerLabel = gesamtkilometerLabel;
    }

    // Fügt dem ausgewählten Fahrer eine neue Fahrt hinzu und aktualisiert die GUI
    public void neueFahrtHinzufuegen(Fahrer ausgewaehlterFahrer, String datumString, String startort, String kilometer) {
        // Überprüft, ob ein gültiger Fahrer ausgewählt ist
        if (ausgewaehlterFahrer == null) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie zuerst einen Fahrer aus.");
            return;
        }

        // Überprüft, ob alle Felder ausgefüllt sind
        if (datumString.isEmpty() || startort.isEmpty() || kilometer.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.");
            return;
        }

        try {
            // Konvertiert das Datum vom String in ein LocalDate-Objekt
            LocalDate datum = LocalDate.parse(datumString, DATE_FORMATTER);

            // Überprüft, ob das Datum in der Zukunft liegt
            if (datum.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "Das Datum darf nicht in der Zukunft liegen.");
                return;
            }

            // Konvertiert die Kilometeranzahl vom String in einen Integer
            int km = Integer.parseInt(kilometer);
            // Überprüft, on die Kilometer negativ sind
            if (km < 0) {
                JOptionPane.showMessageDialog(null, "Kilometeranzahl darf nicht negativ sein.");
                return;
            }

            // Erstellt ein neues Fahrt-Objekt und fügt es dem Fahrer hinzu
            Fahrt neueFahrt = new Fahrt(datum, startort, km);
            ausgewaehlterFahrer.addFahrt(neueFahrt);

            // Aktualisiert die Fahrten-Tabelle und Gesamtkilometeranzeige
            updateFahrtenTabelle(ausgewaehlterFahrer);
            updateGesamtkilometer(ausgewaehlterFahrer);

            JOptionPane.showMessageDialog(null, "Neue Fahrt wurde hinzugefügt.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie das Datum im Format TT.MM.JJJJ ein.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Zahl für die Kilometer ein.");
        }
    }

    // Aktualisiert die GUI-Anzeige für den ausgewählten Fahrer
    public void updateFahrerUI(Fahrer fahrer) {
        if (fahrer != null) {
            updateFahrtenTabelle(fahrer);
            updateGesamtkilometer(fahrer);
        } else {
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