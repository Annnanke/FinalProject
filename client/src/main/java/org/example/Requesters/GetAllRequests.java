package org.example.Requesters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Ent.Category;
import org.example.Ent.Product;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static org.example.Requesters.ConnectionString.AUTH_TOKEN;
import static org.example.Requesters.ConnectionString.LOCAL_URL;

public class GetAllRequests {
    public static ArrayList<Category> getAllCategories() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(LOCAL_URL + "/api/categories"))
                .header("accept", "application/json")
                .header("Authorization", AUTH_TOKEN)
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<Category> res = objectMapper.readValue(body, new TypeReference<ArrayList<Category>>() {});
        return res;
    }

    public static ArrayList<Product> getAllProducts() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(LOCAL_URL + "/api/goods"))
                .header("accept", "application/json")
                .header("Authorization", AUTH_TOKEN)
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<Product> res = objectMapper.readValue(body, new TypeReference<ArrayList<Product>>() {});
        return res;
    }

    public static ArrayList<Product> getAllProductsByCategoryId(int categoryId) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(LOCAL_URL + "/api/goodsbycategory/" + categoryId))
                .header("accept", "application/json")
                .header("Authorization", AUTH_TOKEN)
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<Product> res = objectMapper.readValue(body, new TypeReference<ArrayList<Product>>() {});
        return res;
    }

       /*     try {
        ArrayList<Category> categories = getAllCategories();
        for(Category category : categories){
            ArrayList<Product> products = getAllProductsByCategoryId(category.getId());
            ArrayList<ProductLabel> productLabels = new ArrayList<>();
            for(Product product : products){
                productLabels.add(new ProductLabel(product));
            }
            CategoryPane categoryPane = new CategoryPane(category, productLabels);
            groupsUnitsVBox.getChildren().add(categoryPane);
        }
    } catch (URISyntaxException | IOException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }*/


}
