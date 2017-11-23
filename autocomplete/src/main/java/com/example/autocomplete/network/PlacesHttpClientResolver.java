package com.example.autocomplete.network;


import com.example.autocomplete.json.*;

public final class PlacesHttpClientResolver {
    public static final PlacesHttpClient PLACES_HTTP_CLIENT;

    static {
        boolean hasOkHttp;

        try {
            Class.forName("com.squareup.okhttp.OkHttpClient");
            hasOkHttp = true;
        } catch (ClassNotFoundException e) {
            hasOkHttp = false;
        }

        PlacesApiJsonParser parser = JsonParserResolver.JSON_PARSER;

        PLACES_HTTP_CLIENT = hasOkHttp ? new OkHttpPlacesHttpClient(parser) : new HttpUrlConnectionMapsHttpClient(parser);
    }

    private PlacesHttpClientResolver() {
        throw new RuntimeException("No Instances!");
    }
}
