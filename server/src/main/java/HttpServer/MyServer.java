package HttpServer;

import DB.Database;
import Basics.Category;
import Basics.Product;
import Basics.User;
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
    private static final byte[] API_KEY_SECRET_BYTES = "super-mega-secret-key-4347t5jsd3q75wd,9423894d2q7d84ynq8734dnqy9gf7283f5gf235fny195".getBytes(StandardCharsets.UTF_8);
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final Key SIGNING_KEY = new SecretKeySpec(API_KEY_SECRET_BYTES, SIGNATURE_ALGORITHM.getJcaName());

    private static final Category[] categories = {
            new Category("category1", "description"),
            new Category("category2", "description"),
            new Category("category3", "description")
    };

    public static final Product[] products = new Product[]{
            new Product("product1", "descr", "prod", 25.0, 35.0, 1),
            new Product("product2", "descr", "prod", 245.0, 345.0, 1),
            new Product("product3", "descr", "prod", 265.0, 335.0, 2),
            new Product("product4", "descr", "prod", 125.0, 35.0, 2),
            new Product("product5", "descr", "prod", 625.0, 335.0, 3),
            new Product("product6", "descr", "prod", 225.0, 375.0, 3),

    };

    private static final User[] users = {
            new User("login", "password"),
            new User("", "")
    };


    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(6060), 0);
        Database db = new Database();
        db.initDatabase("warehouse");

        for (User user : users)
            db.insertUser(user);

        for (Category category: categories)
            db.insertCategory(category);

        for(Product product: products)
            db.insertProduct(product);

        server.start();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);

        server.createContext("/login", exchange -> {
            if (exchange.getRequestMethod().equals("POST")) {
                User user = objectMapper.readValue(exchange.getRequestBody(), User.class);
                User userFromDb = db.getUserByLogin(user.getLogin());
                if(userFromDb != null)
                {
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

        Authenticator authenticator = new Authenticator() {
            @Override
            public Result authenticate(HttpExchange exchange) {
                String jwt= exchange.getRequestHeaders().getFirst("Authorization");
                if(jwt!=null) {
                    String login = getUserLoginFromJWT(jwt);
                    User user = db.getUserByLogin(login);
                    if(user!=null){
                        return new Success(new HttpPrincipal(login,"admin"));
                    }
                }
                return new Failure(403);
            }
        };

        server.createContext("/api/goods", exchange -> {
                if(exchange.getRequestMethod().equals("GET")){
                    List<Product> products = db.getAllProducts();
                    byte[] response = objectMapper.writeValueAsBytes(products);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length);
                    exchange.getResponseBody().write(response);
                }else exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        );

        server.createContext("/api/goodsbycategory/", exchange -> {
            int categoryId = getURIId(exchange.getRequestURI().getPath());
            if(categoryId < 1){
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            } else if(!(db.isCategoryPresent(categoryId))){
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            } else if(exchange.getRequestMethod().equals("GET")){
                        List<Product> products = db.getProductsByCategoryId(categoryId);
                        byte[] response = objectMapper.writeValueAsBytes(products);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);
                    }else exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
        );

        server.createContext("/api/categories", exchange -> {

            if(exchange.getRequestMethod().equals("GET")){
                        List<Category> categories = db.getAllCategories();
                        byte[] response = objectMapper.writeValueAsBytes(categories);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);
                    }else exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
        );

        server.createContext("/api/good", exchange -> {
            if(exchange.getRequestMethod().equals("PUT")){
                Product product = objectMapper.readValue(exchange.getRequestBody().readAllBytes(), Product.class);

                if(product != null) {
                    if(db.isProductPresentByName(product.getName()) || !product.isValid()){
                        exchange.sendResponseHeaders(409, 0);
                    } else {
                        product = db.insertProduct(product);
                        byte[] response = ByteBuffer.allocate(4).putInt(product.getId()).array();

                        exchange.sendResponseHeaders(201, response.length);
                        exchange.getResponseBody().write(response);
                    }
                }
            } else exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }).setAuthenticator(authenticator);


        // Goods
        server.createContext("/api/good/", exchange ->{
            int productId = getURIId(exchange.getRequestURI().getPath());
            if(productId < 1){
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            } else if(!(db.isProductPresent(productId))){
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;

            } else if(exchange.getRequestMethod().equals("GET")){
                    Product product = db.GetProductById(productId);
                    byte[] response = objectMapper.writeValueAsBytes(product);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length);
                    exchange.getResponseBody().write(response);
            }else if(exchange.getRequestMethod().equals("POST")){
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
                Product product = db.GetProductById(productId);
                db.deleteProduct(product.getId());
                exchange.sendResponseHeaders(204, 0);
            }
            else exchange.sendResponseHeaders(405, 0);
            exchange.close();
            }
        ).setAuthenticator(authenticator);

        // CATEGORY
        server.createContext("/api/category", exchange -> {
            if(exchange.getRequestMethod().equals("PUT")){
                Category category = objectMapper.readValue(exchange.getRequestBody(), Category.class);
                if(category != null) {
                    if(db.isCategoryPresentByName(category.getName()) ||!category.isValid()){
                        exchange.sendResponseHeaders(409, 0);
                    } else {
                        category = db.insertCategory(category);
                        byte[] response = ByteBuffer.allocate(4).putInt(category.getId()).array();;
                        exchange.sendResponseHeaders(201, response.length);
                        exchange.getResponseBody().write(response);
                    }
                }
            } else exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }).setAuthenticator(authenticator);



        server.createContext("/api/category/", exchange ->{
                    int categoryId = getURIId(exchange.getRequestURI().getPath());
                    if(categoryId < 1){
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;
                    } else if(!(db.isCategoryPresent(categoryId))){
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;

                    } else if(exchange.getRequestMethod().equals("GET")){
                        Category category = db.GetCategoryById(categoryId);
                        byte[] response = objectMapper.writeValueAsBytes(category);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);
                    }else if(exchange.getRequestMethod().equals("POST")){
                        String test = new String(exchange.getRequestBody().readAllBytes());
                        test = Strings.replace(test, "\r\n", "");
                        Category category = objectMapper.readValue(test, Category.class);
                        db.updateCategory(category);
                        exchange.sendResponseHeaders(204, 0);
                    } else if(exchange.getRequestMethod().equals("DELETE")){
                        Category category = db.GetCategoryById(categoryId);
                        db.deleteCategory(category.getId());
                        exchange.sendResponseHeaders(204, 0);
                    }
                    else exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
        ).setAuthenticator(authenticator);
    }

    private static int getURIId(String uri){
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

    private static String createJWTFromLogin(String login) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TimeUnit.HOURS.toMillis(24)))
                .setSubject(login)
                .signWith(SIGNING_KEY, SIGNATURE_ALGORITHM)
                .compact();
    }

    private static String getUserLoginFromJWT(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(jwt).getBody();
            return claims.getSubject();
        } catch(RuntimeException e) {
            return null;
        }
    }

}
