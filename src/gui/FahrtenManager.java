package gui;

import model.Fahrer;
import model.Fahrt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FahrtenManager {

    private JTable fahrtenTable;
    private JLabel gesamtkilometerLabel;
    private static final DateTimeFormatter DATE_FORMTATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public FahrtenManager(JTable fahrtenTable, JLabel gesamtkilometerLabel) {
        this.fahrtenTable = fahrtenTable;
        this.gesamtkilometerLabel = gesamtkilometerLabel;
    }

    public void neueFahrtHinzufuegen(Fahrer ausgewaehlterFahrer, String datumString, String startort, String kilometer) {
        if (ausgewaehlterFahrer == null) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie zuerst einen Fahrer aus.");
            return;
        }

        if (datumString.isEmpty() || startort.isEmpty() || kilometer.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.");
            return;
        }

        try {
            LocalDate datum = LocalDate.parse(datumString, DATE_FORMTATTER);

            if (datum.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "Das Datum darf nicht in der Zukunft liegen.");
                return;
            }

            int km = Integer.parseInt(kilometer);

            if (km < 0) {
                JOptionPane.showMessageDialog(null, "Kilometeranzahl darf nicht negativ sein.");
                return;
            }

            Fahrt neueFahrt = new Fahrt(datum, startort, km);
            ausgewaehlterFahrer.addFahrt(neueFahrt);

            updateFahrtenTabelle(ausgewaehlterFahrer);
            updateGesamtkilometer(ausgewaehlterFahrer);

            JOptionPane.showMessageDialog(null, "Neue Fahrt wurde hinzugefügt.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie das Datum im Format TT.MM.JJJJ ein.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Zahl für die Kilometer ein.");
        }
    }

    public void updateFahrerUI(Fahrer fahrer) {
        if (fahrer != null) {
            updateFahrtenTabelle(fahrer);
            updateGesamtkilometer(fahrer);
        } else {
            clearFahrtenTabelle();
            gesamtkilometerLabel.setText("Gesamtkilometer: 0");
        }
    }

    private void updateFahrtenTabelle(Fahrer fahrer) {
        DefaultTableModel model = (DefaultTableModel) fahrtenTable.getModel();
        model.setRowCount(0);
        for (Fahrt fahrt : fahrer.getFahrten()) {
            model.addRow(new Object[]{fahrt.getDatum().format(DATE_FORMTATTER), fahrt.getStartort(), fahrt.getKilometer()});
        }
    }

    private void updateGesamtkilometer(Fahrer fahrer) {
        int gesamtKilometer = fahrer.berechneGesamtKilometer();
        gesamtkilometerLabel.setText("Gesamtkilometer: " + gesamtKilometer);
    }

    private void clearFahrtenTabelle() {
        DefaultTableModel model = (DefaultTableModel) fahrtenTable.getModel();
        model.setRowCount(0);
    }
}