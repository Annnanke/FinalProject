package org.example.Requesters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Objects.Category;
import org.example.Objects.Product;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.Requesters.ConnectionString.AUTH_TOKEN;
import static org.example.Requesters.ConnectionString.LOCAL_URL_CATEGORY;


public class UpdateRequests {
    private static ObjectMapper objectMapper =  new ObjectMapper();

    public static int updateProductRequest(Product product) {
        String body = null;
        try {
            body = objectMapper.writeValueAsString(product);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return updateSample(LOCAL_URL_CATEGORY + "/" + product.getId(), body);
    }

    public static int updateCategoryRequest(Category category) {
        String body = null;
        try {
            body = objectMapper.writeValueAsString(category);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return updateSample(LOCAL_URL_CATEGORY + "/" + category.getId(), body);
    }

    private static int updateSample(String path,String body) {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(path))
                    .header("accept", "application/json")
                    .header("Authorization", AUTH_TOKEN)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.statusCode();
    }

}
