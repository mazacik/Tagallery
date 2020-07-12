package frontend.component.side;

import backend.BaseList;
import frontend.node.ListBox;
import frontend.node.override.VBox;
import main.Main;

public abstract class SidePaneBase extends VBox {
	public static final double MIN_WIDTH = 200;
	
	protected final BaseList<TagNode> tagNodes;
	protected final ListBox listBox;
	
	protected SidePaneBase() {
		tagNodes = new BaseList<>();
		
		listBox = new ListBox();
		
		this.setMinWidth(MIN_WIDTH);
	}
	
	public boolean reload() {
		tagNodes.clear();
		//todo holy memory leak
		Main.DB_TAG.forEach(tag -> tagNodes.add(new TagNode(this, tag)));
		listBox.setNodes(tagNodes);
		
		return true;
	}
}
