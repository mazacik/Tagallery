package project.frontend.common;

import javafx.stage.DirectoryChooser;
import project.Main;

import java.io.File;

public class DirectoryChooserWindow {
    private DirectoryChooser directoryChooser = new DirectoryChooser();


    public DirectoryChooserWindow(String title, String initialDirectory) {
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(new File(initialDirectory));
    }

    public String getResultValue() {
        File selectedDirectory = directoryChooser.showDialog(Main.getMainStage());
        return selectedDirectory.getAbsolutePath();
    }
}
