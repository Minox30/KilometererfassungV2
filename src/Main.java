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
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Startet die GUI im Event-Dispatch-Thread
        SwingUtilities.invokeLater(() ->{
            try {
                KilometererfassungView gui = new KilometererfassungView();
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Fehler beim Starten der Anwendung" + e.getMessage());
            }
        });
    }
}
