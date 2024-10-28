package persistence;

import static util.Fehlermeldung.zeigeExterneFehlermeldung;
import static util.Fehlermeldung.zeigeFehlermeldung;
import gui.FahrtenManager;
import model.Fahrer;
import model.Fahrt;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

// Thread zur Verarbeitung von externen Fahrerdaten aus der "addfahrten.csv" Datei
public class ExterneFahrtenVerarbeitung extends Thread {
    // Name der Datei mit den externen Fahrerdaten
    private static final String EXTERNE_FAHRTEN = "addfahrten.csv";
    // Formatter für die Datumskonvertierung
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    // Pfad zur Datei
    private final Path externeFahrten;
    // Map zur Zuordnung von Personalnummern zu Fahrerobjekten
    private final Map<String, Fahrer> fahrerMap;
    // Referenz zur GUI, um die Oberfläche zu aktualisieren
    private final FahrtenManager fahrtenManager;
    // Flag, das den Status des Threads kontrolliert (laufend oder nicht)
    private volatile boolean running = true;
    // Singleton-Instanz dieser Klasse
    private static ExterneFahrtenVerarbeitung instance;

    // Konstruktor für die ExterneFahrtenVerwaltung
    private ExterneFahrtenVerarbeitung(Map<String, Fahrer> fahrerMap, FahrtenManager fahrtenManager) {
        this.externeFahrten = Paths.get(EXTERNE_FAHRTEN);
        this.fahrerMap = fahrerMap;
        this.fahrtenManager = fahrtenManager;
    }

    // Gibt die ExterneFahrtenVerwaltung-Instanz zurück oder erstellt diese.
    public static ExterneFahrtenVerarbeitung getInstance(Map<String, Fahrer> fahrerMap, FahrtenManager fahrtenManager) {
        if (instance == null) {
            instance = new ExterneFahrtenVerarbeitung(fahrerMap, fahrtenManager);
        }
        return instance;
    }

    // Beendet die Instanz und den Thread
    public static void shutdownInstance() {
        if (instance != null) {
            // Setzt das "running"-Flag auf false und unterbricht den Thread
            instance.shutdown();
            try {
                // Wartet maximal 5 Sekunden, bis der Thread beendet ist
                instance.join(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Setzt die Instanz auf null, damit sie bei Bedarf neu erstellt werden kann
            instance = null;
        }
    }

    // Methode, die den Thread ausführt und die Datei überwacht
    @Override
    public void run() {
        while (running && !isInterrupted()) {
            try {
                // Überprüft, ob die Datei existiert
                if (Files.exists(externeFahrten)) {
                    verarbeiteDatei();
                }

                // Wartet 60 Sekunden bis zur nächsten Überprüfung
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // Setzt den Thread-Interrupt-Status und beendet die Schleife
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                // Zeigt eine Fehlermeldung an, wenn ein Fehler beim Lesen der Datei auftritt
                zeigeExterneFehlermeldung("Fehler beim lesen der Datei: " + EXTERNE_FAHRTEN, e);
            }
        }
    }

    // Liest die Datei aus und fügt die neuen Fahrten den Fahrern hinzu
    private void verarbeiteDatei() throws IOException {
        boolean dateiVerarbeitet = false;
        // Öffnet die Datei zum Lesen
        try (BufferedReader reader = new BufferedReader(new FileReader(externeFahrten.toFile()))) {
            String line;
            // Liest jede Zeile der Datei und verarbeitet diese
            while ((line = reader.readLine()) != null) {
                verarbeiteZeilen(line);
            }
            // Setzt das Flag, nachdem alle Zeilen verarbeitet wurden
            dateiVerarbeitet = true;
        } catch (IOException e) {
            // Zeigt eine Fehlermeldung an, wenn ein Fehler beim Lesen der Datei auftritt
            zeigeExterneFehlermeldung("Fehler beim lesen der Datei " + EXTERNE_FAHRTEN, e);
        } finally {
            // Löscht die Datei, nachdem diese verarbeitet wurde
            try {
                Files.deleteIfExists(externeFahrten);
            } catch (IOException e) {
                // Zeigt eine Fehlermeldung an, wenn die Datei nicht gelöscht werden kann
                zeigeExterneFehlermeldung("Fehler beim Löschen der Datei");
            }
        }

    }

    // Verarbeitet die einzelnen Zeilen aus der Datei
    private void verarbeiteZeilen(String line) {
        // Teilt die Zeile in ihre Bestandteile (Personalnummer, Datum, Startort, Kilometer)
        String[] parts = line.split(",");
        // Überprüft, ob die Zeile das richtige Format (4 Teile) hat
        if (parts.length != 4) {
            zeigeExterneFehlermeldung("Ungültiges Zeilenformat in Zeile: " + line);
            return;
        }
        try {
            String personalnummer = parts[0];
            LocalDate datum = LocalDate.parse(parts[1], dateFormatter);
            String startort = parts[2];
            int kilometer = Integer.parseInt(parts[3]);

            // Überprüft, ob das Datum in der Zukunft liegt
            if(datum.isAfter(LocalDate.now())) {
                zeigeExterneFehlermeldung("Das Datum darf nicht in der Zukunft liegen: "  +line);
                return;
            }
            // Überprüft, ob die Kilometerangabe negativ ist
            if (kilometer < 0) {
                zeigeExterneFehlermeldung("Negative Kilometerangabe in Zeile: " + line);
                return;
            }
            // Findet den Fahrer anhand der Personalnummer
            Fahrer fahrer = fahrerMap.get(personalnummer);
            if (fahrer == null) {
                zeigeFehlermeldung("Fahrer mit der Personalnummer " + personalnummer + " nicht gefunden.");
                return;
            }
            // Erstellt eine neue Fahrt und fügt sie dem Fahrer hinzu
            Fahrt neueFahrt = new Fahrt(datum, startort, kilometer);
            fahrer.addFahrt(neueFahrt);

            // Aktualisiert die GUI, damit die neue Fahrt angezeigt wird
            SwingUtilities.invokeLater(() -> {
                fahrtenManager.updateFahrerUI(fahrer);
            });
        } catch (DateTimeParseException e) {
            zeigeExterneFehlermeldung("Ungültiges Datumsformat in Zeile: " + line);
        } catch (NumberFormatException e) {
            zeigeExterneFehlermeldung("Ungültige Kilometerangabe in der Zeile: " + line);
        } catch (IllegalArgumentException e) {
            zeigeExterneFehlermeldung("Fehler beim Verarbeiten der Zeile: " + line + " - " + e.getMessage());
        }
    }

    // Beendet den laufenden Thread
    public void shutdown() {
        running = false;
        interrupt();
    }
}
