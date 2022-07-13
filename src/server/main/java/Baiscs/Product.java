package Baiscs;

public class Product {
    private Integer id;
    private String name;
    private String description;

    private String distributor;
    private Double amount;
    private Double price;
    private Integer category_id;


    /**EVERYTING constructor*/
    public Product(Integer id, String name, String description, String distributor, Double amount, Double price, Integer category_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.distributor = distributor;
        this.amount = amount;
        this.price = price;
        this.category_id = category_id;
    }
    /**NO ID constructor*/
    public Product(String name, String description, String distributor, Double amount, Double price, Integer category_id) {
        this.name = name;
        this.description = description;
        this.distributor = distributor;
        this.amount = amount;
        this.price = price;
        this.category_id = category_id;
    }

    /**NO ID NO CATEGORY ID constructor*/
    public Product(String name, String description, String producer, Double amount, Double price) {
        this.name = name;
        this.description = description;
        this.distributor = producer;
        this.amount = amount;
        this.price = price;
    }

    //GETTERS AND SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String producer) {
        this.distributor = distributor;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }



}