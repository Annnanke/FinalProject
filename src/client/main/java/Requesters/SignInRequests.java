package Requesters;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static Requesters.ConnectionString.LOCAL_URL;
import static Requesters.ConnectionString.AUTH_TOKEN;

public class SignInRequests {

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

    private static String GenerateLoginJSON(String login, String password){
        return "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
    }
}
