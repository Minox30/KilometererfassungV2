package gui;

import model.Fahrer;
import persistence.InterneFahrtenVerarbeitung;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FahrerManager {

    private JComboBox<String> fahrerComboBox;
    private Map<String, Fahrer> fahrerMap;
    private InterneFahrtenVerarbeitung interneFahrtenVerarbeitung;

    public FahrerManager(JComboBox<String> fahrerComboBox, InterneFahrtenVerarbeitung interneFahrtenVerarbeitung) {
        this.fahrerComboBox = fahrerComboBox;
        this.interneFahrtenVerarbeitung = interneFahrtenVerarbeitung;
        this.fahrerMap = new HashMap<>();
        initialisiereFahrerDaten();
    }

    private void initialisiereFahrerDaten() {
        List<Fahrer> fahrer = interneFahrtenVerarbeitung.loadData(); // Lädt die Fahrer-Daten aus der CSV-Datei
        fahrerComboBox.addItem("Bitte Fahrer auswählen");
        for (Fahrer f : fahrer) {
            fahrerMap.put(f.getPersonalnummer(), f);
            fahrerComboBox.addItem(f.toString());
        }
    }

    public void neuerFahrerHinzufuegen() {
        JTextField personalnummerField = new JTextField();
        JTextField vornameField = new JTextField();
        JTextField nachnameField = new JTextField();

        int option = JOptionPane.showConfirmDialog(null, new Object[]{"Personalnummer:", personalnummerField, "Vorname:", vornameField, "Nachname:", nachnameField},
                "Neuen Fahrer hinzufügen", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String personalnummer = personalnummerField.getText().trim();
            String vorname = vornameField.getText().trim();
            String nachname = nachnameField.getText().trim();

            if (!personalnummer.isEmpty() && !vorname.isEmpty() && !nachname.isEmpty()) {
                if (personalnummer.matches("\\d{10}")) {
                    Fahrer neuerFahrer = new Fahrer(personalnummer, vorname, nachname);
                    if (!fahrerMap.containsKey(personalnummer)) {
                        fahrerMap.put(personalnummer, neuerFahrer);
                        fahrerComboBox.addItem(neuerFahrer.toString());
                        fahrerComboBox.setSelectedItem(neuerFahrer.toString());
                        JOptionPane.showMessageDialog(null, "Neuer Fahrer wurde hinzugefügt.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Dieser Fahrer ist bereits vorhanden.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Die Personalnummer muss aus 10 Ziffern bestehen.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.");
            }
        }
    }

    public Fahrer getAusgewaehlterFahrer() {
        String ausgewaehlterFahrerString = (String) fahrerComboBox.getSelectedItem();
        if (ausgewaehlterFahrerString != null && !ausgewaehlterFahrerString.equals("Bitte Fahrer auswählen")) {
            String personalnummer = ausgewaehlterFahrerString.split(" ")[0];
            return fahrerMap.get(personalnummer);
        }
        return null;
    }

    public List<Fahrer> getAlleFahrer() {
        return new ArrayList<>(fahrerMap.values());
    }
}