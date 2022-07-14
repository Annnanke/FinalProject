import Basics.Category;
import Basics.Product;
import DB.DB;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @BeforeEach
    void fillDataBase(){
        for (Category c: categories) {
            db.addCategory(c);
        }

        for (Product p : products) {
            db.addProduct(p);
        }
    }


    @AfterEach
    void clean(){
        db.deleteAll();
    }


    @Test
    void testUpdateCategory() {

        List<Category> categories = db.getAllCategories();
        Category category = db.addCategory(new Category("category4", "description4"));
        db.updateCategory(new Category(category.getId(), "category3", "super-description"));

        categories = db.getAllCategories();
        System.out.println(categories.get(3).getDescription());

        assert(categories.get(3).getDescription().equals("super-description"));
    }

    @Test
    void testUpdateProduct() {
        List<Product> products = db.getAllProducts();
        Product product = db.addProduct(new Product("product6", "description6", "distributor", 7.0, 32.0, 1));
        db.updateProduct(new Product(product.getId(), "product6", "description6", "distributor", 100.0, 60.0, 1));

        products = db.getAllProducts();
        assert(products.get(6).getPrice() == 100.0);}


    @Test
    void testDeleteCategory() {
        List<Category> categories = db.getAllCategories();
        db.deleteCategory(3);
       categories = db.getAllCategories();

        assertThat(categories).extracting(Category::getName).doesNotContain("category3");

        List<Product> products = db.getAllProducts();
        assertThat(products).extracting(Product::getName).doesNotContain("product3");}


    @Test
    void testDeleteProduct() {
        List<Product> products = db.getAllProducts();
        db.deleteProduct(4);
        products = db.getAllProducts();

        assertThat(products).extracting(Product::getName).doesNotContain("product4");
    }


    @Test
    void testGetAllProduct(){
        List<Product> products = db.getAllProducts();

        assertThat(products)
                .extracting(Product::getName)
                .containsExactly("product1", "product2", "product3", "product4", "product5", "product6");
    }

    @Test
    void testGetAllCategory(){
        List<Category> categories = db.getAllCategories();

        assertThat(categories)
                .extracting(Category::getName)
                .containsExactly("category1", "category2", "category3");
    }



}
