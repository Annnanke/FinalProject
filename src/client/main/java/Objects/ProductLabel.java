package Objects;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class ProductLabel extends Button {
    private Product product;

    public ProductLabel(Product product) {
        super();
        ProductLabel.this.setStyle("-fx-background-color: #ffffff");
        this.setOnAction(new EventHandler<>() {
            @Override public void handle(ActionEvent e) {
                ProductLabel.this.setStyle("-fx-background-color: #ffffff");
            }
        });
        this.product = product;
        this.update();
    }

    public void setProduct(Product product) {
        this.product = product;
        this.update();
    }


    public Product getProduct() {
        return product;
    }

    public void addAmount(Double amount) {
        this.product.setAmount(this.product.getAmount() + amount);
        this.update();
    }
//TODO: udpate objects
    protected void update() {
        this.update();
    }

}