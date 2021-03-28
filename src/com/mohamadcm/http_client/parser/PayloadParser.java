package com.mohamadcm.http_client.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mohamadcm.http_client.request.HTTPRequest;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PayloadParser implements Parser<Object> {
    private final HashMap<String, Object> parsedValue;
    private HTTPRequest.ApplicationType applicationType;
    private String wrongJson = null;

    public PayloadParser(HTTPRequest.ApplicationType applicationType) {
        this.parsedValue = new HashMap<>();
        this.applicationType = applicationType == null ? HTTPRequest.ApplicationType.URLENCODED : HTTPRequest.ApplicationType.JSON;
    }

    @Override
    public Parser<Object> parse(String input) throws Exception {
        if (input == null || input.equals(""))
            throw new Exception("No input provided as a header!");
        if(applicationType.equals(HTTPRequest.ApplicationType.URLENCODED)){
            String[] pairs = input.split("&");
            for (String pair : pairs) {
                String[] fields = pair.split("=");
                String name = URLDecoder.decode(fields[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(fields[1], StandardCharsets.UTF_8);
                parsedValue.put(name, value);
            }
        }else {
            try {
                Gson gson = new Gson();
                HashMap<String, Object> map = gson.fromJson(input, HashMap.class);
                map.forEach(parsedValue::put);
            } catch (Exception e){
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
        if(applicationType.equals(HTTPRequest.ApplicationType.URLENCODED)) {
            parsedValue.forEach((key, value) -> {
                Object newVal = value;
                if (value instanceof String) newVal = ((String) value).replaceAll(" ", "+");
                stringBuilder.append(key).append("=").append(newVal).append("&");
            });
        } else {
            if(wrongJson == null) {
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
}
