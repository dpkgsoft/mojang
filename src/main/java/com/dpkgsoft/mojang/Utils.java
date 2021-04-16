package com.dpkgsoft.mojang;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static String postJson(String url, String json) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity requestEntity = new StringEntity(
                json,
                ContentType.APPLICATION_JSON
        );
        request.setEntity(requestEntity);
        HttpResponse rawResponse = client.execute(request);

        if (rawResponse.getStatusLine().getStatusCode() == 204) return "";
        return IOUtils.toString(rawResponse.getEntity().getContent(), StandardCharsets.UTF_8);
    }

    public static String get(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse rawResponse = client.execute(request);

        if (rawResponse.getStatusLine().getStatusCode() == 204) return "";
        return IOUtils.toString(rawResponse.getEntity().getContent(), StandardCharsets.UTF_8);
    }
}
