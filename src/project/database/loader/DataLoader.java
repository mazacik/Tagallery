package project.database.loader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.Main;
import project.database.control.DataObjectControl;
import project.database.control.TagElementControl;
import project.database.element.DataObject;
import project.gui.GUIInstance;
import project.gui.component.gallerypane.GalleryTile;
import project.gui.custom.specific.LoadingWindow;
import project.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DataLoader extends Thread {
    /* imports */
    private final double GALLERY_ICON_MAX_SIZE = Settings.getGalleryIconSizeMax();

    private final String PATH_MAINDIR = Settings.getMainDirectoryPath();
    private final String PATH_IMAGECACHE = Settings.getImageCacheDirectoryPath();
    private final String PATH_DATABASECACHE = Settings.getDatabaseCacheFilePath();

    private final ArrayList<DataObject> dataElementsLive = DataObjectControl.getDataElementsLive();

    /* vars */
    private int fileCount = 0;
    private int currentElementIndex = 0;
    private ArrayList<File> validFiles;

    @Override
    public void run() {
        initialization();
        deserialization();
        validation();
        finalization();
        TagElementControl.initialize();
    }

    /* private */
    private void initialization() {
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(PATH_MAINDIR).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        File imageCacheDirectory = new File(PATH_IMAGECACHE);
        if (!imageCacheDirectory.exists()) {
            imageCacheDirectory.mkdir();
        }
    }
    private void deserialization() {
        if (!new File(PATH_DATABASECACHE).exists() || !DataObjectControl.addAll(Serialization.readFromDisk())) {
            createDatabaseCache();
        }
    }
    private void validation() {
        ArrayList<String> dataElementsItemNames = new ArrayList<>();
        ArrayList<String> validFilesItemNames = new ArrayList<>();

        for (DataObject dataObject : dataElementsLive) {
            dataElementsItemNames.add(dataObject.getName());
        }
        for (File file : validFiles) {
            validFilesItemNames.add(file.getName());
        }

        dataElementsItemNames.sort(Comparator.naturalOrder());
        validFilesItemNames.sort(Comparator.naturalOrder());

        if (!dataElementsItemNames.equals(validFilesItemNames)) {
            validateDatabaseCache(dataElementsItemNames, validFilesItemNames);
        }
    }
    private void finalization() {
        for (DataObject dataObject : dataElementsLive) {
            dataObject.setImage(getImageFromDataElement(dataObject));
            dataObject.setGalleryTile(new GalleryTile(dataObject));
        }

        Platform.runLater(() -> {
            Main.getLoadingWindow().close();
            GUIInstance.initialize();
            Main.setStage(GUIInstance.getInstance());
        });
    }

    private void createDatabaseCache() {
        for (File file : validFiles) {
            DataObject dataObject = createDataElementFromFile(file);
            DataObjectControl.add(dataObject);
        }

        Serialization.writeToDisk();
    }
    private void validateDatabaseCache(ArrayList<String> dataElementsItemNames, ArrayList<String> validFilesItemNames) {
        /* add unrecognized items */
        for (File file : validFiles) {
            if (!dataElementsItemNames.contains(file.getName())) {
                dataElementsLive.add(createDataElementFromFile(file));
            }
        }

        /* remove missing items */
        ArrayList<DataObject> temporaryList = new ArrayList<>(dataElementsLive);
        for (DataObject dataObject : dataElementsLive) {
            if (!validFilesItemNames.contains(dataObject.getName())) {
                temporaryList.remove(dataObject);
            }
        }

        dataElementsLive.clear();
        dataElementsLive.addAll(temporaryList);
        Serialization.writeToDisk();
    }

    private Image getImageFromDataElement(DataObject dataObject) {
        Image currentElementImage = null;
        currentElementIndex++;

        String currentElementName = dataObject.getName();
        String currentElementCachePath = PATH_IMAGECACHE + "/" + currentElementName;
        File currentElementCacheFile = new File(currentElementCachePath);

        /* write image cache to disk if not present */
        if (!currentElementCacheFile.exists()) {
            try {
                currentElementCacheFile.createNewFile();
                String currentElementFilePath = "file:" + PATH_MAINDIR + "/" + currentElementName;
                currentElementImage = new Image(currentElementFilePath, GALLERY_ICON_MAX_SIZE, GALLERY_ICON_MAX_SIZE, false, true);
                String currentElementExtension = FilenameUtils.getExtension(currentElementName);
                BufferedImage currentElementBufferedImage = SwingFXUtils.fromFXImage(currentElementImage, null);
                ImageIO.write(currentElementBufferedImage, currentElementExtension, currentElementCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* update loading window label */
        if (Main.getLoadingWindow().getClass().equals(LoadingWindow.class)) {
            Platform.runLater(() -> Main.getLoadingWindow().getProgressLabel().setText("Loading item " + currentElementIndex + " of " + fileCount + ", " + currentElementIndex * 100 / fileCount + "% done"));
        }

        return Objects.requireNonNullElseGet(currentElementImage, () -> new Image("file:" + currentElementCachePath, GALLERY_ICON_MAX_SIZE, GALLERY_ICON_MAX_SIZE, false, true));
    }
    private DataObject createDataElementFromFile(File file) {
        return new DataObject(file.getName(), new ArrayList<>());
    }
}
