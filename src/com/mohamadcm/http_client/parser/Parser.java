package com.mohamadcm.http_client.parser;

import java.util.HashMap;

public interface Parser {
    Parser parse(String input) throws Exception;
    HashMap<String, String> getParsedValue();
    String toString();
}
