package com.virtuallymachined.countrydb.service;

import javafx.concurrent.Task;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadFlagTask extends Task<File> {
    private static final String ALPHA_CODE_KEY = "ALPHA2CODE";
    private static final String COUNTRY_FLAGS_URL = "https://www.countryflags.io/ALPHA2CODE/flat/64.png";

    private String alpha2Code;
    private HttpClient httpClient;

    public DownloadFlagTask(HttpClient httpClient, String alpha2Code) {
        this.httpClient = httpClient;
        this.alpha2Code = alpha2Code;
    }

    public DownloadFlagTask(String alpha2Code) {
        this.httpClient = HttpClientBuilder.create().build();
        this.alpha2Code = alpha2Code;
    }

    @Override
    protected File call() throws Exception {

        HttpGet request = new HttpGet(COUNTRY_FLAGS_URL.replace(ALPHA_CODE_KEY, alpha2Code));
        request.addHeader("content-type", "image/png");

        File imageFile = new File("downloads/" + alpha2Code + ".png");

        if (imageFile.exists()) {
            return imageFile;
        }

        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            HttpResponse response = httpClient.execute(request);
            InputStream inputStream = response.getEntity().getContent();
            IOUtils.copy(inputStream, outputStream);

            return imageFile;
        }
    }
}
