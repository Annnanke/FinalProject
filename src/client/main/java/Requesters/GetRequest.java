package Requesters;

import Objects.Category;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;


public class GetRequests {
    public static ArrayList<Category> getAllCategories() throws URISyntaxException, IOException, InterruptedException {
       //TODO HttpRequest request = HttpRequest.newBuilder();

        HttpResponse<String> response = HttpClient.newHttpClient().send(MyRrequest, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
      //  ArrayList<Category> categories = getAllCategories();

        return null;
    }




}
