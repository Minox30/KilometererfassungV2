package app;

import gui.KilometererfassungView;

import javax.swing.*;
// Startet die GUI zur Kilometererfassung
public class Main {
    // Konfiguriert das Look-and-Feel und startet die Anwendung
    public static void main(String[] args) {
        try {
           // Versucht das "Nimbus" Look-and-Feel zu setzen, wenn es verfügbar ist
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    // Bricht die Schleife ab, sobald Nimbus gefunden und gesetzt wurde
                    break;
                }
            }
            // Falls Nimbus nicht verfügbar ist, wird das Standard Look-and-Feel verwendet
        } catch (Exception e) {
        }
        // Startet die GUI im Event-Dispatch-Thread
        SwingUtilities.invokeLater(() ->{
            try {
                // Erstellt und zeigt die GUI für die Kilometererfassung
                KilometererfassungView gui = new KilometererfassungView();
                // Macht das Fenster sichtbar
                gui.setVisible(true);
            } catch (Exception e) {
                // Ausgabe der Exception in der Konsole, da die GUI möglicherweise noch nicht existiert
                e.printStackTrace();
                System.err.println("Fehler beim Starten der Anwendung" + e.getMessage());
            }
        });
    }
}
