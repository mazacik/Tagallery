package cache;

import base.entity.Entity;
import base.entity.EntityList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.Root;
import misc.FileUtil;
import misc.Settings;
import thread.Thread;
import ui.main.display.VideoPlayer;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public abstract class CacheUtil {
	private static Image placeholder = new WritableImage(Settings.GALLERY_TILE_SIZE.getValueInteger(), Settings.GALLERY_TILE_SIZE.getValueInteger()) {{
		Label label = new Label("Placeholder");
		label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
		label.setWrapText(true);
		label.setFont(new Font(26));
		label.setAlignment(Pos.CENTER);
		
		int size = Settings.GALLERY_TILE_SIZE.getValueInteger();
		label.setMinWidth(size);
		label.setMinHeight(size);
		label.setMaxWidth(size);
		label.setMaxHeight(size);
		
		Scene scene = new Scene(new Group(label));
		scene.setFill(Color.GRAY);
		scene.snapshot(this);
	}};
	
	public static Image getCache(Entity entity) {
		return getCache(entity, false);
	}
	public static Image getCache(Entity entity, boolean recreate) {
		File cacheFile = new File(FileUtil.getFileCache(entity));
		Image image;
		
		if (recreate || !cacheFile.exists()) {
			image = createCache(entity);
		} else {
			image = new Image("file:" + cacheFile.getAbsolutePath());
		}
		
		return (image == null) ? placeholder : image;
	}
	
	public static Image createCache(Entity entity) {
		//String entityIndex = StringUtils.right("00000000" + (EntityList.getMain().indexOf(entity) + 1), String.valueOf(EntityList.getMain().size()).length());
		//Logger.getGlobal().info(String.format("[%s/%s] %s", entityIndex, EntityList.getMain().size(), entity.getName()));
		
		switch (entity.getMediaType()) {
			case IMAGE:
				return createFromImage(entity);
			case GIF:
				return createFromGif(entity);
			case VIDEO:
				return createFromVideo(entity);
			default:
				return null;
		}
	}
	private static Image createFromImage(Entity entity) {
		int thumbSize = Settings.GALLERY_TILE_SIZE.getValueInteger();
		Image image = new Image("file:" + FileUtil.getFileEntity(entity), thumbSize, thumbSize, false, false);
		BufferedImage buffer = SwingFXUtils.fromFXImage(image, null);
		
		if (buffer != null) {
			try {
				File cacheFile = new File(FileUtil.getFileCache(entity));
				cacheFile.getParentFile().mkdirs();
				ImageIO.write(buffer, "jpg", cacheFile);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return image;
	}
	private static Image createFromGif(Entity entity) {
		GifDecoder gifDecoder = new GifDecoder();
		gifDecoder.read(FileUtil.getFileEntity(entity));
		int thumbSize = Settings.GALLERY_TILE_SIZE.getValueInteger();
		
		java.awt.Image frame = gifDecoder.getFrame(gifDecoder.getFrameCount() / 2).getScaledInstance(thumbSize, thumbSize, java.awt.Image.SCALE_FAST);
		BufferedImage buffer = new BufferedImage(thumbSize, thumbSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = buffer.createGraphics();
		graphics.setBackground(java.awt.Color.BLACK);
		graphics.clearRect(0, 0, thumbSize, thumbSize);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.drawImage(frame, 0, 0, thumbSize, thumbSize, null);
		
		try {
			File cacheFile = new File(FileUtil.getFileCache(entity));
			cacheFile.getParentFile().mkdirs();
			ImageIO.write(buffer, "jpg", cacheFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return SwingFXUtils.toFXImage(buffer, null);
	}
	private static Image createFromVideo(Entity entity) {
		if (VideoPlayer.hasLibs()) {
			String[] vlcArgs = {
					"--intf", "dummy",          /* no interface */
					"--vout", "dummy",          /* no video (output) */
					"--no-audio",               /* no audio (decoding) */
					"--no-snapshot-preview",    /* no blending in dummy vout */
			};
			
			float mediaPosition = 0.5f;         /* approximate video position to take the snapshot from */
			
			MediaPlayerFactory factory = new MediaPlayerFactory(vlcArgs);
			MediaPlayer mediaPlayer = factory.mediaPlayers().newMediaPlayer();
			
			final CountDownLatch inPositionLatch = new CountDownLatch(1);
			final CountDownLatch snapshotTakenLatch = new CountDownLatch(1);
			
			mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
				@Override
				public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
					if (newPosition >= mediaPosition * 0.9f) { /* 10% error margin, don't set it lower */
						inPositionLatch.countDown();
					}
				}
				
				@Override
				public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
					snapshotTakenLatch.countDown();
				}
			});
			
			File cacheFile = new File(FileUtil.getFileCache(entity));
			cacheFile.getParentFile().mkdirs();
			
			if (mediaPlayer.media().start(FileUtil.getFileEntity(entity))) {
				try {
					entity.setMediaDuration(mediaPlayer.media().info().duration());
					mediaPlayer.controls().setPosition(mediaPosition);
					inPositionLatch.await(); // might wait forever if error
					
					int thumbSize = Settings.GALLERY_TILE_SIZE.getValueInteger();
					mediaPlayer.snapshots().save(cacheFile, thumbSize, thumbSize);
					snapshotTakenLatch.await(); // might wait forever if error
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				mediaPlayer.controls().stop();
			}
			
			mediaPlayer.release();
			factory.release();
			
			if (cacheFile.exists()) {
				return new Image("file:" + cacheFile.getAbsolutePath());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static void reset() {
		EntityList.getMain().forEach(entity -> entity.getTile().setImage(null));
		CacheUtil.loadCache(EntityList.getMain(), true);
	}
	
	private static Thread thread = null;
	public static void loadCache(EntityList entityList) {
		loadCache(entityList, false);
	}
	public static void loadCache(EntityList entityList, boolean recreate) {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
		
		thread = new Thread(() -> {
			Root.MAIN_STAGE.getMainScene().showLoadingBar(entityList, entityList.size());
			
			for (Entity entity : new EntityList(entityList)) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				
				entity.getTile().setImage(CacheUtil.getCache(entity, recreate));
				Root.MAIN_STAGE.getMainScene().advanceLoadingBar(entityList);
			}
			
			Root.MAIN_STAGE.getMainScene().hideLoadingBar(entityList);
		});
		
		thread.start();
	}
}