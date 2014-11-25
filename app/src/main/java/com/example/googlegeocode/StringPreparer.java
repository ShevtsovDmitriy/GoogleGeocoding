package com.example.googlegeocode;

/**
 * Created by Дмитрий on 25.11.2014.
 */
public class StringPreparer {

    public static String makeString(String source){
        source = truncSpaces(source);
        if (Character.isDigit(source.charAt(0))){
            return addressRequest(source);
        }
        else {
            return coordsRequest(source);
        }
    }

    public static String replaceSpaces(String source){
        source = source.replaceAll(" ", "+");
        return source;
    }

    public static String truncAllSpaces(String source){
        source = source.replaceAll(" ", "");
        return source;
    }

    public static String truncSpaces(String source){
        while (source.charAt(0) == ' '){
            source = source.substring(1);
        }
        while (source.charAt(source.length() - 1) == ' '){
            source = source.substring(0, source.length() - 1);
        }
        return source;
    }

    public static String addressRequest(String source){
        String request = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
        source = truncAllSpaces(source);
        request += source;
        request += "&sensor=false";
        return request;
    }

    public static String coordsRequest(String source){
        String request = "http://maps.googleapis.com/maps/api/geocode/json?address=";
        source = replaceSpaces(source);
        request += source;
        request += "&sensor=false";
        return request;
    }

}
