package elora;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for MainWindow.fxml: the chat window that displays the
 * conversation between the user and Elora, and forwards typed input to
 * an {@link Elora} instance.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Elora elora;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image eloraImage = new Image(getClass().getResourceAsStream("/images/DaElora.png"));

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Connects this window to the given Elora instance and displays its
     * welcome message as the first bubble in the conversation.
     *
     * @param elora The Elora instance backing this window.
     */
    public void setElora(Elora elora) {
        this.elora = elora;
        dialogContainer.getChildren().add(DialogBox.getEloraDialog(elora.getWelcomeMessage(), eloraImage));
    }

    /**
     * Reads the text in the input field, sends it to Elora, and appends
     * both the user's message and Elora's reply as dialog bubbles. Exits
     * the application if the input was the "bye" command.
     */
    @FXML
    private void handleUserInput() {
        assert elora != null : "setElora() must be called before the window can handle input";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = elora.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getEloraDialog(response, eloraImage));
        userInput.clear();
        if (elora.isExitCommand(input)) {
            Platform.exit();
        }
    }
}
