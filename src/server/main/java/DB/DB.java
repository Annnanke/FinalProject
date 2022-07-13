package DB;

import Baiscs.Category;
import Baiscs.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {


    private Connection con;

    /**init*/
    public void initDatabase(String name) {
        try {

            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + name);


            PreparedStatement pst = con.prepareStatement("PRAGMA foreign_keys = ON;");
            pst.executeUpdate();


            pst = con.prepareStatement(
                    "create table if not exists 'category' (" +
                            "'id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "'name' text UNIQUE ," +
                            "'description' text);"
            );
            pst.executeUpdate();

            pst = con.prepareStatement("create table if not exists 'product' (" +
                    "'id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "'name' text UNIQUE," +
                    "'description' text," +
                    "'producer' text," +
                    "'price' double," +
                    "'amount' double," +
                    "'category_id' Integer," +
                    "FOREIGN KEY(category_id) REFERENCES category(id) ON UPDATE CASCADE ON DELETE CASCADE " +
                    ");"
            );
            pst.executeUpdate();



            pst = con.prepareStatement("create table if not exists 'users'" +
                    "( 'id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "'login' text unique," +
                    "'password' text);");

            pst.executeUpdate();
            deleteAll();//add the method



        } catch (ClassNotFoundException e) {
            System.out.println("JDBC DRIVER NOT FOUND");
            e.printStackTrace();
            System.exit(0);



        } catch (SQLException e) {
            System.out.println("SQR QUERY ERROR");
            e.printStackTrace();
        }

    }

    /**DELETE EVERYTHING*/
    public void deleteAll() {
        try {
            PreparedStatement st = con.prepareStatement("DELETE FROM category");
            st.executeUpdate();
            st = con.prepareStatement("UPDATE 'sqlite_sequence' SET 'seq' = 0 WHERE name = 'category'");
            st.executeUpdate();

            st = con.prepareStatement("DELETE FROM product");
            st.executeUpdate();
            st = con.prepareStatement("UPDATE 'sqlite_sequence' SET 'seq' = 0 WHERE name = 'product'");
            st.executeUpdate();
            st = con.prepareStatement("DELETE FROM users");
            st.executeUpdate();
            st = con.prepareStatement("UPDATE 'sqlite_sequence' SET 'seq' = 0 WHERE name = 'users'");
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH DELETING PRODUCT", e);
        }
    }


    //PRODUCT
    /**is the product present?*/
    public boolean isProductPresent(int id) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product where id = " + id + ";");
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("NO PRODUCT FOUND", e);
        }
        return false;
    }

    /**is the product present, check by name*/
    public boolean isProductPresent_ByName(String name) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product where name = '" + name + "';");
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("PRODUCT NOT FOUND", e);
        }
        return false;
    }


   /**insert a product*/
    public Product insertProduct(Product product) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO product(name, description, producer, price, amount, category_id) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getDistributor());
            statement.setDouble(4, product.getPrice());
            statement.setDouble(5, product.getAmount());
            statement.setInt(6, product.getCategory_id());
            statement.executeUpdate();

            ResultSet resSet = statement.getGeneratedKeys();
            product.setId(resSet.getInt("last_insert_rowid()"));
            statement.close();
            return product;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH PRODUCT INSERTION", e);
        }
    }

   /**get product by ID*/
    public Product getProductByID(int ID){

        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product WHERE id = " + ID + ";");


            if(res.next()){
                return getProductFromResultSet(res);
            } else return null;


        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("ISSUES WITH SQL QUERY FOR PRODUCT SELECTED BY ID", e);
        }
    }

   /**List of ALL products getter*/
    public List<Product> getAllProducts() {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product");
            List<Product> products = new ArrayList<>();
            while (res.next()) {
                products.add(getProductFromResultSet(res));
            }
            res.close();
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUES WITH SQL QUERY FOR SELECTED PRODUCTS", e);
        }
    }

    public List<Product> getProductsByCategoryId(int categoryId) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product WHERE category_id = " + categoryId + ";");
            List<Product> products = new ArrayList<>();
            while (res.next()) {
                products.add(getProductFromResultSet(res));
            }
            res.close();
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUES WITH SQL QUERY FOR SELECTED PRODUCTS", e);
        }
    }

    private static Product getProductFromResultSet(ResultSet res) throws SQLException {
        return new Product(res.getInt("id"),
                res.getString("name"),
                res.getString("description"),
                res.getString("producer"),
                res.getDouble("price"),
                res.getDouble("amount"),
                res.getInt("category_id"));
    }

    public void updateProduct(Product product){
        try{
            Statement st = con.createStatement();
            st.executeUpdate("UPDATE product" +
                    " SET name = '" + product.getName() + "', " +
                    "description = '" + product.getDescription() +"', " +
                    "distributor ='" + product.getDistributor() + "', " +
                    "price = " + product.getPrice() + "," +
                    "amount = " + product.getAmount() + "," +
                    "category_id = " + product.getCategory_id() + " " +
                    "WHERE id = " + product.getId() +
                    ";");
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH UPDATING THE PRODUCT", e);
        }
    }


    /**Delete a product*/
    public void deleteProduct(int id) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate("DELETE FROM product WHERE id = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH DELETING THE PRODUCT", e);
        }
    }



    //CATEGORY
    /**get category by ID*/
    public Category getCategoryByID(int ID) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category WHERE id = " + ID + ";");

            if (res.next()) {
                return  getCategoryFromResultSet(res);

            } else return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH SQL QUERY FOR CATEGORY SELECTED BY ID", e);
        }
    }


    /**get category from the result set*/
    private static Category getCategoryFromResultSet(ResultSet res) throws SQLException {

        return new Category(res.getInt("id"),
                res.getString("name"),
                res.getString("description"));
    }



   /**is category present?*/
    public boolean isCategoryPresent(int ID) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category where id = " + ID + ";");
            if (res.next()) {
                return true;

            }
        } catch (SQLException e) {
            throw new RuntimeException("CATEGORY NOT FOUND", e);
        }
        return false;
    }


    /**is category present by name*/
    public boolean isCategoryPresent_ByName(String name) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category where name = '" + name + "';");
            if (res.next()) {
                return true;

            }
        } catch (SQLException e) {
            throw new RuntimeException("CATEGORY NOT FOUND", e);
        }
        return false;
    }


    /**insert a category*/
    public Category insertCategory(Category category) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO category(name, description) VALUES (?, ?)");

            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();

            ResultSet resSet = statement.getGeneratedKeys();
            category.setId(resSet.getInt("last_insert_rowid()"));
            statement.close();

            return category;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUES WITH INSERTING A CATEGORY", e);
        }
    }



    /**update a category*/
    public void updateCategory(Category category){
        try{
            Statement st = con.createStatement();
            st.executeUpdate("UPDATE category" +
                    " SET name = '" + category.getName() + "', " +
                    "description = '" + category.getDescription()  +
                    "' WHERE id = " + category.getId()  +
                    ";");
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("ISSUES WITH UPDATING CATEGORY", e);
        }
    }



    /**get ALL the categories*/
    public List<Category> getAllCategories() {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category");
            List<Category> categories = new ArrayList<>();
            while (res.next()) {
                categories.add(getCategoryFromResultSet(res));
            }
            res.close();
            return categories;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUES WITH SQL QUERY FOR SELECTED CATEGORIES", e);
        }
    }

   /**delete category*/
    public void deleteCategory(int id) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate("DELETE FROM category WHERE id = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH DELETE CATEGORY", e);
        }
    }










}
