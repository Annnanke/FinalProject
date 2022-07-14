package DB;

import Basics.Category;
import Basics.Product;
import Basics.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection con;

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
            deleteAll();
        } catch (ClassNotFoundException e) {
            System.out.println("Can`t find driver JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("SQR query exception");
            e.printStackTrace();
        }
    }

    // Category
    public Category GetCategoryById(int id) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category WHERE id = " + id + ";");

            if (res.next()) {
                return getCategoryFromResultSet(res);
            } else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with SQL query for select category by id", e);
        }
    }
    
    public boolean isCategoryPresent(int id) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category where id = " + id + ";");
            if (res.next()) {
                return true;

            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t find category", e);
        }
        return false;
    }


    public boolean isCategoryPresentByName(String name) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category where name = '" + name + "';");
            if (res.next()) {
                return true;

            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t find category", e);
        }
        return false;
    }


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
            throw new RuntimeException("Problem with insert category", e);
        }
    }

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
            throw new RuntimeException("Problem with UPDATE category", e);
        }
    }    
    
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
            throw new RuntimeException("Problems with SQL query for select categorys", e);
        }
    }

    private static Category getCategoryFromResultSet(ResultSet res) throws SQLException {
        return new Category(res.getInt("id"),
                res.getString("name"),
                res.getString("description"));
    }

    public void deleteCategory(int id) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate("DELETE FROM category WHERE id = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem with DELETE from category", e);
        }
    }

    // Product
    public boolean isProductPresent(int id) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product where id = " + id + ";");
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t find product", e);
        }
        return false;
    }

    public boolean isProductPresentByName(String name) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product where name = '" + name + "';");
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t find product", e);
        }
        return false;
    }


    public Product insertProduct(Product product) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO product(name, description, producer, price, amount, category_id) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getProducer());
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
            throw new RuntimeException("Problem with insert product", e);
        }
    }

    public Product GetProductById(int id){
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM product WHERE id = " + id + ";");

            if(res.next()){
                return getProductFromResultSet(res);
            } else return null;
        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Problems with SQL query for select product by id", e);
        }
    }

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
            throw new RuntimeException("Problems with SQL query for select products", e);
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
            throw new RuntimeException("Problems with SQL query for select products", e);
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
                    "producer ='" + product.getProducer() + "', " +
                    "price = " + product.getPrice() + "," +
                    "amount = " + product.getAmount() + "," +
                    "category_id = " + product.getCategory_id() + " " +
                    "WHERE id = " + product.getId() +
                    ";");
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Problem with UPDATE product", e);
        }
    }

    public void deleteProduct(int id) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate("DELETE FROM product WHERE id = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem with DELETE from product", e);
        }
    }

    // User
    public User insertUser(User user) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO users(login, password) VALUES (?, ?)");

            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());

            statement.executeUpdate();
            ResultSet resSet = statement.getGeneratedKeys();
            user.setId(resSet.getInt("last_insert_rowid()"));
            statement.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem with insert user", e);
        }
    }

    public User getUserByLogin(String login) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM users where login = '" + login + "'");
            if (res.next())
                return new User(res.getInt("id"), res.getString("login"), res.getString("password"));
        } catch (SQLException e) {
            throw new RuntimeException("Can`t get user", e);
        }
        return null;
    }

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
            throw new RuntimeException("Problem with DELETE from product", e);
        }
    }


}