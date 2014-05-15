/*
 * Copyright (C) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.glassware;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles POST requests from index.jsp
 *
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class MainServlet extends HttpServlet {
	String firstcard = "test";
	TimelineItem temp;
	Boolean firstTimeNotification = true;
	int counter = 0;  

	/**
	 * Private class to process batch request results.
	 * <p/>
	 * For more information, see
	 * https://code.google.com/p/google-api-java-client/wiki/Batch.
	 */
	private final class BatchCallback extends JsonBatchCallback<TimelineItem> {
		private int success = 0;
		private int failure = 0;

		@Override
		public void onSuccess(TimelineItem item, HttpHeaders headers) throws IOException {
			++success;
		}

		@Override
		public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
			++failure;
			LOG.info("Failed to insert item: " + error.getMessage());
		}
	}

	private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());
	public static final String CONTACT_ID = "com.google.glassware.contact.java-quick-start";
	public static final String CONTACT_NAME = "Java Quick Start";


	private static final String PAGINATED_HTML =
			"<article class='author'>"
					+ "<img src='http://littleswedenart.files.wordpress.com/2012/11/ok_glass_wp_hallgren.jpg?w=700&h=' width='100%' height='100%'>"
					//+ "<div class='overlay-full'/><section>Ta upp mobilen och använd WallTagger mot konstverket som X håller upp</section>"
					+ "<div class='overlay-full'/><section><a href='"
					+ "https://www.google.com/maps/place/Tvistev%C3%A4gen+46/@63.8166591,20.3176932,17z/data=!3m1!4b1!4m2!3m1!1s0x467c5afa16a46649:0x4fc0ec6289014d5a"
					+ "'>maps</a></section>"
					//+ "<section><img src='https://mirror-api-playground.appspot.com/links/lincoln-avatar.png'/><h1>@abraham_lincoln</h1><h2>Gettysburg, Pennsylvania</h2>"
					//+ "Four score and seven years ago, our fathers brought forth on this continent a new nation, conceived in <span class='blue'>#liberty</span></section>"
					+ "</article>";


	/**
	 * Do stuff when buttons on index.jsp are clicked
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		GetJsonData test = new GetJsonData();
		test.getData();
		
		String userId = AuthUtil.getUserId(req);
		Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
		String message = "";
		//Boolean firstTimeNotification = true;

		/////////////////////////////////
		//	InsertNotification 
		////////////////////////////////
		// Uppgift.Moment
		////////////////////////////////
		if (req.getParameter("operation").equals("InsertNotification")) {

			LOG.fine("Inserting Timeline Item");
			
			TimelineItem timelineItem = new TimelineItem();
			//timelineItem.setText("Notification");
			timelineItem.setHtml(test.getData());
			
			if(firstTimeNotification){
				timelineItem.setBundleId("Moment");
				timelineItem.setIsBundleCover(true);
				
				List<MenuItem> menuItemList = new ArrayList<MenuItem>();
				List<MenuValue> menuValues = new ArrayList<MenuValue>();
				menuValues.add(new MenuValue().setDisplayName("Starta Uppgiften"));
				menuItemList.add(new MenuItem().setValues(menuValues).setAction("TOGGLE_PINNED"));
				timelineItem.setMenuItems(menuItemList);
				
				firstTimeNotification=false;
			}
			
			
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

			MirrorClient.insertTimelineItem(credential, timelineItem);
			
		/////////////////////////////////
		//	UpdateCoverCard 
		////////////////////////////////
		} else if (req.getParameter("operation").equals("UpdateCoverCard")) {
			
			UpdateMirror um = new UpdateMirror();
			
			/////////////////////////////////
			//
			/////////////////////////////////
			List<TimelineItem> items = new ArrayList<TimelineItem>();
			Mirror service = MirrorClient.getMirror(credential);
			TimelineListResponse timelineItems;
			List<TimelineItem> result = new ArrayList<TimelineItem>();
			Timeline.List request;

			request = service.timeline().list();
			timelineItems = request.execute();
			result = timelineItems.getItems();
			//System.out.println(result.size() + "  " + result.get(result.size()-1).getIsPinned());
			
			
			
			for(int i = 0; i < result.size(); i++){
				try{
					if(result.get(i).getIsPinned()){
						um.updateTimelineItem(MirrorClient.getMirror(credential), result.get(i).getId(), "Rubrik: WallTagger", "DEFAULT");
					}
				}catch(NullPointerException e){
					System.out.println("null");
				}
			}
			
			//um.updateTimelineItem(MirrorClient.getMirror(credential), result.get(result.size()-1).getId(), "Rubrik: WallTagger", "DEFAULT");

		/////////////////////////////////
		//	InsertBundleCard 
		////////////////////////////////
		} else if (req.getParameter("operation").equals("InsertBundleCard")) {

			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setBundleId("Moment");
			timelineItem.setText("Bundle"+counter);
			//timelineItem.setHtml(PAGINATED_HTML);
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

			MirrorClient.insertTimelineItem(credential, timelineItem);
			counter++;
			
		/////////////////////////////////
		//	DeleteAllCard 
		////////////////////////////////
		} else if (req.getParameter("operation").equals("DeleteAllCard")) {
			
			
			List<TimelineItem> items = new ArrayList<TimelineItem>();
			Mirror service = MirrorClient.getMirror(credential);
			TimelineListResponse timelineItems;
			List<TimelineItem> result = new ArrayList<TimelineItem>();
			Timeline.List request;

			request = service.timeline().list();
			timelineItems = request.execute();
			result = timelineItems.getItems();
			
			
			
			for (int i=0; i < result.size();i++) {
				MirrorClient.deleteTimelineItem(credential,result.get(i).getId());
			}
			
			firstTimeNotification = true;
			
		/////////////////////////////////
		//	 insertItemAllUsers
		////////////////////////////////
		} else if (req.getParameter("operation").equals("insertItemAllUsers")) {
			if (req.getServerName().contains("glass-java-starter-demo.appspot.com")) {
				message = "This function is disabled on the demo instance.";
			}

			// Insert a contact
			List<String> users = AuthUtil.getAllUserIds();
			LOG.info("found " + users.size() + " users");
			if (users.size() > 10) {
				// We wouldn't want you to run out of quota on your first day!
				message =
						"Total user count is " + users.size() + ". Aborting broadcast " + "to save your quota.";
			} else {
				TimelineItem allUsersItem = new TimelineItem();
				//allUsersItem.setIsBundleCover(false);
				allUsersItem.setText("Hello Everyone!"+firstcard);

				BatchRequest batch = MirrorClient.getMirror(null).batch();
				BatchCallback callback = new BatchCallback();

				// TODO: add a picture of a cat
				for (String user : users) {
					Credential userCredential = AuthUtil.getCredential(user);
					MirrorClient.getMirror(userCredential).timeline().insert(allUsersItem)
					.queue(batch, callback);
				}

				batch.execute();
				message =
						"Successfully sent cards to " + callback.success + " users (" + callback.failure
						+ " failed).";
			}

		} else {
			String operation = req.getParameter("operation");
			LOG.warning("Unknown operation specified " + operation);
			message = "I don't know how to do that";
		}
		WebUtil.setFlash(req, message);
		res.sendRedirect(WebUtil.buildUrl(req, "/"));
	}
}
