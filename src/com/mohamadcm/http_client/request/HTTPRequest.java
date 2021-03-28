package com.mohamadcm.http_client.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;

public class HTTPRequest {
    private String method;
    private String URL;
    private HashMap<String, String> headers;
    private String queryString;
    public HTTPRequest(String method, String url){
        this.method = method != null ? method : "GET"; //TODO: VALIDATE METHOD
        URL = url; //TODO: build url queries, VALIDATE URL
    }
    public Object sendRequest() throws IOException, InterruptedException {
        String xx = "{\"_username\":\"09390703846\",\"_password\":\"12345678\"}";
        HttpClient client = HttpClient.newHttpClient();
        String queryAppendedURL = URL + queryString;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(queryAppendedURL))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(xx))
                .build(); //TODO: remove build so you can add multiple headers
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
        return "";
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
