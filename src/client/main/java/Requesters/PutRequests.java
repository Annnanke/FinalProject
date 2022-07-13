package Requesters;

import Objects.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;

public class putRequests {
    private static ObjectMapper objectMapper =  new ObjectMapper();

    public static int[] putProductRequest(Product product) {
        String body = null;
        product.setId(1);
      /*  try {
            body = objectMapper.writeValueAsString(product);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    */
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
    public static int doSignInRequest(String login, String password) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(LOCAL_URL + "/login"))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GenerateLoginJSON(login, password)))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().
                send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200) {
            AUTH_TOKEN = response.headers().firstValue("Authorization").get();
        }

        return response.statusCode();
    }

    public static int deleteProduct(Product product){
        return deleteSample(LOCAL_URL_PRODUCT + "/" + product.getId());
    }

    private static int deleteSample(String path){
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(path))
                    .header("accept", "application/json")
                    .header("Authorization", AUTH_TOKEN)
                    .DELETE()
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    private static String GenerateLoginJSON(String login, String password){
        return "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
    }
}