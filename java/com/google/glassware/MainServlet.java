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
					+ "<img src='https://mirror-api-playground.appspot.com/links/filoli-spring-fling.jpg' width='100%' height='100%'>"
					+ "<div class='overlay-full'/>"
					+ "<section><img src='https://mirror-api-playground.appspot.com/links/lincoln-avatar.png'/><h1>@abraham_lincoln</h1><h2>Gettysburg, Pennsylvania</h2>"
					+ "Four score and seven years ago, our fathers brought forth on this continent a new nation, conceived in <span class='blue'>#liberty</span></section>"
					+ "</article>";


	/**
	 * Do stuff when buttons on index.jsp are clicked
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String userId = AuthUtil.getUserId(req);
		Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
		String message = "";
		Boolean firstTimeNotification = true;


		/////////////////////////////////
		//	InsertNotification 
		////////////////////////////////
		// Uppgift.Moment
		////////////////////////////////
		if (req.getParameter("operation").equals("InsertNotification")) {

			LOG.fine("Inserting Timeline Item");
			
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setText("Notification");
			
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
			
			um.updateTimelineItem(MirrorClient.getMirror(credential), result.get(result.size()).getId(), "Update", "DEFAULT");

		/////////////////////////////////
		//	InsertBundleCard 
		////////////////////////////////
		} else if (req.getParameter("operation").equals("InsertBundleCard")) {


			//Lista
			List<TimelineItem> items = new ArrayList<TimelineItem>();
			Mirror service = MirrorClient.getMirror(credential);
			TimelineListResponse timelineItems;
			List<TimelineItem> result = new ArrayList<TimelineItem>();
			Timeline.List request;

			request = service.timeline().list();
			timelineItems = request.execute();
			result = timelineItems.getItems();
			//Kort
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setBundleId("abcde");
			//String txt = Integer.toString(items.size());
			//String txt = Boolean.toString(result.isEmpty());
			String txt = "";
			for (int i = 0; i < result.size(); i++){
				txt = txt + result.get(i).getId();
			}
			timelineItem.setText(txt);
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			
			//result.get(0).getId();
			//String txt = result.get(0).toString();
			//String txt = result.toString();

			timelineItem = new TimelineItem();
			String temp = "";
			String booleanTemp = "";
			for (int i=0; i < result.size();i++) {

				temp += result.get(i).getId()+"<< >>";
				booleanTemp +=result.get(i).getIsBundleCover().toString();
				/*if(result.get(i).getIsBundleCover()){

				}else{
					MirrorClient.deleteTimelineItem(credential,result.get(i).getId());
				}*/
				MirrorClient.deleteTimelineItem(credential,result.get(i).getId());


			}

			//timelineItem.setText(result.get(0).getId());
			//timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			timelineItem.setText(temp);
			timelineItem.setIsBundleCover(false);
			MirrorClient.insertTimelineItem(credential, timelineItem);


			TimelineItem timelineItem2 = new TimelineItem();
			timelineItem2.setText(booleanTemp);
			timelineItem2.setIsBundleCover(false);
			MirrorClient.insertTimelineItem(credential, timelineItem2);




		} else if (req.getParameter("operation").equals("DeleteAllCard")) {
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setText("Welcome to the Glass Learn something");
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			timelineItem.setBundleId("abcde");
			timelineItem.setIsBundleCover(false);

			MirrorClient.insertTimelineItem(credential, timelineItem);



		} else if (req.getParameter("operation").equals("insertContact")) {
			if (req.getParameter("iconUrl") == null || req.getParameter("name") == null) {
				message = "Must specify iconUrl and name to insert contact";
			} else {
				// Insert a contact
				LOG.fine("Inserting contact Item");
				Contact contact = new Contact();
				contact.setId(req.getParameter("id"));
				contact.setDisplayName(req.getParameter("name"));
				contact.setImageUrls(Lists.newArrayList(req.getParameter("iconUrl")));
				contact.setAcceptCommands(Lists.newArrayList(new Command().setType("TAKE_A_NOTE")));
				MirrorClient.insertContact(credential, contact);

				message = "Inserted contact: " + req.getParameter("name");
			}

		} else if (req.getParameter("operation").equals("deleteContact")) {

			// Insert a contact
			LOG.fine("Deleting contact Item");
			MirrorClient.deleteContact(credential, req.getParameter("id"));

			message = "Contact has been deleted.";

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
					//temp.setText("Det funkar eller");
					//MirrorClient.getMirror(userCredential).timeline().update(firstcard, temp).execute();
				}


				batch.execute();
				message =
						"Successfully sent cards to " + callback.success + " users (" + callback.failure
						+ " failed).";
			}


		} else if (req.getParameter("operation").equals("deleteTimelineItem")) {

			// Delete a timeline item
			LOG.fine("Deleting Timeline Item");
			MirrorClient.deleteTimelineItem(credential, req.getParameter("itemId"));
			UpdateMirror um = new UpdateMirror();
			um.updateTimelineItem(MirrorClient.getMirror(credential), req.getParameter("itemId"), "Hej på dig", "DEFAULT");

			/*TimelineItem timelineItem3 = new TimelineItem();
			timelineItem3.setText(req.getParameter("itemId"));
			timelineItem3.setBundleId("abcde");
			MirrorClient.insertTimelineItem(credential, timelineItem3);*/


			message = "Timeline Item has been deleted.";

		} else {
			String operation = req.getParameter("operation");
			LOG.warning("Unknown operation specified " + operation);
			message = "I don't know how to do that";
		}
		WebUtil.setFlash(req, message);
		res.sendRedirect(WebUtil.buildUrl(req, "/"));
	}
}
