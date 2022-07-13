import Basics.Category;
import Basics.Product;
import DB.DB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

public class DB_Test {
    private static final DB db = new DB();

    @BeforeAll
    static void initDataBase(){
        db.initDatabase(":memory:");
    }

    //Create categories
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

    //add stuff test yes
    //delete everything test yes
    //update category, update product test yes
    //delete category, delete product test yes
    //something else?? ???


    @BeforeEach
    void fillDataBase(){
        for (Category c: categories) {
            db.addCategory(c);
        }

        for (Product p : products) {
            db.addProduct(p);
        }
    }


    //ehh annotation??
    void clean(){
        db.deleteAll();
    }


    @Test
    void testUpdateCategory(){}

    @Test
    void testUpdateProduct(){}


    @Test
    void testDeleteCategory(){}
    @Test
    void testDeleteProduct(){}

}
