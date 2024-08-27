package persistence;

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

import static util.Fehlermeldung.zeigeFehlermeldung;
// Klasse zum Laden und Speichern von Fahrerdaten aus einer CSV-Datei
public class InterneFahrtenVerarbeitung {
    private static final String CSV_File = "kilometer.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Methode zum Laden der Fahrer- und Fahrtendaten aus der CSV-Datei
    public List<Fahrer> loadData() {
        List<Fahrer> fahrer = new ArrayList<>();
        Map<String, Fahrer> fahrerMap = new HashMap<>();
        File file = new File(CSV_File);

        //Überprüft, ob die Datei existiert und falls nicht, wird diese erstellt.
        if (!file.exists()) {
            zeigeFehlermeldung("Die Datei konnte nicht gefunden werden.");
            return fahrer;
        }
        // Nutzung des BufferedReader zum Lesen der Datei.
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // List die Datei Zeile für Zeile ein
            while ((line = br.readLine()) != null) {
                // Der String line wird an jedem Komma geteilt und das Ergebnis in values gespeichert.
                String[] values = line.split(",");
                try {
                    // Bei 3 enthaltenen Elementen in einer Zeile wird ein Fahrer erstellt und zur Liste hinzugefügt
                    if (values.length == 3) {
                        Fahrer f = new Fahrer(values[0], values[1], values[2]);
                        fahrer.add(f);
                        fahrerMap.put(values[0], f);
                        // Bei 4 enthaltenen Elementen handelt es sich um eine Fahrt, die dem entsprechenden Fahrer hinzugefügt wird
                    } else if (values.length == 4) {
                        // Erstellt ein neues Fahrt-Objekt.
                        Fahrt fahrt = new Fahrt(LocalDate.parse(values[1], DATE_FORMATTER), values[2], Integer.parseInt(values[3]));
                        Fahrer f = fahrerMap.get(values[0]);
                        if (f != null) {
                            f.addFahrt(fahrt);
                        } else {
                            zeigeFehlermeldung("Kein passender Fahrer für Personalnummer " + values[0] + " gefunden.");
                        }
                    } else {
                        zeigeFehlermeldung("Ungültiges Zeilenformat in der Zeile " + line);
                    }
                } catch (DateTimeParseException e) {
                    zeigeFehlermeldung("Ungültiges Datumsformat in der Zeile " + line);
                } catch (NumberFormatException e) {
                    zeigeFehlermeldung("Ungültige Kilometerangabe in der Zeile " + line);
                }
            }
        } catch (IOException e) {
            zeigeFehlermeldung("Fehler beim Lesen der Datei");
        }
        return fahrer;
    }

    // Methode zum Speichern der Daten in der CSV-Datei
    public void saveData(List<Fahrer> fahrer) {
        // Öffnet einen BufferedWriter für die CSV-Datei.
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
            zeigeFehlermeldung("Fehler beim Speichern der Datei");
        }
    }
}