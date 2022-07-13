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
       body = objectMapper.writeValueAsString(product);

    }

    public static int[] putCategoryRequest(Category category) {
        String body = null;

    }

    private static int[] putSample(String path, String body){

        }

}
