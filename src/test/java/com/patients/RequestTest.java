package com.patients;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestTest {

    static JettyServer server;

    @BeforeAll
    public static void startServer() throws Exception {
        server = new JettyServer(8080);
        server.start();
    }

    @AfterAll
    public static void shutdownServer() throws Exception {
        server.shutDown();
    }

    @Test
    public void testGet401() throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/patient"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(r -> {
                    assertEquals(400, r.statusCode());
                    assertTrue(r.body().contains("id"));
                }).join();
    }

    @Test
    public void testGetIdInvalid() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/patient?id=not_a_number"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(r -> {
                    assertEquals(400, r.statusCode());
                    assertTrue(r.body().contains("id"));
                }).join();
    }

    @Test
    public void testPostEmptyBody() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create("http://localhost:8080/patient"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(r -> {
                    assertEquals(400, r.statusCode());
                    //assertTrue(r.body().contains("id"));
                }).join();
    }

    @Test
    public void testDelete() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().DELETE()
                .uri(URI.create("http://localhost:8080/patient"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(r -> {
                    assertEquals(400, r.statusCode());
                    assertTrue(r.body().contains("id"));
                }).join();
    }
}
