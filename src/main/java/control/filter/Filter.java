package control.filter;

import base.entity.Entity;
import base.entity.EntityList;
import base.tag.TagList;
import control.reload.Notifier;
import control.reload.Reload;
import enums.MediaType;

public class Filter extends EntityList {
	private static final FilterSettings settings = new FilterSettings();
	private static final FilterListManager listManager = new FilterListManager();
	private static final EntityList newEntities = new EntityList();
	
	public static void reset() {
		listManager.getWhitelist().clear();
		listManager.getBlacklist().clear();
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public static void refresh() {
		getEntities().clear();
		
		for (Entity entity : EntityList.getMain()) {
			if (entity.getMediaType() == MediaType.IMAGE) {
				if (!settings.isShowImages()) {
					continue;
				}
			} else if (entity.getMediaType() == MediaType.GIF || entity.getMediaDuration() < 30000) {
				if (!settings.isShowGifs()) {
					continue;
				}
			} else if (entity.getMediaType() == MediaType.VIDEO) {
				if (!settings.isShowVideos()) {
					continue;
				}
			}
			
			if (settings.isShowOnlyNewEntities() && !newEntities.contains(entity)) {
				continue;
			}
			
			if (settings.isEnableLimit() && entity.getTagList().size() > settings.getLimit()) {
				continue;
			}
			
			if (!listManager.isWhitelistOk(entity) || !listManager.isBlacklistOk(entity)) {
				continue;
			}
			
			getEntities().add(entity);
		}
		
		if (!getEntities().isEmpty()) {
			Reload.notify(Notifier.FILTER);
		}
	}
	
	public static void showSimilar(Entity entity) {
		listManager.getWhitelist().clear();
		listManager.getBlacklist().clear();
		Loader.INSTANCE.clear();
		
		TagList query = entity.getTagList();
		for (Entity iterator : EntityList.getMain()) {
			if (iterator.getTagList().size() != 0) {
				TagList sameTags = new TagList(query);
				sameTags.retainAll(iterator.getTagList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= settings.getSimilarityFactor()) {
						Loader.INSTANCE.add(iterator);
					}
				}
			}
		}
	}
	public static EntityList applyTo(EntityList listBefore) {
		EntityList entityList = new EntityList();
		listBefore.forEach(entity -> {
			if (Loader.INSTANCE.contains(entity)) {
				entityList.add(entity);
			}
		});
		return entityList;
	}
	
	public static FilterSettings getSettings() {
		return settings;
	}
	public static FilterListManager getListManager() {
		return listManager;
	}
	public static EntityList getNewEntities() {
		return newEntities;
	}
	
	private Filter() {}
	private static class Loader {
		private static final Filter INSTANCE = new Filter();
	}
	public static Filter getEntities() {
		return Loader.INSTANCE;
	}
}
