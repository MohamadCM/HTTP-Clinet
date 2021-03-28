package com.mohamadcm.http_client.parser;

import java.util.HashMap;

public class HeaderParser implements Parser<String> {
    private final HashMap<String, String> parsedValue;

    public HeaderParser() {
        this.parsedValue = new HashMap<>();
    }

    @Override
    public Parser<String> parse(String input) throws Exception {
        if (input == null || input.equals(""))
            throw new Exception("No input provided as a header!");
        String[] split = input.split(",");
        for (String keyValueString : split) {
            String[] keyValuePair = keyValueString.split(":");
            parsedValue.put(keyValuePair[0], keyValuePair[1]);
        }
        return this;
    }

    @Override
    public HashMap<String, String> getParsedValue() {
        return parsedValue;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        parsedValue.forEach((key, value) -> stringBuilder.append(key).append(":").append(value).append("\n"));
        return stringBuilder.toString();
    }
}
