package app.utils;

import app.exceptions.ApiImportException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientHelper {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static String get(String url) {

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try {

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiImportException("Request failed with status: " + response.statusCode());
            }

            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiImportException("HTTP request was interrupted: " + url, e);
        } catch (IOException e) {
            throw new ApiImportException("Error during HTTP request to: " + url, e);
        }
    }
}