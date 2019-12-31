package ui.component.simple.template;

import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheManager;
import control.Select;
import control.Target;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import misc.FileUtil;
import misc.HttpUtil;
import misc.Project;
import org.apache.commons.text.WordUtils;
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.TextNode;
import ui.main.center.PaneGallery;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;
import ui.stage.StageManager;
import ui.stage.template.tageditstage.TagEditStageResult;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum ButtonTemplates {
	ENTITY_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				File entityFile = new File(FileUtil.getFileEntity(Target.get()));
				try {
					Desktop.getDesktop().open(entityFile);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenu.hideAll();
				}
			});
			return textNode;
		}
	},
	ENTITY_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String entityFilePath = FileUtil.getFileEntity(Target.get());
				try {
					Runtime.getRuntime().exec("mspaint.exe " + entityFilePath);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenu.hideAll();
				}
			});
			return textNode;
		}
	},
	ENTITY_COPY_NAME {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Name", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Target.get()));
				Clipboard.getSystemClipboard().setContent(content);
				
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Target.get()));
				Clipboard.getSystemClipboard().setContent(content);
				
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_REVERSE_IMAGE_SEARCH {
		public TextNode get() {
			TextNode textNode = new TextNode("Reverse Image Search", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				HttpUtil.googleReverseImageSearch(Target.get());
				StageManager.getErrorStage().show("Info", "Request sent.");
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar Files", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.showSimilar(Target.get());
				StageManager.getStageMain().getSceneMain().viewGallery();
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	FILTER_RANDOM {
		public TextNode get() {
			TextNode textNode = new TextNode("Random", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().set(PaneGallery.get().getEntitiesOfTiles().getRandom());//todo is this right?
				//Target.set(entity);//todo ??
				Reload.start();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().setAll(Filter.getEntities());
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_SET_NONE {
		public TextNode get() {
			TextNode textNode = new TextNode("Select None", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().clear();
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete Selection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().deleteFiles();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	TAG_GROUP_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag Group", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String groupBefore = ClickMenu.getGroup();
				String groupAfter = WordUtils.capitalize(StageManager.getGroupEditStage().show(groupBefore).toLowerCase());
				
				TagList.getMainInstance().forEach(tag -> {
					if (tag.getGroup().equals(groupBefore)) {
						tag.setGroup(groupAfter);
					}
				});
				
				PaneFilter.get().getGroupNode(groupBefore).setGroup(groupAfter);
				PaneSelect.get().getGroupNode(groupBefore).setGroup(groupAfter);
				
				ClickMenu.hideAll();
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_GROUP_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag Group", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				
				if (StageManager.getOkCancelStage().show("Remove \"" + group + "\" and all of its tags?")) {
					for (String name : TagList.getMainInstance().getNames(group)) {
						PaneFilter.get().getGroupNode(group).removeNameNode(name);
						PaneSelect.get().getGroupNode(group).removeNameNode(name);
						Tag tag = TagList.getMainInstance().getTag(group, name);
						EntityList.getMain().forEach(entity -> entity.getTagList().remove(tag));
						Filter.getListManager().unlist(tag);
						TagList.getMainInstance().remove(tag);
						Reload.notify(ChangeIn.TAG_LIST_MAIN);
					}
				}
				
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	TAG_GROUP_WHITELIST {
		public TextNode get() {
			TextNode textNode = new TextNode("Whitelist All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.getListManager().whitelist(ClickMenu.getGroup());
				Filter.refresh();
				ClickMenu.hideAll();
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_GROUP_BLACKLIST {
		public TextNode get() {
			TextNode textNode = new TextNode("Blacklist All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.getListManager().blacklist(ClickMenu.getGroup());
				Filter.refresh();
				ClickMenu.hideAll();
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_GROUP_UNLIST {
		public TextNode get() {
			TextNode textNode = new TextNode("Unlist All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.getListManager().unlist(ClickMenu.getGroup());
				Filter.refresh();
				ClickMenu.hideAll();
				Reload.start();
			});
			return textNode;
		}
	},
	
	TAG_NAME_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String groupBefore = ClickMenu.getGroup();
				String nameBefore = ClickMenu.getName();
				Tag tag = TagList.getMainInstance().getTag(groupBefore, nameBefore);
				
				TagEditStageResult result = StageManager.getTagEditStage().show(groupBefore, nameBefore);
				String groupAfter = WordUtils.capitalize(result.getGroup().toLowerCase());
				String nameAfter = WordUtils.capitalize(result.getName().toLowerCase());
				
				tag.set(groupAfter, nameAfter);
				TagList.getMainInstance().sort();
				
				if (result.isAddToSelect()) {
					Select.getEntities().addTag(tag);
				}
				
				if (!groupBefore.equals(groupAfter)) {
					PaneFilter.get().getGroupNode(groupBefore).setGroup(groupAfter);
					PaneSelect.get().getGroupNode(groupBefore).setGroup(groupAfter);
				}
				
				if (!nameBefore.equals(nameAfter)) {
					PaneFilter.get().getGroupNode(groupAfter).getNameNode(nameBefore).setText(nameAfter);
					PaneSelect.get().getGroupNode(groupAfter).getNameNode(nameBefore).setText(nameAfter);
				}
				
				ClickMenu.hideAll();
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_NAME_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				String name = ClickMenu.getName();
				
				Tag tag = TagList.getMainInstance().getTag(group, name);
				if (StageManager.getOkCancelStage().show("Remove \"" + tag.getFull() + "\" ?")) {
					PaneFilter.get().getGroupNode(group).removeNameNode(name);
					PaneSelect.get().getGroupNode(group).removeNameNode(name);
					EntityList.getMain().forEach(entity -> entity.getTagList().remove(tag));
					Filter.getListManager().unlist(tag);
					TagList.getMainInstance().remove(tag);
					Reload.notify(ChangeIn.TAG_LIST_MAIN);
				}
				
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	COLLECTION_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				EntityCollectionUtil.create();
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	COLLECTION_DISCARD {
		public TextNode get() {
			TextNode textNode = new TextNode("Discard Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				EntityCollectionUtil.discard(Target.get());
				Reload.start();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	CACHE_RESET {
		public TextNode get() {
			TextNode textNode = new TextNode("Reset Cache", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				CacheManager.stopThread();
				for (Entity entity : EntityList.getMain()) {
					try {
						Files.delete(Paths.get(FileUtil.getFileCache(entity)));
					} catch (IOException e) {
						e.printStackTrace();
					}
					entity.getGalleryTile().setImage(null);
				}
				CacheManager.checkCacheInBackground(EntityList.getMain());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	APPLICATION_SAVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Project.getCurrent().writeToDisk();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_IMPORT {
		public TextNode get() {
			TextNode textNode = new TextNode("Import", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				FileUtil.importFiles();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_EXIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Exit", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
					StageManager.getStageMain().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
}