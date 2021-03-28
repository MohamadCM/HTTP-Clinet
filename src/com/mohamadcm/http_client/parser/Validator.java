package com.mohamadcm.http_client.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mohamadcm.http_client.request.HTTPRequest;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Validator {
    public static boolean isUrlValid(String input) {
        boolean res;
        try {
            new URL(input).toURI();
            res = true;
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    public static boolean isJsonValid(String input) {
        boolean res;
        if (input == null || input.equals(""))
            res = false;
        else {
            try {
                Gson gson = new Gson();
                HashMap<String, Object> map = gson.fromJson(input, HashMap.class);
                res = true;
            } catch (Exception e) {
                res = false;
            }
        }
        return res;
    }

    public static boolean isUrlencodedValid(String input) {
        boolean res;
        if (input == null || input.equals(""))
            res = false;
        else {
            try {
                PayloadParser payloadParser = new PayloadParser(HTTPRequest.ApplicationType.URLENCODED);
                String url = URLDecoder.decode(input, StandardCharsets.UTF_8);
                String encodeStr = URLEncoder.encode(url, StandardCharsets.UTF_8);
                System.out.println(encodeStr);
                res = input.equals(encodeStr.replaceAll("%3D", "=").replaceAll("%26", "&"));
            } catch (Exception e) {
                res = false;
            }
        }
        return res;
    }
}
