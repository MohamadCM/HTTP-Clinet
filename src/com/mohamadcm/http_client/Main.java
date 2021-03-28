package com.mohamadcm.http_client;

import com.mohamadcm.http_client.parser.HeaderParser;
import com.mohamadcm.http_client.parser.PayloadParser;
import com.mohamadcm.http_client.parser.QueryParser;
import com.mohamadcm.http_client.request.HTTPRequest;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        HeaderParser hp = new HeaderParser();
        try {
            PayloadParser payloadParser = new PayloadParser(HTTPRequest.ApplicationType.JSON);
            payloadParser.parse("{\"_name\":\"1234\",\"_fName\":\"789\"}");
            System.out.println(payloadParser.toString());


            HeaderParser headerParser = new HeaderParser();
            headerParser.parse("Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjA5MzkwNzAzODQ2IiwidXNlclR5cGUiOjIsImlhdCI6MTYxNjkzOTU5OSwiZXhwIjoxNjE3NTQ0Mzk5fQ.3qt1Bi44tffNVMKhjFCSpA0a2pwzrHMxuUJ1VPI2evI");
            QueryParser queryParser = new QueryParser();
            queryParser.parse("limit=1&offset=0");
            HTTPRequest httpRequest = new HTTPRequest("PUT", "http://localhost:5000/api/user/profile", HTTPRequest.ApplicationType.URLENCODED);
            httpRequest.setQueryString(queryParser.toString());
            httpRequest.setPayload(payloadParser.toString());
            httpRequest.setHeaders(headerParser.getParsedValue());
            httpRequest.sendRequest();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
