package com.patients;

import com.patients.model.Patient;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
public class CSVParserTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testParseDate() {
        assertNull(CSVParser.parseDate("Apirl 5, 1970"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date(CSVParser.parseDate("January 9, 1940").getTime()));
        assertEquals(1940, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(9, calendar.get(Calendar.DATE));
        calendar.setTime(new java.util.Date(CSVParser.parseDate("5 June 1950").getTime()));
        assertEquals(1950, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        assertEquals(5, calendar.get(Calendar.DATE));
        calendar.setTime(new java.util.Date(CSVParser.parseDate("1955/08/03").getTime()));
        assertEquals(1955, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(3, calendar.get(Calendar.DATE));
        calendar.setTime(new java.util.Date(CSVParser.parseDate("10/5/1948").getTime()));
        assertEquals(1948, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.OCTOBER, calendar.get(Calendar.MONTH));
        assertEquals(5, calendar.get(Calendar.DATE));
    }

    @Test
    public void testParseLine() {
        Patient patient = CSVParser.lineToPatient("john smith;January 9, 1940;10/4/2019;Dr. Charles Wilson;Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur nec dapibus turpis, vel tempus velit. In luctus velit at ligula eleifend euismod. Duis tincidunt, massa quis egestas euismod, ex purus faucibus nunc, eu ultrices eros quam porta diam. Cras non lectus nibh. Maecenas porta sem lacus, eu sagittis tortor eleifend sit amet. Pellentesque eget porta dolor, ut convallis sem. Aenean semper, justo eu maximus imperdiet, libero nibh mattis turpis, et suscipit massa dui molestie lacus. Nam non ornare nisl, in semper magna. Fusce ultrices vitae erat id ultricies. Aliquam vulputate magna vel sollicitudin auctor. Proin vel gravida odio.;diabetes;5");
        assertNotNull(patient);
        assertEquals(patient.getPatient(), "john smith");
        Calendar dob = Calendar.getInstance();
        dob.setTime(patient.getDob());
        assertEquals(1940, dob.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, dob.get(Calendar.MONTH));
        assertEquals(9, dob.get(Calendar.DATE));
        Calendar encounterDate = Calendar.getInstance();
        encounterDate.setTime(patient.getEncounterDate());
        assertEquals(2019, encounterDate.get(Calendar.YEAR));
        assertEquals(Calendar.OCTOBER, encounterDate.get(Calendar.MONTH));
        assertEquals(4, encounterDate.get(Calendar.DATE));
        assertTrue(patient.getProvider().contains("Charles"));
        assertTrue(patient.getEncounterNote().contains("ipsum"));
        assertEquals("diabetes", patient.getChiefComplaint());
        assertEquals(patient.getProviderNpi(), 5);
    }

    @Test
    public void testParseEmpty() {
        Patient patient = CSVParser.lineToPatient("");
        assertNull(patient);
    }

    @Test
    public void testParseNull() {
        assertNull(CSVParser.lineToPatient(null));
    }
}
