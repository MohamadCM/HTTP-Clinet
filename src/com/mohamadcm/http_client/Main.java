package com.mohamadcm.http_client;

import com.mohamadcm.http_client.parser.HeaderParser;
import com.mohamadcm.http_client.parser.QueryParser;
import com.mohamadcm.http_client.request.HTTPRequest;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        HeaderParser hp = new HeaderParser();
        try {
            QueryParser queryParser = new QueryParser();
            queryParser.parse("limit=1&offset=0");
            HTTPRequest httpRequest = new HTTPRequest("GET", "http://localhost:5000/api/user/auth/admin");
            httpRequest.setQueryString(queryParser.toString());
            httpRequest.sendRequest();
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
