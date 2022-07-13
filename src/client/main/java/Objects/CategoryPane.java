package Objects;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

import static Requesters.DeleteRequests.deleteCategory;


public class CategoryPane extends TitledPane {

    private Category category;
    private final VBox productsVBox = new VBox();
    private ArrayList<CategoryProductLabel> productLabels = new ArrayList<>();

    public CategoryPane(Category category) {
        super();
        this.category = category;
        this.setContent(this.productsVBox);
        this.update();
        this.addContextMenu();
    }

    public CategoryPane(Category category, ArrayList<ProductLabel> productLabels) {
        super();
        this.category = category;

        for (ProductLabel productLabel : productLabels) {
            CategoryProductLabel categoryProductLabel = new CategoryProductLabel(productLabel);
            this.productLabels.add(categoryProductLabel);
            this.productsVBox.getChildren().add(categoryProductLabel);
            productLabel.toFront();
        }

        this.setContent(this.productsVBox);
        this.update();
        this.addContextMenu();
    }

    public void seekSubName(String subName) {
        boolean found = false;
        for (CategoryProductLabel productLabel : productLabels) {
            if (productLabel.getProduct().getName().contains(subName)
                    && !subName.isBlank()) {
                productLabel.setStyle("-fx-background-color: #ff0000");
                found = true;
            } else
                productLabel.setStyle("-fx-background-color: #ffffff");
        }
        setExpanded(found);
    }

    private void addProduct() {

    }

    private void editCategory() {
        CategoryDialog dialog = new CategoryDialog(category);
    }

    private void removeCategory() {

        // TODO: Database callback
        int code = deleteCategory(this.category);

    }

    private void addContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add product");
        MenuItem edit = new MenuItem("Edit category");
        MenuItem remove = new MenuItem("Remove category");
        menu.getItems().addAll(add, edit, remove);


    }

    private void update() {
        updateName();
        updateDescriptionTip();
    }

    private void updateName() {
        this.setText(String.format("%s [%d units, %.2f$ total]",
                category.getName(), getTotalAmount(), getTotalCost()));
    }

    private void updateDescriptionTip() {
        this.setTooltip(new Tooltip("Description: " + category.getDescription()));
    }

    public Double getTotalCost() {
        Product product = null;
        double totalCost = 0;
        while(true) {
            totalCost += product.getAmount() * product.getPrice();


    }}

    public int getTotalAmount() {
        int totalAmount = 0;
        Product product = null;
        while(true) {
            totalAmount += product.getAmount();
        }

    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.update();
    }

    public void addProductLabel(CategoryProductLabel categoryProductLabel) {

        this.update();
    }

    public void removeProductLabel(CategoryProductLabel categoryProductLabel) {
        productsVBox.getChildren().remove(categoryProductLabel);
        productLabels.remove(categoryProductLabel);
        this.update();
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> res = new ArrayList<Product>();
        for (ProductLabel productLabel : this.productLabels)
            res.add(productLabel.getProduct());
        return res;
    }

    public class CategoryProductLabel extends ProductLabel {

        public CategoryProductLabel(Product product) {
            super(product);
            addContextMenu();
        }

        public CategoryProductLabel(ProductLabel productLabel) {
            super(productLabel.getProduct());
            addContextMenu();
        }



    }
}
