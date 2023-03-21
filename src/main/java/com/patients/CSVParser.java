package com.patients;

import com.patients.database.Database;
import com.patients.model.Patient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CSV parser for parsing the patients data
 */
public class CSVParser {

    // LOGGER
    private final static Logger LOGGER = LogManager.getLogger(CSVParser.class);

    /**
     * Keep the known data formats as a list as a CSV file may contain multiple date formats.
     * https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
     * @return Date format in string
     */
    private static List<String> knownDateFormats() {
        List<String> formats = new ArrayList<>();
        formats.add("MMMM d, yyyy"); // January 9, 1940
        formats.add("d MMMM yyyy"); // 5 June 1950
        formats.add("MM/d/yyyy"); // 10/5/1948
        formats.add("yyyy/MM/dd"); // 1955/08/03
        return formats;
    }

    /**
     * This function will accept the date as string and try to convert it into java.sql.Date
     * Please note that the original CSV files contains typo for month like "Apirl" for "April",
     * for the time being, this function will just return null for the invalid date strings.
     * In real production, we may need to have more complex logic to handle typos.
     * @param dateStr the date in string format
     * @return java.sql.Date; if conversion fails, it returns null
     */
    public static Date parseDate(String dateStr) {
        // null returns null
        if (dateStr == null) {
            return null;
        }
        // blank or empty string, null
        if (Strings.isBlank(dateStr) || Strings.isEmpty(dateStr)) {
            return null;
        }
        // for known date formats
        for (String format:knownDateFormats()) {
            try {
                DateFormat f = new SimpleDateFormat(format);
                // https://stackoverflow.com/questions/45143802/why-simpledateformat-does-not-throw-exception-for-invalid-format
                // has to set lenient to false to handle 10/5/1948 vs 1955/08/03
                f.setLenient(false);
                // convert parsed java.util.Date to java.sql.Date
                return new Date(f.parse(dateStr).getTime());
            } catch (ParseException e) {
                LOGGER.log(Level.INFO, "date format not " + format +", try next...");
            }
        }
        LOGGER.log(Level.WARN, "unknown datetime format: " + dateStr);
        // for string that known formats cannot parse, return null
        return null;
    }

    /**
     * convert a CSV line to a Patient object
     * @param line a colon (;) separated line
     * @return A Patient object
     */
    public static Patient lineToPatient(String line) {
        // null return null
        if (line == null) {
            return null;
        }

        String[] columns = line.split(";");

        // has to be 7 columns in this project
        if (columns.length != 7) {
            LOGGER.log(Level.WARN, "line length is not 7, skip...");
            return null;
        }

        // populate patient fields
        Patient patient = new Patient();
        patient.setPatient(columns[0]);
        patient.setDob(parseDate(columns[1]));
        patient.setEncounterDate(parseDate(columns[2]));
        // nullable string
        String provider = columns[3];
        patient.setProvider(Strings.isBlank(provider) || Strings.isEmpty(provider) ? null : provider);
        // nullable string
        String encounterNote = columns[4];
        patient.setEncounterNote(Strings.isBlank(encounterNote) || Strings.isEmpty(encounterNote) ? null : encounterNote);
        // nullable string
        String chiefComplaint = columns[5];
        patient.setChiefComplaint(Strings.isBlank(chiefComplaint) || Strings.isEmpty(chiefComplaint) ? null : chiefComplaint);
        patient.setProviderNpi(Integer.parseInt(columns[6]));
        return patient;
    }

    /**
     * To parse the CSV file and insert the result into database
     * @param args the CSV file location
     */
    public static void main(String[] args) {
        // has to provide the csv filename as the first argument
        if (args.length != 1) {
            LOGGER.log(Level.ERROR, "csvFileName is not provided");
            System.exit(1);
        }
        // non-empty check
        String csvFileName = args[0];
        if (Strings.isEmpty(csvFileName) || Strings.isBlank(csvFileName)) {
            LOGGER.log(Level.ERROR, "csvFileName is not provided");
            System.exit(1);
        }

        // validate DB options are set
        Database.validateSystemProperties();

        // get the DB connection from the connection pool
        try (Connection conn = Database.getConnection()) {
            LOGGER.log(Level.DEBUG, "DB connection completes");
            // read the CSV file line by line; Scanner for reading text
            try (Scanner scanner = new Scanner(new File(csvFileName))) {
                // skip first line
                scanner.nextLine();
                // loop until the end
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // convert line into patient
                    Patient patient = lineToPatient(line);

                    // skip for failed conversions
                    if (patient == null) {
                        continue; // skip insert if cannot parse
                    }

                    // insert into DB
                    patient.insert(conn);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
}
