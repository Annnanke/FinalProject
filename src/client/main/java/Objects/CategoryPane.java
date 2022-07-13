package Objects;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;



public class CategoryPane extends TitledPane {
    private Category category;
    private final VBox productsVBox = new VBox();


    private void updateDescriptionTip() {
        this.setTooltip(new Tooltip("Description: " + category.getDescription()));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {

    }


        private void addContextMenu() {
            ContextMenu menu = new ContextMenu();
            MenuItem add = new MenuItem("Add amount");
            MenuItem reduce = new MenuItem("Reduce amount");
            MenuItem edit = new MenuItem("Edit product");
            MenuItem remove = new MenuItem("Remove product");
            menu.getItems().addAll(add, reduce, edit, remove);



    }
}
