package org.example.Ent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    protected void update() {
        this.updateText();
        this.updateDescriptionTip();
    }

    private void updateDescriptionTip() {
        this.setTooltip(new Tooltip("Description: " + product.getDescription() +
                                     "\nProducer: " + product.getProducer()));
    }

    private void updateText() {
        this.setText(product.getName() + " [" + product.getAmount() + "pcs, " + product.getPrice() + "$]");
    }
}