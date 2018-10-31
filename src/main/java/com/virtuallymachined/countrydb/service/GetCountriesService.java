package com.virtuallymachined.countrydb.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.virtuallymachined.countrydb.model.Country;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetCountriesService extends Service<List<Country>> {

    private static final String REST_COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";

    private HttpClient httpClient;
    private Gson gson = new Gson();

    private static final Type COUNTRY_LIST_TYPE = new TypeToken<ArrayList<Country>>() {
    }.getType();

    public GetCountriesService() {
        httpClient = HttpClientBuilder.create().build();
    }

    public GetCountriesService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    protected Task<List<Country>> createTask() {
        return new Task<List<Country>>() {
            @Override
            protected List<Country> call() throws IOException {
                File countriesFile = new File("downloads/countries.json");
                if (countriesFile.exists()) {
                    JsonReader reader = new JsonReader(new FileReader(countriesFile));
                    return gson.fromJson(reader, COUNTRY_LIST_TYPE);
                } else {

                    HttpGet request = new HttpGet(REST_COUNTRIES_URL);
                    request.addHeader("content-type", "application/json");

                    HttpResponse response = httpClient.execute(request);
                    String jsonArray = EntityUtils.toString(response.getEntity(), "UTF-8");

                    List<Country> countryList = gson.fromJson(jsonArray, COUNTRY_LIST_TYPE);

                    try (Writer writer = new FileWriter(countriesFile)) {
                        // create file and directories
                        countriesFile.getParentFile().mkdirs();
                        countriesFile.createNewFile();
                        gson.toJson(countryList, writer);
                    }

                    return countryList;
                }
            }
        };
    }
}
