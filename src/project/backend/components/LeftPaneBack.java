package project.backend.components;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.backend.common.Filter;
import project.backend.Backend;
import project.backend.database.Database;
import project.frontend.shared.ColoredText;
import project.frontend.shared.Frontend;

import java.util.List;

/**
 * Backend of a GUI component which displays all tags found across all items in the working directory.
 */
public class LeftPaneBack {
    private final List<String> whitelist = Database.getDatabaseTagsWhitelist();
    private final List<String> blacklist = Database.getDatabaseTagsBlacklist();
    private final ContextMenu listContextMenu = new ContextMenu();
    private ListView<ColoredText> listView = Frontend.getLeftPane().getListView();

    /**
     * Initialization of the component.
     */
    public LeftPaneBack() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        buildContextMenu();
        listView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY))
                if (listView.getSelectionModel().getSelectedItem() != null) {
                    String tag = listView.getSelectionModel().getSelectedItem().getText();
                    if (whitelist.contains(tag)) {
                        whitelist.remove(tag);
                        blacklist.add(tag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.RED));
                    } else if (blacklist.contains(tag)) {
                        blacklist.remove(tag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.BLACK));
                    } else {
                        whitelist.add(tag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.GREEN));
                    }
                    Filter.filterByTags();
                    Backend.getGalleryPane().reloadContent();
                }
        });
    }

    /**
     * Initialization of the right-click context menu of the component.
     */
    private void buildContextMenu() {
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> Filter.renameTag(listView.getSelectionModel().getSelectedItem().getText()));
        listContextMenu.getItems().addAll(menuRename);
    }

    /**
     * Reloads all tags found across all items in the working directory.
     */
    public static void reloadContent() {
        listView.getItems().clear();
        for (String tag : Database.getDatabaseTags()) {
            if (Database.getDatabaseTagsWhitelist().contains(tag)) listView.getItems().add(new ColoredText(tag, Color.GREEN));
            else if (Database.getDatabaseTagsBlacklist().contains(tag)) listView.getItems().add(new ColoredText(tag, Color.RED));
            else listView.getItems().add(new ColoredText(tag, Color.BLACK));
        }
    }
}
