package Baiscs;


public class Category {
    private Integer id;
    private String name;
    private String description;


    /**EVERYTHING constructor*/
    public Category(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**NO ID constructor*/
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

}