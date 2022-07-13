package Baiscs;


public class User {
    private Integer id;
    private String login;
    private String password;




    /**NO ID constructor*/
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }


    /**EVERYTHING constructor*/
    public User(Integer id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }



    //GETTERS AND SETTERS
    public Integer getId() {
    return id;
}

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    @Override
    public String toString() {
        return "User {" +
                "id =" + id +
                ", login ='" + login + '\'' +
                ", password ='" + password + '\'' +
                '}';
    }
}
