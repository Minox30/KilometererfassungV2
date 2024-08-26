import gui.KilometererfassungView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Schleife durchläuft alle Look and Feels auf dem System und wird beendet, sobald Nimbus gefunden und gesetzt wurde.
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
