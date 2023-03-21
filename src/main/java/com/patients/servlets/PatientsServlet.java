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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientsServlet for endpoint /patients
 */
public class PatientsServlet extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(PatientsServlet.class);

    // query parameter from=
    private static final String FROM = "from";
    // query parameter limits=
    private static final String LIMITS = "limits";

    /**
     * GET /patients?from=0&limit=10, return the list of patient-records in the database
     * @param req Servlet request
     * @param resp Servlet response
     * @throws IOException Servlet exception
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // default values
        int from = 0;
        int limits = 10;
        if (req.getParameterMap().containsKey(FROM)) {
            try {
                // must be integer
                from = Integer.parseInt(req.getParameter(FROM));
            } catch (RuntimeException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("from is not a valid number");
                return;
            }
            if (from < 0) {
                // must be bigger than 0
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("from cannot be less than 0");
                return;
            }
        }
        if (req.getParameterMap().containsKey(LIMITS)) {
            try {
                // must be integer
                limits = Integer.parseInt(req.getParameter(LIMITS));
            } catch (RuntimeException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("limits is not a valid number");
                return;
            }
            if (limits > 10) {
                // must be smaller than 10
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("limits cannot be larger than 10");
                return;
            }
            if (limits < 0) {
                // cannot be negative to void 500
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("from cannot be less than 0");
                return;
            }
        }
        try(Connection conn = Database.getConnection()) {
            // select and return
            PreparedStatement select = conn.prepareStatement("select id, patient, dob, encounter_date, provider, encounter_note, chief_complaint, provider_npi from patients order by id limit ?, ?");
            select.setInt(1, from);
            select.setInt(2, limits);
            ResultSet rs = select.executeQuery();
            List<Patient> patients = new ArrayList<>();
            while (rs.next()) {
                patients.add(Patient.rsToPatient(rs));
            }
            resp.getWriter().println(new Gson().toJson(patients));
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "failed to get patient: ", e);
            resp.setStatus(500);
            resp.getWriter().println("500 Internal Server Err");
        }
    }
}
