package ui.node.arrowtextnode;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import ui.decorator.Decorator;
import ui.node.textnode.TextNode;

public class ArrowTextNode extends BorderPane {
	private static final int PADDING_V = 3;
	private static final int PADDING_H = 10;
	private static final Insets insets = new Insets(PADDING_V, PADDING_H, PADDING_V, PADDING_H);
	
	private ArrowTextNodeTemplates template;
	
	private Label nodeMain;
	private Label nodeArrow;
	
	public ArrowTextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding) {
		this(text, hoverBackground, hoverText, hoverCursor, defaultPadding, null);
	}
	public ArrowTextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding, ArrowTextNodeTemplates template) {
		this.template = template;
		
		nodeMain = new TextNode(text, false, false, false, false);
		nodeArrow = new TextNode("▶", false, false, false, false);
		
		nodeMain.minWidthProperty().unbind();
		nodeArrow.minWidthProperty().unbind();
		
		if (defaultPadding) this.setPadding(insets);
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (hoverBackground) {
				this.setBackground(Decorator.getBackgroundSecondary());
			}
			if (hoverText) {
				nodeMain.setTextFill(Decorator.getColorSecondary());
				nodeArrow.setTextFill(Decorator.getColorSecondary());
			}
			if (hoverCursor) {
				this.setCursor(Cursor.HAND);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (hoverBackground) {
				this.setBackground(Background.EMPTY);
			}
			if (hoverText) {
				nodeMain.setTextFill(Decorator.getColorPrimary());
				nodeArrow.setTextFill(Decorator.getColorPrimary());
			}
			if (hoverCursor) {
				this.setCursor(Cursor.DEFAULT);
			}
		});
		
		this.setLeft(nodeMain);
		this.setRight(nodeArrow);
	}
	
	public <T extends Event> void addMouseEvent(final EventType<T> eventType, MouseButton mouseButton, Runnable runnable) {
		this.addEventFilter(eventType, event -> {
			if (event instanceof MouseEvent) {
				MouseEvent mouseEvent = (MouseEvent) event;
				if (mouseEvent.getButton() == mouseButton) {
					runnable.run();
				}
			}
		});
	}
	
	public ArrowTextNodeTemplates getTemplate() {
		return template;
	}
}
