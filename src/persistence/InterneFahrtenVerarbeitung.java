package persistence;

import static util.Fehlermeldung.zeigeFehlermeldung;
import model.Fahrt;
import model.Fahrer;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Klasse zum Laden und Speichern von Fahrerdaten aus einer CSV-Datei
public class InterneFahrtenVerarbeitung {
    // Name der Datei, welche die Daten speichert
    private static final String CSV_File = "kilometer.csv";
    // Datumformatierer zum Parsen und Formatieren von Datumsangaben
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Methode zum Laden der Fahrer- und Fahrtendaten aus der CSV-Datei
    public List<Fahrer> loadData() {
        // Liste aller Fahrer
        List<Fahrer> fahrer = new ArrayList<>();
        // Map zum Zuordnen von Personalnummer zu Fahrer
        Map<String, Fahrer> fahrerMap = new HashMap<>();
        // Die CSV-Datei, die die Daten speichert
        File file = new File(CSV_File);

        //Überprüft, ob die Datei existiert
        if (!file.exists()) {
            try {
                // Erstellt die Datei, falls sie nicht existiert
                if (file.createNewFile()) {
                    System.out.println("Die Datei wurde erfolgreich erstellt: " + CSV_File);
                } else {
                    zeigeFehlermeldung("Die Datei konnte nicht erstellt werden.");
                    // Rückgabe einer leeren Liste, wenn die Erstellung fehlschlägt
                    return fahrer;
                }
            } catch (IOException e) {
                zeigeFehlermeldung("Fehler beim Erstellen der Datei " + CSV_File + ": " + e.getMessage());
                // Rückgabe einer leeren Liste bei einem Fehler
                return fahrer;
            }
        }
        // Nutzung des BufferedReader zum Lesen der Datei.
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // List die Datei zeilenweise ein
            while ((line = br.readLine()) != null) {
                // Teilt die Zeilen an jedem Komma
                String[] values = line.split(",");
                try {
                    // Bei 3 enthaltenen Elementen in einer Zeile wird ein Fahrer erstellt und zur Liste hinzugefügt
                    if (values.length == 3) {
                        Fahrer f = new Fahrer(values[0], values[1], values[2]);
                        fahrer.add(f);
                        fahrerMap.put(values[0], f);
                        // Bei 4 enthaltenen Elementen handelt es sich um eine Fahrt, die dem entsprechenden Fahrer hinzugefügt wird
                    } else if (values.length == 4) {
                        // Erstellt eine neue Fahrt und parst das Datum.
                        Fahrt fahrt = new Fahrt(LocalDate.parse(values[1], DATE_FORMATTER), values[2], Integer.parseInt(values[3]));
                        // Findet den zugehörigen Fahrer anhand der Personalnummer
                        Fahrer f = fahrerMap.get(values[0]);
                        // Fügt die Fahrt dem Fahrer hinzu, wenn dieser existiert
                        if (f != null) {
                            f.addFahrt(fahrt);
                        } else {
                            zeigeFehlermeldung("Kein passender Fahrer für Personalnummer " + values[0] + " gefunden.");
                        }
                    } else {
                        // Zeigt eine Fehlermeldung, wenn das Dateiformat nicht stimmt
                        zeigeFehlermeldung("Ungültiges Zeilenformat in der Zeile " + line);
                    }
                } catch (DateTimeParseException e) {
                    // Zeigt eine Fehlermeldung bei einem ungültigen Datumsformat
                    zeigeFehlermeldung("Ungültiges Datumsformat in der Zeile " + line);
                } catch (NumberFormatException e) {
                    // Zeigt eine Fehlermeldung bei einer ungültigen Kilometerangabe
                    zeigeFehlermeldung("Ungültige Kilometerangabe in der Zeile " + line);
                }
            }
        } catch (IOException e) {
            // Zeigt eine Fehlermeldung, wenn ein Fehler beim Lesen der Datei auftritt
            zeigeFehlermeldung("Fehler beim Lesen der Datei " + CSV_File, e);
        }
        // Gibt die Liste der Fahrer zurück
        return fahrer;
    }

    // Speichert die Fahrer- und Fahrtendaten in der CSV-Datei
    public void saveData(List<Fahrer> fahrer) {
        // Versucht, die Datei zu öffnen und die Daten zu schreiben
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_File))) {
            for (Fahrer f : fahrer) {
                // Schreibt die Fahrer-Informationen in die Datei.
                bw.write(String.format("%s,%s,%s%n", f.getPersonalnummer(), f.getVorname(), f.getNachname()));
                for (Fahrt fahrt : f.getFahrten()) {
                    // Schreibt die Fahrt-Informationen in die Datei.
                    bw.write(String.format("%s,%s,%s,%s%n",
                            f.getPersonalnummer(),
                            fahrt.getDatum().format(DATE_FORMATTER),
                            fahrt.getStartort(),
                            fahrt.getKilometer()));
                }
            }
        } catch (IOException e) {
            // Zeigt eine Fehlermeldung, wenn ein Fehler beim Speichern der Datei auftritt
            zeigeFehlermeldung("Fehler beim Speichern der Datei " + CSV_File, e);
        }
    }
}