package Server;

import Baiscs.Category;
import Baiscs.Product;
import Baiscs.User;
import DB.DB;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Strings;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyServer {


    private static final byte[] SECRET_KEY_BYTES = "the-secret-secret-key-4347t5jsd3q75wd,9423894d2q7d84ynq8734dnqy9gf7283f5gf235fny195".getBytes(StandardCharsets.UTF_8);
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private static final Key SIGN_KEY = new SecretKeySpec(SECRET_KEY_BYTES, SIGNATURE_ALGORITHM.getJcaName());

    //Create products
    private static final Category[] categories = {
            new Category("category1", "description1"),
            new Category("category2", "description2"),
            new Category("category3", "description3")
    };

    //Create products
    public static final Product[] products = new Product[]{
            new Product("product1", "description1", "distributor1", 15.0, 89.0, 1),
            new Product("product2", "description2", "distributor2", 293.0, 411.1, 1),
            new Product("product3", "description3", "distributor3", 192.0, 192.7, 3),
            new Product("product4", "description4", "distributor4", 78.0, 21.4, 2),
            new Product("product5", "description5", "distributor5", 115.0, 382.0, 2),

    };




   //Create users
    private static final User[] users = {
            new User("login", "password"),
            new User("", "")
    };


    /**get URI ID*/
    private static int getURIID(String uri){
        String[] splitted = uri.split("/");
        int result = 0;
        String possId = splitted[splitted.length-1];
        if (!possId.chars().allMatch(x -> Character.isDigit(x))) {
            return result;
        }
        try {
            result = Integer.parseInt(possId);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**create JWT from login*/
    private static String createJWTFromLogin(String login) {
        long MillisATM = System.currentTimeMillis();
        Date now = new Date(MillisATM);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TimeUnit.HOURS.toMillis(24)))
                .setSubject(login)
                .signWith(SIGN_KEY, SIGNATURE_ALGORITHM)
                .compact();
    }

    /**Acquire User Login*/
    private static String getUserLoginFromJWT(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SIGN_KEY)
                    .build()
                    .parseClaimsJws(jwt).getBody();
            return claims.getSubject();
        } catch(RuntimeException e) {
            return null;
        }
    }



    //MAIN


    public static void main(String[] args) throws IOException {
    }


}
