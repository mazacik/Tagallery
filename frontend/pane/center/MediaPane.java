package application.frontend.pane.center;

import application.backend.base.entity.Entity;
import application.backend.util.FileUtil;
import application.frontend.component.ClickMenu;
import application.frontend.pane.PaneInterface;
import application.frontend.stage.StageManager;
import application.main.InstanceCollector;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MediaPane extends BorderPane implements PaneInterface, InstanceCollector {
	private Canvas canvas;
	private ImageView gifPlayer;
	private VideoPlayer videoPlayer;
	private MediaPaneControls controls;
	
	private Image currentImage = null;
	private Entity currentCache = null;
	
	private Image placeholder = null;
	
	private boolean needsReload;
	
	public MediaPane() {
	
	}
	
	public void init() {
		needsReload = false;
		
		canvas = new Canvas();
		gifPlayer = new ImageView();
		videoPlayer = VideoPlayer.create(canvas);
		controls = new MediaPaneControls(this, videoPlayer);
		
		gifPlayer.fitWidthProperty().bind(galleryPane.widthProperty());
		gifPlayer.fitHeightProperty().bind(galleryPane.heightProperty());
		
		this.setCenter(canvas);
		
		initEvents();
	}
	
	public boolean reload() {
		Entity currentTarget = target.get();
		if (StageManager.getMainStage().isFullView() && currentTarget != null) {
			switch (FileUtil.getFileType(currentTarget)) {
				case IMAGE:
					reloadAsImage(currentTarget);
					break;
				case GIF:
					reloadAsGif(currentTarget);
					break;
				case VIDEO:
					reloadAsVideo(currentTarget);
					break;
			}
			return true;
		}
		
		return false;
	}
	
	private void reloadAsImage(Entity currentTarget) {
		controls.setVideoMode(false);
		
		if (this.getCenter() != canvas) this.setCenter(canvas);
		
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			currentCache = currentTarget;
			currentImage = new Image("file:" + currentCache.getPath());
		}
		
		double imageWidth = currentImage.getWidth();
		double imageHeight = currentImage.getHeight();
		double maxWidth = canvas.getWidth();
		double maxHeight = canvas.getHeight();
		
		boolean upScale = true;
		
		double resultWidth;
		double resultHeight;
		
		//noinspection ConstantConditions
		if (!upScale && imageWidth < maxWidth && imageHeight < maxHeight) {
			// image is smaller than canvas, upscaling is off
			resultWidth = imageWidth;
			resultHeight = imageHeight;
		} else {
			// scale image to fit width
			resultWidth = maxWidth;
			resultHeight = imageHeight * maxWidth / imageWidth;
			
			// if scaled image is too tall, scale to fit height instead
			if (resultHeight > maxHeight) {
				resultHeight = maxHeight;
				resultWidth = imageWidth * maxHeight / imageHeight;
			}
		}
		
		double resultX = maxWidth / 2 - resultWidth / 2;
		double resultY = maxHeight / 2 - resultHeight / 2;
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.drawImage(currentImage, resultX, resultY, resultWidth, resultHeight);
	}
	private void reloadAsGif(Entity currentTarget) {
		controls.setVideoMode(false);
		
		if (this.getCenter() != gifPlayer) this.setCenter(gifPlayer);
		
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			currentCache = currentTarget;
			currentImage = new Image("file:" + currentCache.getPath());
		}
		
		gifPlayer.setImage(currentImage);
	}
	private void reloadAsVideo(Entity currentTarget) {
		if (this.getCenter() != canvas) this.setCenter(canvas);
		
		if (VideoPlayer.hasLibs()) {
			controls.setVideoMode(true);
			
			if (currentCache == null || !currentCache.equals(currentTarget)) {
				currentCache = currentTarget;
				videoPlayer.start(currentTarget.getPath());
			} else {
				videoPlayer.resume();
			}
		} else {
			controls.setVideoMode(false);
			
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			if (placeholder == null) placeholder = createPlaceholder();
			gc.drawImage(placeholder, 0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}
	
	private Image createPlaceholder() {
		Label label = new Label("Placeholder");
		label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
		label.setWrapText(true);
		label.setFont(new Font(100));
		label.setAlignment(Pos.CENTER);
		
		int width = (int) canvas.getWidth();
		int height = (int) canvas.getHeight();
		
		label.setMinWidth(width);
		label.setMinHeight(height);
		label.setMaxWidth(width);
		label.setMaxHeight(height);
		
		WritableImage img = new WritableImage(width, height);
		Scene scene = new Scene(new Group(label));
		scene.setFill(Color.GRAY);
		scene.snapshot(img);
		return img;
	}
	
	private void initEvents() {
		canvas.widthProperty().bind(galleryPane.widthProperty());
		canvas.heightProperty().bind(galleryPane.heightProperty());
		
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> reload());
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> reload());
		
		ClickMenu.install(this, MouseButton.SECONDARY, ClickMenu.StaticInstance.DATA);
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() % 2 != 0) {
					requestFocus();
				} else {
					StageManager.getMainStage().swapViewMode();
					reload.doReload();
				}
				ClickMenu.hideAll();
			}
		});
	}
	
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
	
	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
	public MediaPaneControls getControls() {
		return controls;
	}
}