package Objects;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AddSubstractAmount extends Dialog<Double> {
    public AddSubstractAmount(boolean add, Double currentAmount) {
        super();
        Label addLabel;
        if (add) {
            this.setTitle("Add amount");
            addLabel = new Label("How much do you want to add?");
        }
        else {
            this.setTitle("Subtract amount");
            addLabel = new Label("How much do you want to subtract?");
        }

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        TextField amount = new TextField();

        dialogPane.setContent(new VBox(5, addLabel, amount));

        this.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.APPLY) {
                try {
                    Double amountValue = Double.parseDouble(amount.getText());
                    if ((add ? amountValue : -amountValue) + currentAmount < 0)
                    return Double.parseDouble(amount.getText());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.showAndWait();
                }
            }
            return null;
        });
    }

}
