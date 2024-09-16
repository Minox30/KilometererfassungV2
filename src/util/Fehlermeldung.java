package util;

import javax.swing.*;

// Hilfsklasse für Fehlermeldungen mit Unterscheidung für einen besseren Überblick
public class Fehlermeldung {

    // Hilfsmethode zum Anzeigen von Fehlermeldungen
    public static void zeigeFehlermeldung(String nachricht){
        JOptionPane.showMessageDialog(null,nachricht,"Fehler",JOptionPane.ERROR_MESSAGE);
    }
    // Hilfsmethode zum Anzeigen von Fehlermeldungen mit Exception-Details
    public static void zeigeFehlermeldung(String nachricht, Exception e){
        JOptionPane.showMessageDialog(null,nachricht + "\n" + e.getMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
    }
    // Hilfsmethode zum Anzeigen von Fehlermeldungen in der externen Datei
    public static void zeigeExterneFehlermeldung(String nachricht){
        JOptionPane.showMessageDialog(null,nachricht,"Fehler in addfahrten-Datei",JOptionPane.WARNING_MESSAGE);
    }
    // Hilfsmethode zum Anzeigen von Fehlermeldungen in der externen Datei mit Exception Details
    public static void zeigeExterneFehlermeldung(String nachricht,Exception e){
        JOptionPane.showMessageDialog(null,nachricht + "\n" +e.getMessage(),"Fehler in addfahrten-Datei",JOptionPane.WARNING_MESSAGE);
    }
}
