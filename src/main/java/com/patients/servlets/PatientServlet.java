package com.patients.servlets;

import com.patients.database.Database;
import com.patients.model.Patient;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Patient Servlet to handle path /patient
 */
public class PatientServlet extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(PatientServlet.class);

    private static final String ID = "id";

    /**
     * Get patient by id, /patient?id=1
     * @param req Servlet request
     * @param resp Servlet response
     * @throws IOException Servlet exception
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Must provide ?id=
        if (!req.getParameterMap().containsKey(ID)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ID + " is not provided");
            return;
        }
        String id = req.getParameter(ID);
        // id must be integer
        int patientId;
        try {
            patientId= Integer.parseInt(id);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ID + " is not a valid number");
            return;
        }
        // get patient from db
        try(Connection conn = Database.getConnection()) {
            doGetPatient(resp, patientId, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "failed to get patient: ", e);
            resp.setStatus(500);
            resp.getWriter().println("500 Internal Server Err");
        }
    }

    /**
     * Create a new record in the DB. This is a POST, a Patient json object must be provided in the body.
     * @param req Servlet request
     * @param resp  Servlet response
     * @throws IOException Servlet exception
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        // Load Patient object from request body using GSON
        Patient patient;
        try {
            patient = gson.fromJson(req.getReader(), Patient.class);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("not a valid json");
            return;
        }
        // Patient object cannot be null
        if (patient == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("no request body provided");
            return;
        }
        // column patient cannot be null
        if (patient.getPatient() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(".patient cannot be null");
            return;
        }
        // column provider_npi cannot be null
        if (patient.getProviderNpi() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(".providerNpi cannot be null");
            return;
        }
        try(Connection conn = Database.getConnection()) {
            // do the actual insert
            try (ResultSet keys = patient.insert(conn)) {
                // get the inserted id (Auto Increase)
                if (keys.next()) {
                    // and return the inserted Patient object
                    doGetPatient(resp, keys.getInt(1), conn);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "failed to create patient: ", e);
            resp.setStatus(500);
            resp.getWriter().println("500 Internal Server Err");
        }
    }

    /**
     * PUT /patient, for update. Similar to POST, a JSON body must be provided.
     * @param req Servlet request
     * @param resp Servlet response
     * @throws IOException Servlet exception
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        // load Patient from request body using GSON
        Patient patient;
        try {
            patient = gson.fromJson(req.getReader(), Patient.class);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("not a valid json");
            return;
        }
        // json object cannot be null
        if (patient == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("no request body provided");
            return;
        }
        // column patient cannot be null
        if (patient.getPatient() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(".patient cannot be null");
            return;
        }
        // column provider_npi cannot be null
        if (patient.getProviderNpi() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(".providerNpi cannot be null");
            return;
        }
        // column id cannot be null
        if (patient.getId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(".id is not provided");
            return;
        }
        // get DB connection from the pool
        try(Connection conn = Database.getConnection()) {
            // if patient id does not exist, return 404
            PreparedStatement select = conn.prepareStatement("select 1 from patients where id = ?");
            select.setInt(1, patient.getId());
            ResultSet rs = select.executeQuery();
            if (!rs.isBeforeFirst()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println(".id = " + patient.getId() + " is not found");
                return;
            }
            // update
            String sql = "update patients set patient = ?, dob = ?, encounter_date = ?, provider = ?, encounter_note = ?, chief_complaint = ?, provider_npi = ? where id = ?";
            PreparedStatement update = conn.prepareStatement(sql);
            update.setString(1, patient.getPatient());
            update.setDate(2, patient.getDob() == null ? null : new Date(patient.getDob().getTime()));
            update.setDate(3, patient.getEncounterDate() == null ? null : new Date(patient.getEncounterDate().getTime()));
            update.setString(4, patient.getProvider());
            update.setString(5, patient.getEncounterNote());
            update.setString(6, patient.getChiefComplaint());
            update.setInt(7, patient.getProviderNpi());
            update.setInt(8, patient.getId());
            // execute
            update.executeUpdate();
            // return updated patient object
            doGetPatient(resp, patient.getId(), conn);
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "failed to update patient: ", e);
            resp.setStatus(500);
            resp.getWriter().println("500 Internal Server Err");
        }
    }

    /**
     * DELETE /patient?id=, delete from the DB
     * @param req Servlet request
     * @param resp Servlet response
     * @throws IOException Servlet exception
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // must provide id=
        if (!req.getParameterMap().containsKey(ID)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ID + " is not provided");
            return;
        }
        // id has to be an integer
        String id = req.getParameter(ID);
        int patientId;
        try {
            patientId= Integer.parseInt(id);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ID + " is not a valid number");
            return;
        }
        // Get DB connection
        try(Connection conn = Database.getConnection()) {
            // delete
            PreparedStatement delete = conn.prepareStatement("delete from patients where id = ?");
            delete.setInt(1, patientId);
            delete.execute();

            // always return 204, no content
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "failed to get patient: ", e);
            resp.setStatus(500);
            resp.getWriter().println("500 Internal Server Err");
        }
    }

    /**
     * Helper function to load the patient by id and return it through servlet response
     * @param resp Servlet response
     * @param patientId Patient id
     * @param conn DB connection
     * @throws SQLException Servlet exception
     * @throws IOException DB exception
     */
    private void doGetPatient(HttpServletResponse resp, int patientId, Connection conn) throws SQLException, IOException {
        String sql = "select id, patient, dob, encounter_date, provider, encounter_note, chief_complaint, provider_npi from patients where id = ?";
        PreparedStatement select = conn.prepareStatement(sql);
        select.setInt(1, patientId);
        try (ResultSet rs = select.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                // no record found return null
                resp.getWriter().println(new Gson().toJson(null));
                return;
            }
            rs.next();
            resp.getWriter().println(new Gson().toJson(Patient.rsToPatient(rs)));
        }
    }
}
