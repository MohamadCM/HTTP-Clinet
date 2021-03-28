package com.mohamadcm.http_client.parser;

import com.mohamadcm.http_client.request.HTTPRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PayloadParser implements Parser<Object> {
    private final HashMap<String, Object> parsedValue;
    private HTTPRequest.ApplicationType applicationType;

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
            return this;
        }
        String[] split = input.split("&");
        for (String keyValueString : split) {
            String[] keyValuePair = keyValueString.split("=");
            parsedValue.put(keyValuePair[0], keyValuePair[1]);
        }
        return this;
    }

    @Override
    public HashMap<String, Object> getParsedValue() {
        return parsedValue;
    }

    @Override
    public String toString() {
        if(applicationType.equals(HTTPRequest.ApplicationType.URLENCODED)) {
            StringBuilder stringBuilder = new StringBuilder();
            parsedValue.forEach((key, value) -> {
                Object newVal = value;
                if (value instanceof String) newVal = ((String) value).replaceAll(" ", "+");
                stringBuilder.append(key).append("=").append(newVal).append("&");
            });
            return stringBuilder.toString();
        }
        return "";
    }

    public HTTPRequest.ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(HTTPRequest.ApplicationType applicationType) {
        this.applicationType = applicationType;
    }
}
