package project.frontend.singleton;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import project.frontend.Frontend;

@SuppressWarnings("FieldCanBeLocal")
public class TopPaneFront extends BorderPane {
    private static TopPaneFront instance = new TopPaneFront();

    private final Menu infoLabel = new Menu();

    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuRefresh = new MenuItem("Refresh");
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("Selection");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Selection");

    private final Menu menuFilter = new Menu("Filter");
    private final MenuItem menuUntaggedOnly = new MenuItem("Untagged");
    private final MenuItem menuLessThanXTags = new MenuItem("Less Than X Tags");
    private final MenuItem menuReset = new MenuItem("Reset");


    private TopPaneFront() {
        menuFile.getItems().addAll(menuSave, menuRefresh, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuLessThanXTags, new SeparatorMenuItem(), menuReset);

        MenuBar mainArea = new MenuBar();
        mainArea.getMenus().addAll(menuFile, menuSelection, menuFilter);
        setCenter(mainArea);

        MenuBar infoLabelArea = new MenuBar();
        infoLabelArea.getMenus().add(infoLabel);
        infoLabelArea.setOnMouseEntered(event -> Frontend.swapImageDisplayMode());
        infoLabelArea.setOnMouseExited(event -> Frontend.swapImageDisplayMode());
        setRight(infoLabelArea);
    }

    public MenuItem getMenuSave() {
        return menuSave;
    }

    public MenuItem getMenuRefresh() {
        return menuRefresh;
    }

    public MenuItem getMenuExit() {
        return menuExit;
    }

    public MenuItem getMenuSelectAll() {
        return menuSelectAll;
    }

    public MenuItem getMenuClearSelection() {
        return menuClearSelection;
    }

    public MenuItem getMenuUntaggedOnly() {
        return menuUntaggedOnly;
    }

    public MenuItem getMenuLessThanXTags() {
        return menuLessThanXTags;
    }

    public MenuItem getMenuReset() {
        return menuReset;
    }

    public Menu getInfoLabel() {
        return infoLabel;
    }

    public static TopPaneFront getInstance() {
        return instance;
    }
}
