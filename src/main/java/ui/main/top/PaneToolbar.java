package ui.main.top;

import base.CustomList;
import base.entity.Entity;
import control.Select;
import enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import ui.custom.ClickMenu;
import ui.custom.TitleBar;
import ui.decorator.Decorator;
import ui.main.stage.StageMain;
import ui.node.NodeBoxSeparator;
import ui.node.NodeTemplates;
import ui.node.NodeText;
import ui.override.HBox;

public class PaneToolbar extends BorderPane {
	public static final double PREF_HEIGHT = 30;
	
	private NodeText nodeTarget;
	
	public void init() {
		TitleBar titleBar = new TitleBar(StageMain.getInstance());
		
		NodeText nodeFile = new NodeText("File", true, true, false, true);
		NodeText nodeSave = NodeTemplates.APPLICATION_SAVE.get();
		NodeText nodeImport = NodeTemplates.APPLICATION_IMPORT.get();
		NodeText nodeCacheReset = NodeTemplates.CACHE_RESET.get();
		NodeText nodeExit = NodeTemplates.APPLICATION_EXIT.get();
		ClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, nodeCacheReset, new NodeBoxSeparator(), nodeExit);
		
		nodeTarget = new NodeText("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY, ClickMenu.StaticInstance.ENTITY);
		
		HBox hBox = new HBox(nodeFile);
		hBox.setAlignment(Pos.CENTER);
		
		titleBar.setCenter(nodeTarget);
		titleBar.setBorder(null);
		
		this.setBorder(Decorator.getBorder(0, 0, 1, 0));
		this.setPrefHeight(PREF_HEIGHT);
		this.setLeft(hBox);
		this.setCenter(titleBar);
	}
	
	public boolean reload() {
		Entity currentTarget = Select.getTarget();
		if (currentTarget.getCollectionID() != 0) {
			CustomList<Entity> collection = currentTarget.getCollection();
			String collectionIndex = (collection.indexOf(currentTarget) + 1) + "/" + collection.size();
			nodeTarget.setText("[" + collectionIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
	
	private PaneToolbar() {super();}
	private static class Loader {
		private static final PaneToolbar INSTANCE = new PaneToolbar();
	}
	public static PaneToolbar getInstance() {
		return Loader.INSTANCE;
	}
}
