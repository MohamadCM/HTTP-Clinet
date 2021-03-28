package com.mohamadcm.http_client.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

public class HTTPRequest {
    private String method;
    private String URL;
    private String applicationType;
    private HashMap<String, String> headers;
    private String queryString;
    private String payload;
    private int timeout;

    public HTTPRequest(String method, String url, ApplicationType applicationType, int timeout) {
        this.method = method != null ? method : "GET"; //TODO: VALIDATE METHOD
        URL = url; //TODO: build url queries, VALIDATE URL
        this.applicationType = "application/" + (applicationType == ApplicationType.JSON ? "JSON" : "x-www-form-urlencoded");
        this.timeout = timeout;
        headers = new HashMap<>();
    }

    public HttpResponse sendRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String queryAppendedURL = URL + queryString;
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(queryAppendedURL))
                .timeout(Duration.ofSeconds(timeout))
                .header("Content-Type", applicationType);
        requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(payload));
        headers.forEach(requestBuilder::header);
        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // Getters and setters
    public HTTPRequest setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public HTTPRequest setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getQueryString() {
        return queryString;
    }

    public HTTPRequest setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    //Application Type enum
    public enum ApplicationType {
        JSON, URL_ENCODED
    }
}
