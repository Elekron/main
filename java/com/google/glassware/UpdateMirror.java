package com.google.glassware;

import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;

import java.io.IOException;
public class UpdateMirror {

	// ...

	/**
	 * Update a timeline item in the user's Glass with an optional notification and attachment.
	 * 
	 * @param service Authorized Mirror service.
	 * @param itemId ID of the timeline item to update.
	 * @param newText timeline item's text.
	 * @param newNotificationLevel Optional notification level, supported values
	 *        are {@code null} and "AUDIO_ONLY".
	 * @return Updated timeline item on success, {@code null} otherwise.
	 */
	public TimelineItem updateTimelineItem(Mirror service, String itemId, String newText,String newNotificationLevel) {
		try {
			// First retrieve the timeline item from the API.
			TimelineItem timelineItem = service.timeline().get(itemId).execute();
			// Update the timeline item's metadata.
			//timelineItem.setText(newText);
			timelineItem.setHtml(newText);
			if (newNotificationLevel != null && newNotificationLevel.length() > 0) {
				timelineItem.setNotification(new NotificationConfig().setLevel(newNotificationLevel));
			} else {
				timelineItem.setNotification(null);
			}
			return service.timeline().update(itemId, timelineItem).execute();
		} catch (IOException e) {
			System.err.println("An error occurred: " + e);
			return null;
		}
	}

	// ...
}



