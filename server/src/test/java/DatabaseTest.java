import DB.Database;
import Basics.Category;
import Basics.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseTest {
    private static final Database db = new Database();

    @BeforeAll
    static void initDataBase(){
        db.initDatabase(":memory:");
    }

    public static final Category[] categories = new Category[]{
        new Category("category1", "descr1"),
        new Category("category2", "descr2"),
        new Category("category3", "descr2"),
    };

    public static final Product[] products = new Product[]{
            new Product("product1", "descr", "prod", 25.0, 35.0, 1),
            new Product("product2", "descr", "prod", 245.0, 345.0, 1),
            new Product("product3", "descr", "prod", 265.0, 335.0, 2),
            new Product("product4", "descr", "prod", 125.0, 35.0, 2),
            new Product("product5", "descr", "prod", 625.0, 335.0, 3),
            new Product("product6", "descr", "prod", 225.0, 375.0, 3),

    };

    @BeforeEach
    void fillDataBase(){
        for (Category c: categories) {
            db.insertCategory(c);
        }

        for (Product p : products) {
            db.insertProduct(p);
        }
    }

    @AfterEach
    void clean(){
        db.deleteAll();
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


    @Test
    void testUpdateProduct(){
        Product product = db.insertProduct(new Product("product7", "descr", "prod", 25.0, 35.0, 1));
        db.updateProduct(new Product(product.getId(), "product7", "descr", "prod", 100.0, 35.0, 1));
        List<Product> products = db.getAllProducts();
        assert(products.get(6).getPrice() == 100.0);
    }

    @Test
    void testUpdateCategory(){
        Category category = db.insertCategory(new Category("category4", "descr"));
        db.updateCategory(new Category(category.getId(), "category4", "super-description"));
        List<Category> categories = db.getAllCategories();
        System.out.println(categories.get(3).getDescription());
        assert(categories.get(3).getDescription().equals("super-description"));
    }

    @Test
    void testDeleteProduct(){
        db.deleteProduct(1);
        List<Product> products = db.getAllProducts();
        assertThat(products).extracting(Product::getName).doesNotContain("product1");
    }

    @Test
    void testDeleteCategory(){
        db.deleteCategory(1);
        List<Category> categories = db.getAllCategories();
        assertThat(categories).extracting(Category::getName).doesNotContain("category1");
        List<Product> products = db.getAllProducts();
        assertThat(products).extracting(Product::getName).doesNotContain("product1");
    }

}
