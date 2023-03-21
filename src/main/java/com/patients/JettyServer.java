package com.patients;

import com.patients.database.Database;
import com.patients.servlets.PatientServlet;
import com.patients.servlets.PatientsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * JettyServer, the CRUD application
 */
public class JettyServer {

    /**
     * The Jetty Server object. Declare as a member so that it can be used for start and shutdown.
     */
    private final Server server;

    /**
     * Initialize a JettyServer object with mapping servlets.
     * @param port Server port such as 8080
     */
    public JettyServer(int port) {

        // new server object
        this.server = new Server(port);

        // add new handler
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        // handle paths
        handler.addServletWithMapping(PatientServlet.class, "/patient");
        handler.addServletWithMapping(PatientsServlet.class, "/patients");
    }

    /**
     * Start the server object
     * @throws Exception any exceptions during start
     */
    public void start() throws Exception {
        server.start();
    }

    /**
     * Shutdown the server object
     * @throws Exception any exceptions during shutdown
     */
    public void shutDown() throws Exception {
        this.server.stop();
    }

    /**
     * Needs to set system properties before start
     * If any of the 3 system properties not set, -DdbUrl= -DdbUsername= -DdbPassword=, main function will exit
     * @param args Currently it accepts no arguments
     * @throws Exception any exceptions during CRUD runtime.
     */
    public static void main(String[] args) throws Exception {
        // validate JVM options
        Database.validateSystemProperties();

        // start the Jetty servers
        new JettyServer(Integer.parseInt(System.getProperty("serverPort", "8082"))).start();
    }
}
