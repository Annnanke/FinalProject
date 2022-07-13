package Requesters;


import Objects.Category;
import Objects.Product;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;

import static Requesters.ConnectionString.*;

public class SignInRequests {

    public static int[] putProductRequest(Product product) {
        String body = null;
        product.setId(1);

        return putSample(LOCAL_URL_PRODUCT, body);
    }

    public static int[] putCategoryRequest(    Category category) {
        String body = null;

        return putSample(LOCAL_URL_CATEGORY, body);
    }

    private static int[] putSample(String path, String body){
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(path))
                    .header("accept", "application/json")
                    .header("Authorization", AUTH_TOKEN)
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpResponse<String> response = null;
        int id = 0;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 201) {
                byte[] array = response.body().getBytes();
                ByteBuffer wrapped = ByteBuffer.wrap(array); // big-endian by default
                id = wrapped.getInt();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new int[]{response.statusCode(), id};
    }


    private static String GenerateLoginJSON(String login, String password){
        return "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
    }
}