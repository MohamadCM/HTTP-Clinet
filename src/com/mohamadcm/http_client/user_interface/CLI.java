package com.mohamadcm.http_client.user_interface;

import com.mohamadcm.http_client.parser.HeaderParser;
import com.mohamadcm.http_client.parser.PayloadParser;
import com.mohamadcm.http_client.parser.QueryParser;
import com.mohamadcm.http_client.parser.Validator;
import com.mohamadcm.http_client.request.HTTPRequest;

import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeoutException;

public class CLI implements Runnable {
    private final String[] args;

    public CLI(String[] args) {
        this.args = args;
        run();
    }

    @Override
    public void run() {
        String url = args[0];
        if (!Validator.isUrlValid(url)) {
            System.out.println("\u001B[31m" + "Error: Wrong URL Provided!!" + "\u001B[0m");
            return;
        }
        StringBuilder headerStringBuiler = new StringBuilder("");
        StringBuilder queryStringBuiler = new StringBuilder("");
        String data = "";
        HTTPRequest.ApplicationType applicationType = HTTPRequest.ApplicationType.URL_ENCODED;
        String method = "GET";
        int timeout = Integer.MAX_VALUE;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-H":
                case "--headers":
                    if (!headerStringBuiler.toString().equals(""))
                        headerStringBuiler.append(",");
                    headerStringBuiler.append(args[i + 1]);
                    break;
                case "-Q":
                case "--queries":
                    if (!queryStringBuiler.toString().equals(""))
                        queryStringBuiler.append("&");
                    queryStringBuiler.append(args[i + 1]);
                    break;
                case "-D":
                case "--data":
                    data = args[i + 1];
                    break;
                case "--json":
                    applicationType = HTTPRequest.ApplicationType.JSON;
                    break;
                case "-M":
                case "--method":
                    method = args[i + 1];
                    break;
                case "--timeout":
                    try {
                        timeout = Integer.parseInt(args[i + 1]);
                    } catch (Exception e) {
                        System.out.println("\u001B[31m" + "Error: Wrong timeout time!!" + "\u001B[0m");
                        return;
                    }
                    break;
            }
        }
        if (!Validator.isValidMethod(method)) {
            System.out.println("\u001B[31m" + "Error: Wrong method provided!!" + "\u001B[0m");
            return;
        }
        if (timeout == 0) {
            System.out.println("\u001B[31m" + "Error: Wrong timeout time!!" + "\u001B[0m");
            return;
        }
        initRequest(url, method, headerStringBuiler.toString(), queryStringBuiler.toString(), data, applicationType, timeout);
    }

    private void initRequest(String url, String method, String header, String query, String data, HTTPRequest.ApplicationType applicationType, int timeout) {
        try {
            // Check and Set payload data
            PayloadParser payloadParser = new PayloadParser(applicationType);
            payloadParser.parse(data);
            if (!method.equals("GET") && ((applicationType.equals(HTTPRequest.ApplicationType.URL_ENCODED) && !Validator.isUrlencodedValid(data))
                    || applicationType.equals(HTTPRequest.ApplicationType.JSON) && !Validator.isJsonValid(data)))
                System.out.println("\u001B[33m" + "Warning: Wrong " + applicationType + " format!!" + "\u001B[0m");
            // Check and Set headers
            HeaderParser headerParser = new HeaderParser();
            headerParser.parse(header);

            // Check and Set Query
            QueryParser queryParser = new QueryParser();
            queryParser.parse(query);

            //Initialize request
            HTTPRequest httpRequest = new HTTPRequest(method, url, applicationType, timeout);
            httpRequest.setQueryString(queryParser.toString());
            httpRequest.setPayload(payloadParser.toString());
            httpRequest.setHeaders(headerParser.getParsedValue());

            HttpResponse result = httpRequest.sendRequest();
            System.out.println(result.version().toString().replaceFirst("_", "/").replaceAll("_", ".") + " " + result.statusCode());
            System.out.println(result.headers().map());
            System.out.println(result.body());
        } catch (HttpConnectTimeoutException te) {
            System.out.println("Error: Timeout time exceeded!");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
