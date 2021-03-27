package com.mohamadcm.http_client;

import com.mohamadcm.http_client.parser.HeaderParser;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        HeaderParser hp = new HeaderParser();
        try {
            System.out.println(hp.parse("KEY1:VALUE1,KEY2:VALUE2").toString());
            System.out.println(hp.parse("KEY3:VALUE3").toString());
            System.out.println(hp.parse("KEY2:VALUE4").toString());
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
