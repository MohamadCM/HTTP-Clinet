package com.mohamadcm.http_client.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
        switch (applicationType) {
            case FILE:
                this.applicationType = "binary/octet-stream";
                break;
            case JSON:
                this.applicationType = "application/JSON";
                break;
            default:
                this.applicationType = "application/x-www-form-urlencoded";
                break;
        }
        this.timeout = timeout;
        this.headers = new HashMap<>();
        this.payload = null;
    }

    public void sendRequest() throws IOException, SocketException {
        String queryAppendedURL = URL + queryString;
        HttpURLConnection connection = (HttpURLConnection) new URL(queryAppendedURL).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout * 1000);
        connection.setRequestProperty("Content-Type", applicationType);
        headers.forEach(connection::setRequestProperty);
        if (payload != null &&
                (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("PATCH"))) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) { // Writing Data
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        int status = connection.getResponseCode();
        String result = "";
        String contentType = connection.getHeaderField("Content-Type").split("/")[1];
        int contentLength = connection.getContentLength();

        if (contentType.equalsIgnoreCase("PDF") || contentType.equalsIgnoreCase("JPG") ||
                contentType.equalsIgnoreCase("PNG") || contentType.equalsIgnoreCase("MP4")) {
            if (status >= 200 && status < 300) {
                String fileName = "";
                String disposition = connection.getHeaderField("Content-Disposition");

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else
                    fileName = (long) Math.ceil(Math.random() * 100_000) + "." + contentType; // giving a random name

                InputStream inputStream = connection.getInputStream(); // open input stream from the HTTP connection
                String saveFilePath = "files" + File.separator + fileName;

                FileOutputStream outputStream = new FileOutputStream(saveFilePath); // open an output stream to save into file

                int bytesRead;
                byte[] buffer = new byte[4_000];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                result = "File successfully downloaded to files/";
            }
        } else {
            Reader streamReader;
            if (status > 299) { // Choosing the right stream reader
                streamReader = new InputStreamReader(connection.getErrorStream());
            } else {
                streamReader = new InputStreamReader(connection.getInputStream());
            }
            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            result = content.toString();
            in.close();
        }
        // Showing
        showResult(connection, result, contentType);
        connection.disconnect();
    }

    private static void showResult(HttpURLConnection connection, String content, String contentType) {
        try {
            StringBuilder fullResponseBuilder = new StringBuilder();
            fullResponseBuilder.append(connection.getResponseCode())
                    .append(" ")
                    .append(connection.getResponseMessage())
                    .append("\n");
            connection.getHeaderFields().entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry -> {
                        fullResponseBuilder.append(entry.getKey()).append(": ");
                        List headerValues = entry.getValue();
                        Iterator it = headerValues.iterator();
                        if (it.hasNext()) {
                            fullResponseBuilder.append(it.next());
                            while (it.hasNext()) {
                                fullResponseBuilder.append(", ").append(it.next());
                            }
                        }
                        fullResponseBuilder.append("\n");
                    });

            if (contentType.contains("json")) {
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(content).getAsJsonObject();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                content = gson.toJson(json);
            }
            fullResponseBuilder.append("\n").append(content);
            System.out.println(fullResponseBuilder.toString());
        } catch (Exception e) {
            System.out.println("Exception happened trying to print result");
            e.printStackTrace();
        }
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
        JSON, URL_ENCODED, FILE
    }
}
