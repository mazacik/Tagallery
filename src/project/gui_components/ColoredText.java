package project.gui_components;

import javafx.scene.paint.Color;
import project.backend.DatabaseItem;

import java.io.Serializable;

public class ColoredText implements Serializable {

    private DatabaseItem owner;
    private String text;
    private Color color;

    public ColoredText(String text, Color color) {
        this(text, color, null);
    }

    public ColoredText(String text, Color color, DatabaseItem owner) {
        this.text = text;
        this.color = color;
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public DatabaseItem getOwner() {
        return owner;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setOwner(DatabaseItem owner) {
        this.owner = owner;
    }
}
