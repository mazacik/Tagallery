package main;

import control.*;
import database.list.DataObjectListMain;
import database.list.TagListMain;
import javafx.stage.Stage;
import settings.Settings;
import userinterface.main.center.GalleryPane;
import userinterface.main.center.MediaPane;
import userinterface.main.center.MediaViewEvent;
import userinterface.main.center.TileViewEvent;
import userinterface.main.side.FilterPane;
import userinterface.main.side.FilterPaneEvent;
import userinterface.main.side.SelectPane;
import userinterface.main.side.SelectPaneEvent;
import userinterface.main.top.ToolbarPane;
import userinterface.main.top.TopMenuEvent;
import userinterface.nodes.menu.ClickMenuData;
import userinterface.nodes.menu.ClickMenuTag;
import userinterface.scene.MainStageEvent;

public abstract class InstanceManager {
    private static Logger logger;
    private static Settings settings;

    private static Filter filter;
    private static Target target;
    private static Select select;
    private static Reload reload;

    private static TagListMain tagListMain;
	private static DataObjectListMain objectListMain;

    private static Stage mainStage;
    private static MediaPane mediaPane;
    private static FilterPane filterPane;
    private static SelectPane selectPane;
    private static ToolbarPane toolbarPane;
    private static GalleryPane galleryPane;

    private static ClickMenuData clickMenuData;
	private static ClickMenuTag clickMenuTag;

    private static MainStageEvent mainStageEvent;
    private static TopMenuEvent topMenuEvent;
	private static FilterPaneEvent filterPaneEvent;
	private static SelectPaneEvent selectPaneEvent;
    private static TileViewEvent tileViewEvent;
    private static MediaViewEvent mediaViewEvent;


    public static void createInstances() {
        createInstancesSystem();

        createInstancesDatabase();

        createInstancesFrontend();

        createInstancesBackend();
    }

    private static void createInstancesSystem() {
        logger = new Logger();
		settings = Settings.readFromDisk();
		settings.checkValues();
    }
    private static void createInstancesDatabase() {
		objectListMain = new DataObjectListMain();
        tagListMain = new TagListMain();
    }
    private static void createInstancesFrontend() {
        mainStage = new Stage();

        toolbarPane = new ToolbarPane();    /* needs Settings */
        galleryPane = new GalleryPane();    /* needs Settings */
        mediaPane = new MediaPane();        /* needs Settings, GalleryPane */
        filterPane = new FilterPane();      /* needs Settings */
        selectPane = new SelectPane();      /* needs Settings */

        clickMenuData = new ClickMenuData();
		clickMenuTag = new ClickMenuTag();
    }
    private static void createInstancesBackend() {
        filter = new Filter();
        target = new Target();
        select = new Select();
        reload = new Reload();  /* needs Frontend */
    }
    public static void createInstancesEvents() {
        mainStageEvent = new MainStageEvent();
        topMenuEvent = new TopMenuEvent();
		filterPaneEvent = new FilterPaneEvent();
		selectPaneEvent = new SelectPaneEvent();
        tileViewEvent = new TileViewEvent();
        mediaViewEvent = new MediaViewEvent();
    }

    public static Logger getLogger() {
        return logger;
    }
    public static Settings getSettings() {
        return settings;
    }
    public static Filter getFilter() {
        return filter;
    }
    public static Target getTarget() {
        return target;
    }
    public static Select getSelect() {
        return select;
    }
    public static Reload getReload() {
        return reload;
    }
	public static DataObjectListMain getObjectListMain() {
        return objectListMain;
    }
    public static TagListMain getTagListMain() {
        return tagListMain;
    }
    public static Stage getMainStage() {
        return mainStage;
    }
    public static ToolbarPane getToolbarPane() {
        return toolbarPane;
    }
    public static GalleryPane getGalleryPane() {
        return galleryPane;
    }
    public static MediaPane getMediaPane() {
        return mediaPane;
    }
    public static FilterPane getFilterPane() {
        return filterPane;
    }
    public static SelectPane getSelectPane() {
        return selectPane;
    }
    public static ClickMenuData getClickMenuData() {
        return clickMenuData;
    }
	public static ClickMenuTag getClickMenuTag() {
		return clickMenuTag;
    }

    public static MainStageEvent getMainStageEvent() {
        return mainStageEvent;
    }
    public static TopMenuEvent getTopMenuEvent() {
        return topMenuEvent;
    }
	public static FilterPaneEvent getFilterPaneEvent() {
		return filterPaneEvent;
    }
	public static SelectPaneEvent getSelectPaneEvent() {
		return selectPaneEvent;
    }
    public static TileViewEvent getTileViewEvent() {
        return tileViewEvent;
    }
    public static MediaViewEvent getMediaViewEvent() {
        return mediaViewEvent;
    }
}
