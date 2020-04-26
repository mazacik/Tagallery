package frontend.decorator;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum DecoratorTemplate {
	PRESET_BRIGHT("Bright", Color.BLACK, Color.DARKORANGE, Color.GREEN, Color.RED, Color.CORNFLOWERBLUE, Color.rgb(96, 94, 92), getBackground("#DDDEEE"), getBackground("#CCCDDD")),
	PRESET_DARK("Dark", Color.LIGHTGRAY, Color.ORANGE, Color.LIGHTGREEN, Color.ORANGERED, Color.CORNFLOWERBLUE, Color.rgb(96, 94, 92), getBackground("#3C3F41"), getBackground("#313335")),
	;
	
	private final String displayName;
	
	private final Color colorPrimary;
	private final Color colorSecondary;
	private final Color colorPositive;
	private final Color colorNegative;
	private final Color colorUnion;
	
	private final Color colorBorder;
	
	private final Background backgroundPrimary;
	private final Background backgroundSecondary;
	
	DecoratorTemplate(String displayName, Color colorPrimary, Color colorSecondary, Color colorPositive, Color colorNegative, Color colorUnion, Color colorBorder, Background backgroundPrimary, Background backgroundSecondary) {
		this.displayName = displayName;
		
		this.colorPrimary = colorPrimary;
		this.colorSecondary = colorSecondary;
		this.colorPositive = colorPositive;
		this.colorNegative = colorNegative;
		this.colorUnion = colorUnion;
		
		this.colorBorder = colorBorder;
		
		this.backgroundPrimary = backgroundPrimary;
		this.backgroundSecondary = backgroundSecondary;
	}
	
	private static Background getBackground(String hex) {
		return getBackground(Paint.valueOf(hex));
	}
	private static Background getBackground(Paint paint) {
		return new Background(new BackgroundFill(paint, null, null));
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Color getColorPrimary() {
		return colorPrimary;
	}
	public Color getColorSecondary() {
		return colorSecondary;
	}
	public Color getColorPositive() {
		return colorPositive;
	}
	public Color getColorNegative() {
		return colorNegative;
	}
	public Color getColorUnion() {
		return colorUnion;
	}
	
	public Color getColorBorder() {
		return colorBorder;
	}
	
	public Background getBackgroundPrimary() {
		return backgroundPrimary;
	}
	public Background getBackgroundSecondary() {
		return backgroundSecondary;
	}
}
