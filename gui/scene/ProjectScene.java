package application.gui.scene;

import application.database.loader.Project;
import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.main.LifeCycle;
import application.misc.FileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProjectScene {
	private final Scene scene;
	
	public ProjectScene() {
		scene = create();
	}
	
	private Scene create() {
		TextNode lblProjectName = new TextNode("Project Name:", ColorType.DEF, ColorType.DEF);
		EditNode edtProjectName = new EditNode("Project1");
		TextNode lblProjectDirectory = new TextNode("Project Directory:", ColorType.DEF, ColorType.DEF);
		EditNode edtProjectDirectory = new EditNode("");
		TextNode btnProjectDirectory = new TextNode("...", ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.ALT);
		TextNode lblWorkingDirectory = new TextNode("Working Directory:", ColorType.DEF, ColorType.DEF);
		EditNode edtWorkingDirectory = new EditNode("");
		TextNode btnWorkingDirectory = new TextNode("...", ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.ALT);
		
		double width = SizeUtil.getStringWidth(lblWorkingDirectory.getText()) + lblWorkingDirectory.getPadding().getLeft() + lblWorkingDirectory.getPadding().getRight();
		lblProjectName.setPrefWidth(width);
		lblProjectDirectory.setPrefWidth(width);
		lblWorkingDirectory.setPrefWidth(width);
		
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
		lblWorkingDirectory.setAlignment(Pos.CENTER_LEFT);
		
		edtProjectName.setPrefWidth(300);
		edtProjectDirectory.setPrefWidth(300);
		edtWorkingDirectory.setPrefWidth(300);
		
		HBox hBoxProjectName = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblProjectName, edtProjectName);
		HBox hBoxProjectDirectory = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblProjectDirectory, edtProjectDirectory, btnProjectDirectory);
		HBox hBoxWorkingDirectory = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblWorkingDirectory, edtWorkingDirectory, btnWorkingDirectory);
		
		hBoxProjectName.setSpacing(5 * SizeUtil.getGlobalSpacing());
		hBoxProjectDirectory.setSpacing(5 * SizeUtil.getGlobalSpacing());
		hBoxWorkingDirectory.setSpacing(5 * SizeUtil.getGlobalSpacing());
		
		TextNode btnCreateProject = new TextNode("Create Project", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		TextNode btnCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String projectDirectory = FileUtil.directoryChooser(scene);
				if (!projectDirectory.isEmpty()) {
					edtProjectDirectory.setText(projectDirectory);
				}
			}
		});
		btnWorkingDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtWorkingDirectory.setText(FileUtil.directoryChooser(scene));
			}
		});
		
		btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			//checkValues values, if invalid, show error
			createProject(edtProjectName.getText(), edtProjectDirectory.getText(), edtWorkingDirectory.getText());
		});
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> SceneUtil.showIntroScene());
		
		HBox hBoxCreateCancel = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, btnCancel, btnCreateProject);
		
		VBox vBoxMain = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF,
				hBoxProjectName,
				hBoxProjectDirectory,
				hBoxWorkingDirectory,
				hBoxCreateCancel
		);
		vBoxMain.setSpacing(5 * SizeUtil.getGlobalSpacing());
		vBoxMain.setPadding(new Insets(15 * SizeUtil.getGlobalSpacing()));
		
		Scene projectScene = new Scene(vBoxMain);
		projectScene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				createProject(edtProjectName.getText(), edtProjectDirectory.getText(), edtWorkingDirectory.getText());
			}
		});
		
		return projectScene;
	}
	
	private void createProject(String projectName, String projectDirectory, String workingDirectory) {
		String projectFile = projectDirectory + projectName + ".json";
		Project project = new Project(projectFile, workingDirectory);
		project.writeToDisk();
		Instances.getSettings().getRecentProjects().push(projectFile);
		Instances.getSettings().writeToDisk();
		LifeCycle.startLoading(project);
	}
	
	public void show() {
		Instances.getMainStage().setScene(scene);
		Decorator.applyStyle();
	}
}