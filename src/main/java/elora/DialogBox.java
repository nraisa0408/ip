package elora;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * A single chat bubble: an avatar next to a speech-bubble label,
 * displayed left-aligned for Elora and right-aligned for the user.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError("DialogBox.fxml should always be on the classpath", e);
        }
        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(32, 32, 32));
    }

    /**
     * Creates a dialog box for a message the user typed, right-aligned
     * with the avatar on the right.
     *
     * @param text The user's message.
     * @param img The user's avatar.
     * @return The dialog box to add to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for one of Elora's replies, left-aligned with
     * the avatar on the left.
     *
     * @param text Elora's reply.
     * @param img Elora's avatar.
     * @return The dialog box to add to the conversation.
     */
    public static DialogBox getEloraDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Reverses this box's children (avatar and label) and re-aligns them
     * to the left, turning a user-style bubble into an Elora-style one.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
