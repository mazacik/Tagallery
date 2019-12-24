package main;

import baseobject.CustomList;
import baseobject.Project;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.Tag;
import baseobject.tag.TagList;
import control.reload.ChangeIn;
import gui.main.center.GalleryTile;
import gui.stage.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import tools.CacheManager;
import tools.CollectionUtil;
import tools.FileUtil;

import java.io.File;
import java.util.Comparator;
import java.util.logging.Logger;

public class Main extends Application implements InstanceCollector {
	private static final boolean QUICKSTART = false;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		initLogger();
		initInstances();
		initLoading();
	}
	
	private static void initLogger() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
	}
	private static void initGraphics() {
	
	}
	private static void initInstances() {
		settings.readFromDisk();
		
		toolbarPane.init();     /* needs Settings */
		galleryPane.init();     /* needs Settings */
		entityPane.init();      /* needs Settings, GalleryPane */
		filterPane.init();      /* needs Settings */
		selectPane.init();      /* needs Settings */
		
		GalleryTile.init();
		
		filter.init();
		target.init();
		select.init();
		reload.init();          /* needs everything */
	}
	private static void initLoading() {
		CustomList<Project> projects = FileUtil.getProjects();
		
		if (!QUICKSTART || projects.isEmpty()) {
			StageManager.getStageMain().layoutIntro();
		} else {
			StageManager.getStageMain().layoutMain();
			
			projects.sort(Project.getComparator());
			startDatabaseLoading(projects.getFirst());
		}
	}
	
	public static void startDatabaseLoading(Project project) {
		Project.setCurrent(project);
		entityListMain.addAll(project.getEntityList());
		checkForNewFiles(FileUtil.getSupportedFiles(new File(project.getDirectorySource())));
		
		CollectionUtil.init();
		entityListMain.sort();
		initTags(project);
		filter.refresh();
		target.set(filter.get(0));
		select.set(filter.get(0));
		
		reload.doReload();
		
		filterPane.collapseAll();
		selectPane.collapseAll();
		
		CacheManager.createCacheInBackground(entityListMain);
	}
	public static void initTags(Project project) {
		//todo help
		TagList allTags = project.getTagList();
		if (allTags != null) {
			tagListMain.addAll(allTags);
		}
		
		for (Entity entity : entityListMain) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (tagListMain.containsEqualTo(tag)) {
					tagList.set(tagList.indexOf(tag), tagListMain.getTag(tag));
				} else {
					tagListMain.add(tag);
				}
			}
		}
		
		tagListMain.sort();
		reload.notify(ChangeIn.TAG_LIST_MAIN);
	}
	private static void checkForNewFiles(CustomList<File> fileList) {
		entityListMain.sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		EntityList orphanEntities = new EntityList(entityListMain);
		CustomList<File> newFiles = new CustomList<>(fileList);
		
		/* compare files in the source directory with known objects in the database */
		for (Entity entity : entityListMain) {
			for (int i = 0; i < newFiles.size(); i++) {
				File file = newFiles.get(i);
				if (entity.getName().equals(FileUtil.createEntityName(file))) {
					orphanEntities.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File newFile : new CustomList<>(newFiles)) {
			for (Entity orphanEntity : new CustomList<>(orphanEntities)) {
				if (newFile.length() == orphanEntity.getLength()) {
					newFiles.remove(newFile);
					orphanEntities.remove(orphanEntity);
					
					/* rename the object and cache file */
					File oldCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					orphanEntity.setName(newFile.getName());
					File newCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					
					if (oldCacheFile.exists() && !newCacheFile.exists()) {
						oldCacheFile.renameTo(newCacheFile);
					}
				}
			}
		}
		
		/* add unrecognized objects */
		for (File file : newFiles) {
			Entity entity = new Entity(file);
			entityListMain.add(entity);
			filter.getNewEntities().add(entity);
		}
		
		/* discard orphan objects */
		for (Entity entity : orphanEntities) {
			entityListMain.remove(entity);
		}
		
		entityListMain.sort(Comparator.comparing(Entity::getName));
	}
	public static void exitApplication() {
		Logger.getGlobal().info("Application Exit");
		
		CacheManager.stopThread();
		
		entityPane.disposeVideoPlayer();
		entityPane.getControls().hide();
		
		Project.getCurrent().writeToDisk();
		settings.writeToDisk();
	}
}
