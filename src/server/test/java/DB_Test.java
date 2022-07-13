import Basics.Category;
import Basics.Product;
import DB.DB;
import org.junit.jupiter.api.BeforeAll;


import java.util.List;

public class DB_Test {
    private static final DB db = new DB();

    @BeforeAll
    static void initDataBase(){
        db.initDatabase(":memory:");
    }


    //add stuff test
    //delete everything test
    //update category, update product test
    //delete category, delete product test
    //something else??

}
