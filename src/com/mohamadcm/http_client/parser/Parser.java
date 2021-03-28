package com.mohamadcm.http_client.parser;

import java.util.HashMap;

public interface Parser<T> {
    Parser<T> parse(String input) throws Exception;

    HashMap<String, T> getParsedValue();

    String toString();
}
