package project.control;

import project.database.DataElementDatabase;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;
import project.gui.GUIStage;
import project.gui.component.part.GalleryTile;

import java.util.ArrayList;
import java.util.Random;

public abstract class SelectionControl {
    /* change */
    private static final ArrayList<ChangeEventListener> changeListeners = new ArrayList<>();
    public static ArrayList<ChangeEventListener> getChangeListeners() {
        return changeListeners;
    }

    /* vars */
    private static final ArrayList<DataElement> dataElements = new ArrayList<>();

    /* public */
    public static void addDataElement(DataElement dataElement) {
        if (dataElement != null && !dataElements.contains(dataElement)) {
            dataElements.add(dataElement);
            GalleryTile.generateEffect(dataElement);
            ChangeEventControl.notifyListeners(ChangeEventEnum.SELECTION);
        }
    }
    public static void addDataElement(ArrayList<DataElement> dataElementsToAdd) {
        if (dataElementsToAdd != null) {
            for (DataElement dataElement : dataElementsToAdd) {
                if (!dataElements.contains(dataElement)) {
                    dataElements.add(dataElement);
                    GalleryTile.generateEffect(dataElement);
                }
            }
            ChangeEventControl.notifyListeners(ChangeEventEnum.SELECTION);
        }
    }
    public static void removeDataElement(DataElement dataElement) {
        if (dataElement != null && dataElements.contains(dataElement)) {
            dataElements.remove(dataElement);
            GalleryTile.generateEffect(dataElement);
            ChangeEventControl.notifyListeners(ChangeEventEnum.SELECTION);
        }
    }
    public static void setDataElement(DataElement dataElement) {
        dataElements.clear();
        addDataElement(dataElement);
    }
    public static void clearDataElements() {
        ArrayList<DataElement> dataElements = DataElementDatabase.getDataElements();
        for (DataElement dataElement : dataElements) {
            DataElement currentFocus = FocusControl.getCurrentFocus();
            if (!dataElement.equals(currentFocus)) {
                dataElement.getGalleryTile().setEffect(null);
            } else {
                GalleryTile.generateEffect(dataElement);
            }
        }
        ChangeEventControl.notifyListeners(ChangeEventEnum.SELECTION);
    }
    public static void setRandomValidDataElement() {
        ArrayList<DataElement> dataElementsFiltered = FilterControl.getValidDataElements();
        int databaseItemsFilteredSize = dataElementsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        SelectionControl.setDataElement(dataElementsFiltered.get(randomIndex));

        GUIStage.getPaneGallery().adjustViewportToFocus();
    }
    public static void swapSelectionStateOf(DataElement dataElement) {
        if (dataElement != null) {
            if (!dataElements.contains(dataElement)) {
                addDataElement(dataElement);
            } else {
                removeDataElement(dataElement);
            }
        }
    }
    public static ArrayList<TagElement> getIntersectingTags() {
        if (isSelectionEmpty()) return new ArrayList<>();

        ArrayList<TagElement> sharedTags = new ArrayList<>();
        ArrayList<TagElement> firstItemTags = dataElements.get(0).getTagElements();
        DataElement lastItemInSelection = dataElements.get(dataElements.size() - 1);

        for (TagElement tagElement : firstItemTags) {
            for (DataElement dataElement : dataElements) {
                if (dataElement.getTagElements().contains(tagElement)) {
                    if (dataElement.equals(lastItemInSelection)) {
                        sharedTags.add(tagElement);
                    }
                } else break;
            }
        }
        return sharedTags;
    }

    /* boolean */
    public static boolean isSelectionEmpty() {
        return dataElements.isEmpty();
    }

    /* get */
    public static ArrayList<DataElement> getDataElements() {
        return dataElements;
    }
}
