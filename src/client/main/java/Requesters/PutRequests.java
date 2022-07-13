package Requesters;

import Objects.Category;
import Objects.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;




public class putRequests {
    private static ObjectMapper objectMapper =  new ObjectMapper();

    public static int[] putProductRequest(Product product) {
        String body = null;
        product.setId(1);
        try {
            body = objectMapper.writeValueAsString(product);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return putSample(LOCAL_URL_PRODUCT, body);
    }

    public static int[] putCategoryRequest(    Category category) {
        String body = null;
        try {
            body = objectMapper.writeValueAsString(category);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
}
