package com.mohamadcm.http_client.parser;

import com.google.gson.Gson;
import com.mohamadcm.http_client.request.HTTPRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PayloadParser implements Parser<Object> {
    private final HashMap<String, Object> parsedValue;
    private HTTPRequest.ApplicationType applicationType;
    private String wrongJson = null;

    public PayloadParser(HTTPRequest.ApplicationType applicationType) {
        this.parsedValue = new HashMap<>();
        this.applicationType = applicationType == null ? HTTPRequest.ApplicationType.URL_ENCODED : HTTPRequest.ApplicationType.JSON;
    }

    @Override
    public Parser<Object> parse(String input) {
        if (input == null || input.equals(""))
            return this;
        if (applicationType.equals(HTTPRequest.ApplicationType.URL_ENCODED)) {
            String[] pairs = input.split("&");
            for (String pair : pairs) {
                String[] fields = pair.split("=");
                String name = URLDecoder.decode(fields[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(fields[1], StandardCharsets.UTF_8);
                parsedValue.put(name, value);
            }
        } else {
            try {
                Gson gson = new Gson();
                HashMap<String, Object> map = gson.fromJson(input, HashMap.class);
                map.forEach(parsedValue::put);
            } catch (Exception e) {
                wrongJson = input;
            }

        }
        return this;
    }

    @Override
    public HashMap<String, Object> getParsedValue() {
        return parsedValue;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (applicationType.equals(HTTPRequest.ApplicationType.URL_ENCODED)) {
            parsedValue.forEach((key, value) -> {
                Object newVal = value;
                if (value instanceof String) newVal = ((String) value).replaceAll(" ", "+");
                stringBuilder.append(key).append("=").append(newVal).append("&");
            });
        } else {
            if (wrongJson == null) {
                Gson gson = new Gson();
                stringBuilder.append(gson.toJson(parsedValue, HashMap.class));
            } else
                stringBuilder.append(wrongJson);
        }
        return stringBuilder.toString();
    }

    public HTTPRequest.ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(HTTPRequest.ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public static byte[] convertFileToString(String path) {
        try {
            File file = new File(path);

            InputStream insputStream = new FileInputStream(file);
            long length = file.length();
            byte[] bytes = new byte[(int) length];

            insputStream.read(bytes);
            insputStream.close();

            return bytes;
        } catch (Exception e) {
            System.out.println("\u001B[31m" + "Error in reading file!" + "\u001B[0m");
            e.printStackTrace();
            return new byte[0];
        }
    }
}
