package base.entity;

import base.CustomList;
import control.Select;
import control.reload.Notifier;
import control.reload.Reload;

import java.util.Random;

public abstract class EntityCollectionUtil {
	public static void create() {
		int collectionID = new Random().nextInt();
		EntityList collection = new EntityList(Select.getEntities());
		for (Entity entity : Select.getEntities()) {
			entity.setCollectionID(collectionID);
			entity.setCollection(collection);
			entity.getTile().updateCollectionIcon();
		}
		
		Select.setTarget(collection.getFirst());
		
		Reload.notify(Notifier.ENTITY_LIST_MAIN);
	}
	public static void discard() {
		for (Entity entity : Select.getEntities().getFirst().getCollection()) {
			entity.setCollectionID(0);
			entity.setCollection(null);
			entity.getTile().setEffect(null);
		}
		
		//todo ping SELECT_COLLECTION (or something) to refresh ToolbarPane
		Reload.notify(Notifier.ENTITY_LIST_MAIN);
	}
	
	public static boolean isCollection(EntityList entityList) {
		int collectionID = entityList.getFirst().getCollectionID();
		if (collectionID == 0) return false;
		for (Entity entity : entityList) {
			if (entity.getCollectionID() != collectionID) {
				return false;
			}
		}
		return true;
	}
	public static boolean hasOpenOrNoCollection(Entity entity) {
		return entity.getCollectionID() == 0 || openCollections.contains(entity.getCollectionID());
	}
	
	private static CustomList<Integer> openCollections = new CustomList<>();
	public static void toggleCollection(Entity entity) {
		int collectionID = entity.getCollectionID();
		
		if (collectionID != 0) {
			if (openCollections.contains(collectionID)) {
				openCollections.remove((Integer) collectionID);
			} else {
				openCollections.add(collectionID);
			}
			
			for (Entity _entity : entity.getCollection()) {
				_entity.getTile().updateCollectionIcon();
			}
			
			Reload.notify(Notifier.ENTITY_LIST_MAIN);
		}
	}
	public static CustomList<Integer> getOpenCollections() {
		return openCollections;
	}
}
