package com.patients.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Patient object to support:
 * 1. GSON.
 * 2. Simple OR mapping such as inert and select (load result set)
 */
public class Patient {

    private Integer id;
    private String patient;
    private Date dob;
    private Date encounterDate;
    private String provider;
    private String encounterNote;
    private String chiefComplaint;
    private Integer providerNpi;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Date getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(Date encounterDate) {
        this.encounterDate = encounterDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEncounterNote() {
        return encounterNote;
    }

    public void setEncounterNote(String encounterNote) {
        this.encounterNote = encounterNote;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public Integer getProviderNpi() {
        return providerNpi;
    }

    public void setProviderNpi(Integer providerNpi) {
        this.providerNpi = providerNpi;
    }

    /**
     * Insert itself into the DB
     * @param conn java.sql.Connection, the JDBC connection
     * @return GeneratedKeys that includes the inserted id
     * @throws SQLException exception during insert
     */
    public ResultSet insert(Connection conn) throws SQLException {
        PreparedStatement insert = conn.prepareStatement("insert into patients (patient, dob, encounter_date, provider, encounter_note, chief_complaint, provider_npi) values (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        insert.setString(1, this.getPatient());
        insert.setDate(2, this.getDob() == null ? null : new java.sql.Date(this.getDob().getTime()));
        insert.setDate(3, this.getEncounterDate() == null ? null : new java.sql.Date(this.getEncounterDate().getTime()));
        insert.setString(4, this.getProvider());
        insert.setString(5, this.getEncounterNote());
        insert.setString(6, this.getChiefComplaint());
        insert.setInt(7, this.getProviderNpi());
        // number of rows inserted has to be 1
        int rows = insert.executeUpdate();
        if (rows != 1) {
            throw new SQLException("Creating user failed, no rows affected.");
        }
        return insert.getGeneratedKeys();
    }

    /**
     * Turns a ResultSet into a Patient Object
     * @param rs JDBC ResultSet
     * @return A Patient object
     * @throws SQLException any exception during ResultSet get
     */
    public static Patient rsToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setPatient(rs.getString("patient"));
        p.setDob(rs.getDate("dob"));
        p.setEncounterDate(rs.getDate("encounter_date"));
        p.setProvider(rs.getString("provider"));
        p.setEncounterNote(rs.getString("encounter_note"));
        p.setChiefComplaint(rs.getString("chief_complaint"));
        p.setProviderNpi(rs.getInt("provider_npi"));
        return p;
    }
}
