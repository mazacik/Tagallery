package application.gui.nodes.custom;

import application.gui.decorator.enums.ColorType;
import application.gui.nodes.simple.TextNode;
import javafx.scene.layout.VBox;

public class SwitchNodeWithTitle extends VBox {
	private TextNode titleNode;
	private SwitchNode switchNode;
	
	public SwitchNodeWithTitle(String title, String text1, String text2) {
		this(title, text1, text2, -1);
	}
	public SwitchNodeWithTitle(String title, String text1, String text2, double prefWidth) {
		titleNode = new TextNode(title, ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);
		switchNode = new SwitchNode(text1, text2, prefWidth);
		
		this.getChildren().add(titleNode);
		this.getChildren().add(switchNode);
	}
	
	public TextNode getTitleNode() {
		return titleNode;
	}
	public SwitchNode getSwitchNode() {
		return switchNode;
	}
}