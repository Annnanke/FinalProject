package DB;

import Baiscs.Category;

import java.sql.*;

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
            throw new RuntimeException("ISSUE WITH DELETE PRODUCT", e);
        }
    }



    /**get category by ID*/
    public Category getCategoryByID(int ID) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category WHERE ID = " + ID + ";");

            if (res.next()) {
                return null;
                //get category by result

            } else return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("ISSUE WITH SQL QUERY FOR CATEGORY SELECTED BY ID", e);
        }
    }



   /**is category present?*/
    public boolean isCategoryPresent(int id) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM category where id = " + id + ";");
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





}
