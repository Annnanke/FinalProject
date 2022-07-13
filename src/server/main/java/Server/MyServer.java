package Server;

import Basics.Category;
import Basics.Product;
import Basics.User;
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
        String[] splitt = uri.split("/");
        int result = 0;
        String possId = splitt[splitt.length-1];
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


    /**a main method. create user, categories and products. test stuff*/
    public static void main(String[] args)  throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(6060), 0);
        DB db = new DB();
        db.initDatabase("warehouse");

        for (User user : users)
            db.addUser(user);

        for (Category category: categories)
            db.addCategory(category);

        for(Product product: products)
            db.addProduct(product);

        server.start();
        ObjectMapper objectMapper = new ObjectMapper();

        server.createContext("/login", exchange -> {

            if (exchange.getRequestMethod().equals("POST")) {
                User user = objectMapper.readValue(exchange.getRequestBody(), User.class);
                User userFromDb = db.getUserByLogin(user.getLogin());

                if(userFromDb != null) {
                    if(userFromDb.getPassword().equals(user.getPassword()))
                    {
                        String jwt = createJWTFromLogin(userFromDb.getLogin());
                        System.out.println(getUserLoginFromJWT(jwt));
                        exchange.getResponseHeaders().set("Authorization", jwt);
                        exchange.sendResponseHeaders(200, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(401, 0);
                    }
                }
                else {
                    exchange.sendResponseHeaders(401, 0);
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });

        //authentication
        Authenticator authenticator = new Authenticator() {
            @Override
            public Result authenticate(HttpExchange exchange) {
                String jwt= exchange.getRequestHeaders().getFirst("Authorization");
                if(jwt != null) {
                    String login = getUserLoginFromJWT(jwt);
                    User user = db.getUserByLogin(login);

                    if(user != null){
                        return new Success(new HttpPrincipal(login,"admin"));
                    }
                }
                return new Failure(403);
            }
        };


        //ALL GOOD RELATED
        server.createContext("/api/goods", exchange -> {

                    if(exchange.getRequestMethod().equals("GET")) {
                        List<Product> products = db.getAllProducts();
                        byte[] response = objectMapper.writeValueAsBytes(products);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);

                    } else{
                        exchange.sendResponseHeaders(405, 0);
                    }
                    exchange.close();
                }
        );

        server.createContext("/api/goodsbycategory/", exchange -> {
                    int categoryId = getURIID(exchange.getRequestURI().getPath());

                    if(categoryId < 1) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if(!(db.isCategoryPresent(categoryId))) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if (exchange.getRequestMethod().equals("GET")) {
                        List<Product> products = db.getProductsByCategoryId(categoryId);
                        byte[] response = objectMapper.writeValueAsBytes(products);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);

                        exchange.getResponseBody().write(response);
                    } else{
                        exchange.sendResponseHeaders(405, 0);
                    }
                    exchange.close();
                }
        );



        server.createContext("/api/good", exchange -> {

            if(exchange.getRequestMethod().equals("PUT")) {
                Product product = objectMapper.readValue(exchange.getRequestBody().readAllBytes(), Product.class);

                if(product != null) {
                    if(db.isProductPresent_ByName(product.getName()) || !product.isValid()){
                        exchange.sendResponseHeaders(409, 0);
                    } else {
                        product = db.addProduct(product);
                        byte[] response = ByteBuffer.allocate(4).putInt(product.getId()).array();

                        exchange.sendResponseHeaders(201, response.length);
                        exchange.getResponseBody().write(response);
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();

        }).setAuthenticator(authenticator);

        server.createContext("/api/good/", exchange -> {

                    int productId = getURIID(exchange.getRequestURI().getPath());

                    if (productId < 1) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if(!(db.isProductPresent(productId))) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if(exchange.getRequestMethod().equals("GET")){

                        Product product = db.getProductByID(productId);
                        byte[] response = objectMapper.writeValueAsBytes(product);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);

                    } else if(exchange.getRequestMethod().equals("POST")) {

                        String test = new String(exchange.getRequestBody().readAllBytes());
                        test = Strings.replace(test, "\r\n", "");
                        Product pr = objectMapper.readValue(exchange.getRequestBody(), Product.class);
                        if(!db.isCategoryPresent(pr.getCategory_id()) || !pr.isValid()){
                            exchange.sendResponseHeaders(409, 0);
                        } else {
                            db.updateProduct(pr);
                            exchange.sendResponseHeaders(204, 0);
                        }
                    } else if(exchange.getRequestMethod().equals("DELETE")){
                        Product product = db.getProductByID(productId);
                        db.deleteProduct(product.getId());
                        exchange.sendResponseHeaders(204, 0);
                    }
                    else exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
        ).setAuthenticator(authenticator);


        //CATEGORIES
        server.createContext("/api/categories", exchange -> {

                    if (exchange.getRequestMethod().equals("GET")) {
                        List<Category> categories = db.getAllCategories();
                        byte[] response = objectMapper.writeValueAsBytes(categories);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);

                        exchange.getResponseBody().write(response);

                    } else exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
        );


        server.createContext("/api/category", exchange -> {
            if(exchange.getRequestMethod().equals("PUT")){
                Category category = objectMapper.readValue(exchange.getRequestBody(), Category.class);
                if(category != null) {
                    if(db.isCategoryPresent_ByName(category.getName()) ||!category.isValid()){
                        exchange.sendResponseHeaders(409, 0);
                    } else {
                        category = db.addCategory(category);
                        byte[] response = ByteBuffer.allocate(4).putInt(category.getId()).array();;
                        exchange.sendResponseHeaders(201, response.length);
                        exchange.getResponseBody().write(response);
                    }
                }
            } else exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }).setAuthenticator(authenticator);



        server.createContext("/api/category/", exchange ->{
                    int categoryId = getURIID(exchange.getRequestURI().getPath());

                    if(categoryId < 1) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if(!(db.isCategoryPresent(categoryId))) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if(exchange.getRequestMethod().equals("GET")) {

                        Category category = db.getCategoryByID(categoryId);
                        byte[] response = objectMapper.writeValueAsBytes(category);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);

                    } else if(exchange.getRequestMethod().equals("POST")) {

                        String test = new String(exchange.getRequestBody().readAllBytes());
                        test = Strings.replace(test, "\r\n", "");
                        Category category = objectMapper.readValue(test, Category.class);

                        db.updateCategory(category);
                        exchange.sendResponseHeaders(204, 0);

                    } else if(exchange.getRequestMethod().equals("DELETE")){
                        Category category = db.getCategoryByID(categoryId);
                        db.deleteCategory(category.getId());

                        exchange.sendResponseHeaders(204, 0);
                    }
                    else{
                        exchange.sendResponseHeaders(405, 0);
                    }
                    exchange.close();
                }
        ).setAuthenticator(authenticator);



    }


}
