package application.backend.base.tag;

import application.backend.base.entity.Entity;
import application.backend.control.Reload;
import application.backend.util.FileUtil;
import application.backend.util.JsonUtil;
import application.frontend.stage.StageManager;
import application.main.InstanceCollector;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;

import java.lang.reflect.Type;

public class TagListMain extends TagList implements InstanceCollector {
	private static final Type typeToken = new TypeToken<TagListMain>() {}.getType();
	
	public void initialize() {
		TagList allTags = readDummyFromDisk();
		if (allTags != null) {
			this.addAll(allTags);
		}
		
		for (Entity entity : entityListMain) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (this.containsEqual(tag)) {
					tagList.set(tagList.indexOf(tag), getTag(tag));
				} else {
					this.add(tag);
				}
			}
		}
		
		super.sort();
	}
	
	public void writeDummyToDisk() {
		JsonUtil.write(this, typeToken, FileUtil.getFileTags());
	}
	private TagList readDummyFromDisk() {
		return (TagList) JsonUtil.read(typeToken, FileUtil.getFileTags());
	}
	
	public boolean add(Tag tag) {
		if (super.add(tag)) {
			reload.notify(Reload.Control.TAGS);
			
			return true;
		}
		
		return false;
	}
	public boolean remove(Tag tag) {
		if (super.remove(tag)) {
			reload.notify(Reload.Control.TAGS);
			
			return true;
		}
		
		return false;
	}
	public boolean edit(Tag tagOld) {
		if (tagOld != null) {
			Pair<Tag, Boolean> result = StageManager.getTagEditStage()._show(tagOld.getGroup(), tagOld.getName());
			Tag tagNew = result.getKey();
			
			if (tagNew != null) {
				tagOld.set(tagNew.getGroup(), tagNew.getName());
				
				super.sort();
				
				if (result.getValue()) {
					select.addTagObject(tagOld);
				}
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}