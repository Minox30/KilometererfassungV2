package gui;

import persistence.InterneFahrtenVerarbeitung;
import persistence.ExterneFahrtenVerarbeitung;

import javax.swing.SwingUtilities;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class KilometererfassungView extends JFrame {
    // GUI-Komponenten
    private JPanel mainPanel;
    private JComboBox fahrerComboBox;
    private JButton neuerFahrerButton;
    private JTextField datumField;
    private JTextField startortField;
    private JTextField kilometerField;
    private JButton fahrtHinzufuegenButton;
    private JButton beendenButton;
    private JTable fahrtenTable;
    private JScrollPane fahrtenScrollPane;
    private JLabel gesamtkilometerLabel;

    private FahrtenManager fahrtenManager;
    private FahrerManager fahrerManager;
    private ExterneFahrtenVerarbeitung externeFahrtenVerarbeitung;
    private InterneFahrtenVerarbeitung interneFahrtenVerarbeitung;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public KilometererfassungView() {
        SwingUtilities.invokeLater(() -> {
            interneFahrtenVerarbeitung= new InterneFahrtenVerarbeitung();
            createUIComponents();
            fahrtenManager= new FahrtenManager(fahrtenTable, gesamtkilometerLabel);
            fahrerManager = new FahrerManager(fahrerComboBox, interneFahrtenVerarbeitung);
            addListeners();
            this.setContentPane(mainPanel);
            this.pack();
            this.setVisible(true);
            externeFahrtenVerarbeitung = ExterneFahrtenVerarbeitung.getInstance(fahrerManager.getFahrerMap(),fahrtenManager,DATE_FORMATTER);
            externeFahrtenVerarbeitung.start();
        });
    }

    // Initialisiert und konfiguriert die GUI-Komponenten
    private void createUIComponents() {
        // Hauptpanel mit Borderlayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Oberes Panel für Fahrer-Auswahl
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Komponenten für die Fahrerauswahl
        fahrerComboBox = new JComboBox<>();
        fahrerComboBox.addItem("Bitte Fahrer auswählen");
        fahrerComboBox.setToolTipText("Wählen Sie einen Fahrer aus der Liste aus.");

        // Button für das Hinzufügen eines neuen Fahrers
        neuerFahrerButton = new JButton("Neuer Fahrer");
        neuerFahrerButton.setToolTipText("Hier klicken, um einen neuen Fahr hinzuzufügen");

        // Komponenten werden dem oberen Panel zugeordnet
        topPanel.add(new JLabel("Fahrer:"));
        topPanel.add(fahrerComboBox);
        topPanel.add(neuerFahrerButton);

        // Tabelle für die Fahrten des ausgewählten Fahrers
        fahrtenTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Datum", "Startort", "Kilometer"}
        ));

        // ScrollPane für die Tabelle
        fahrtenScrollPane = new JScrollPane(fahrtenTable);

        // Label für die Anzeige der Gesamtkilometer
        gesamtkilometerLabel = new JLabel("Gesamtkilometer: 0");

        // Unteres Panel für die Eingabe einer neuen Fahrt und den Beenden-Button
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Textfelder für die Eingabe einer neuen Fahrt
        datumField = new JTextField(10);
        datumField.setToolTipText("Geben Sie das Datum im Format TT.MM.JJJJ ein");
        // Setzt das aktuelle Datum als Standartwert ein, um die Eingabe zu erleichtern
        datumField.setText(LocalDate.now().format(DATE_FORMATTER));

        startortField = new JTextField(15);
        startortField.setToolTipText("Geben Sie den Startort der Fahrt ein");

        kilometerField = new JTextField(5);
        kilometerField.setToolTipText("Geben Sie die Anzahl der gefahrenen Kilometer ein");

        // Button zum Hinzufügen einer neuen Fahrt
        fahrtHinzufuegenButton = new JButton("Fahrt hinzufügen");
        // Button zum Beenden der Anwendung
        beendenButton = new JButton("Beenden");

        // Komponenten werden dem unteren Panel hinzugefügt
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        bottomPanel.add(new JLabel("Neue Fahrt erfassen:"), gbc);

        // Eingabefeld für das Datum
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        bottomPanel.add(new JLabel("Datum:"), gbc);
        gbc.gridx = 1;
        bottomPanel.add(datumField, gbc);

        // Eingabefeld für den Startort
        gbc.gridx = 2;
        bottomPanel.add(new JLabel("Startort:"), gbc);
        gbc.gridx = 3;
        bottomPanel.add(startortField, gbc);

        // Eingabefeld für die Kilometer
        gbc.gridx = 4;
        bottomPanel.add(new JLabel("Kilometer:"), gbc);
        gbc.gridx = 5;
        bottomPanel.add(kilometerField, gbc);

        // Button zum Hinzufügen der Fahrt
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        bottomPanel.add(fahrtHinzufuegenButton, gbc);

        // Button zum Beenden der Anwendung
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        bottomPanel.add(beendenButton, gbc);

        // Komponenten werden dem HauptPanel hinzugefügt
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(fahrtenScrollPane, BorderLayout.CENTER);

        // Unteres Panel für Gesamtkilometer und die Eingabefelder für eine neue Fahrt
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(gesamtkilometerLabel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.CENTER);

        mainPanel.add(southPanel, BorderLayout.SOUTH);
    }
// Fügt Event-Listener zu den GUI-Komponenten hinzu
    private void addListeners() {
        // Listener für die Auswahl eines Fahrers in der Combobox
        fahrerComboBox.addActionListener(e -> {
            if (fahrerComboBox.getSelectedIndex() > 0) {
                fahrerComboBox.removeItem("Bitte Fahrer auswählen");
            }
            fahrtenManager.updateFahrerUI(fahrerManager.getAusgewaehlterFahrer());
        });
        // Listener zum Hinzufügen eines neuen Fahrers
        neuerFahrerButton.addActionListener(e -> fahrerManager.neuerFahrerHinzufuegen());
        // Listener zum Hinzufügen einer neuen Fahrt
        fahrtHinzufuegenButton.addActionListener(e -> fahrtenManager.neueFahrtHinzufuegen(fahrerManager.getAusgewaehlterFahrer(), datumField.getText(), startortField.getText(), kilometerField.getText()));
        // Listener zum Speichern der Daten und Beenden der Anwendung
        beendenButton.addActionListener(e -> {
            interneFahrtenVerarbeitung.saveData(fahrerManager.getAlleFahrer());
            ExterneFahrtenVerarbeitung.shutdownInstance();
            System.exit(0);
        });
        // Listener zur Aktualisierung der Fahrtenanzeige bei einem Fahrerwechsel
        fahrerComboBox.addActionListener(e -> fahrtenManager.updateFahrerUI(fahrerManager.getAusgewaehlterFahrer()));
    }
}


