package Baiscs;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private String distibutor;
    private Double amount;
    private Double price;
    private Integer category_id;


    /**EVERYTING constructor*/
    public Product(Integer id, String name, String description, String producer, Double amount, Double price, Integer category_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.distibutor = producer;
        this.amount = amount;
        this.price = price;
        this.category_id = category_id;
    }
    /**NO ID constructor*/
    public Product(String name, String description, String producer, Double amount, Double price, Integer category_id) {
        this.name = name;
        this.description = description;
        this.distibutor = producer;
        this.amount = amount;
        this.price = price;
        this.category_id = category_id;
    }

    /**NO ID NO CATEGORY ID constructor*/
    public Product(String name, String description, String producer, Double amount, Double price) {
        this.name = name;
        this.description = description;
        this.distibutor = producer;
        this.amount = amount;
        this.price = price;
    }


}