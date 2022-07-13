package Objects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

import static Requesters.DeleteRequests.deleteCategory;
import static Requesters.DeleteRequests.deleteProduct;
import static Requesters.PutRequests.putProductRequest;
import static Requesters.UpdateRequests.updateCategoryRequest;
import static Requesters.UpdateRequests.updateProductRequest;


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
        ProductDialog dialog = new ProductDialog(category.getId());
        Optional<Product> results = dialog.showAndWait();
        System.out.println(getTotalCost());
        results.ifPresent((Product result) -> {
            // TODO: Database callback
            int[] array  = putProductRequest(result);
            if(array[0] == 201) {
                result.setId(array[1]);
                this.addProductLabel(new CategoryProductLabel(result));
            } else{
                // TODO: Add error;
                System.out.println("ERROR");
            }
        });

        // ProductLabel productLabel = new ProductLabel(product);

        // TODO: Database callback
        // addProductLabel(productLabel);
    }

    private void editCategory() {
        CategoryDialog dialog = new CategoryDialog(category);
        Optional<Category> results = dialog.showAndWait();
        results.ifPresent((Category result) -> {
            // TODO: Database callback
            int code = updateCategoryRequest(result);
            if(code == 204){
                this.setCategory(result);
            } else{
                // TODO: Error
                System.out.println("Error");
            }

            System.out.println("Got result!");
        });
    }

    private void removeCategory() {

        // TODO: Database callback
        int code = deleteCategory(this.category);
        if(code != 204){
            System.out.println("Error");
        }

    }

    private void addContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add product");
        MenuItem edit = new MenuItem("Edit category");
        MenuItem remove = new MenuItem("Remove category");
        menu.getItems().addAll(add, edit, remove);

        add.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                CategoryPane.this.addProduct();
            }
        });

        edit.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                CategoryPane.this.editCategory();
            }
        });

        remove.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                CategoryPane.this.removeCategory();
            }
        });
        this.setContextMenu(menu);
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
        Double totalCost = 0d;
        for (Product product : getProducts())
            totalCost += product.getAmount() * product.getPrice();
        return totalCost;
    }

    public int getTotalAmount() {
        int totalAmount = 0;
        for (Product product : getProducts())
            totalAmount += product.getAmount();
        return totalAmount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.update();
    }

    public void addProductLabel(CategoryProductLabel categoryProductLabel) {
        productsVBox.getChildren().add(categoryProductLabel);
        productLabels.add(categoryProductLabel);
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

    /** Class for units inside category list **/

    public class CategoryProductLabel extends ProductLabel {

        public CategoryProductLabel(Product product) {
            super(product);
            addContextMenu();
        }

        public CategoryProductLabel(ProductLabel productLabel) {
            super(productLabel.getProduct());
            addContextMenu();
        }


        private void addAmount() {
            AddSubstractAmount dialog = new AddSubstractAmount(true, this.getProduct().getAmount());
            Optional<Double> results = dialog.showAndWait();
            results.ifPresent((Double result) -> {
                // TODO: Database callback
                this.addAmount(result);
                this.update();
                CategoryPane.this.update();

                int code = updateProductRequest(this.getProduct());
                if(!(code == 204)){
                    System.out.println("Error");
                }
                this.update();
                CategoryPane.this.update();
            });
        }

        private void reduceAmount() {
            AddSubstractAmount dialog = new AddSubstractAmount(false, this.getProduct().getAmount());
            Optional<Double> results = dialog.showAndWait();
            results.ifPresent((Double result) -> {
                // TODO: Database callback
                this.addAmount(-result);
                this.update();
                CategoryPane.this.update();

                int code = updateProductRequest(this.getProduct());
                if(!(code == 204)){
                    System.out.println("Error");
                }
                this.update();
                CategoryPane.this.update();
            });
        }

        private void editProduct() {
            ProductDialog dialog = new ProductDialog(category.getId(), getProduct());
            Optional<Product> results = dialog.showAndWait();
            System.out.println(getTotalCost());
            results.ifPresent((Product result) -> {
                // TODO: Database callback
                int code = updateProductRequest(result);
                if(code == 204){
                    this.setProduct(result);
                } else{
                    System.out.println("Error");
                }
                this.setProduct(result);
                this.update();
                CategoryPane.this.update();
            });
            System.out.println(getTotalCost());
        }

        private void removeProduct() {
            // TODO: Database callback
            int code =    deleteProduct(this.getProduct());
            if(code != 204){
                System.out.println("Error");
            }
            this.getProduct();
            removeProductLabel(this);

        }

        private void addContextMenu() {
            ContextMenu menu = new ContextMenu();
            MenuItem add = new MenuItem("Add amount");
            MenuItem reduce = new MenuItem("Reduce amount");
            MenuItem edit = new MenuItem("Edit product");
            MenuItem remove = new MenuItem("Remove product");
            menu.getItems().addAll(add, reduce, edit, remove);

            add.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    CategoryProductLabel.this.addAmount();
                }
            });

            reduce.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    CategoryProductLabel.this.reduceAmount();
                }
            });

            edit.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    CategoryProductLabel.this.editProduct();
                }
            });

            remove.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    CategoryProductLabel.this.removeProduct();
                }
            });
            this.setContextMenu(menu);
        }

    }
}
