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

	/*private static final String PAGINATED_HTML =
      "<article class='auto-paginate'>"
      + "<h2 class='blue text-large'>Did you know...?</h2>"
      + "<p>Cats are <em class='yellow'>solar-powered.</em> The time they spend napping in "
      + "direct sunlight is necessary to regenerate their internal batteries. Cats that do not "
      + "receive sufficient charge may exhibit the following symptoms: lethargy, "
      + "irritability, and disdainful glares. Cats will reactivate on their own automatically "
      + "after a complete charge cycle; it is recommended that they be left undisturbed during "
      + "this process to maximize your enjoyment of your cat.</p><br/><p>"
      + "For more cat maintenance tips, tap to view the website!</p>"
      + "</article>";*/

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


		/////////////////////////////////
		//	Insert a Notification kort 
		////////////////////////////////
		if (req.getParameter("operation").equals("insertItem")) {

		
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();

			temp = timelineItem;

			
			//timelineItem.setText("Starta appen");
			timelineItem.setHtml(PAGINATED_HTML);
			timelineItem.setBundleId("abcde");
			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			menuItemList.add(new MenuItem().setAction("REPLY"));
			menuItemList.add(new MenuItem().setAction("READ_ALOUD"));

			List<MenuValue> menuValues = new ArrayList<MenuValue>();
			menuValues.add(new MenuValue().setDisplayName("Startar appen"));

			menuItemList.add(new MenuItem().setValues(menuValues).setAction("TOGGLE_PINNED"));
			timelineItem.setMenuItems(menuItemList);

			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

			//timelineItem.setId("Pannkaka");
			//firstcard = timelineItem.getId();

			MirrorClient.insertTimelineItem(credential, timelineItem);







			//i++;
			//timelineItem.setHtml("<article class='photo'><img src='https://mirror-api-playground.appspot.com/links/filoli-spring-fling.jpg'><div class='overlay-gradient-tall-dark'/><section><p class='text-auto-size'>Spring Fling Fundraiser at Filoli</p></section></article>");
			//timelineItem.setHtml("<article>\n  <figure>\n    <ul class=\"mosaic mosaic3\">\n      <li style=\"background-image: url(https://mirror-api-playground.appspot.com/links/washington.jpg)\"></li>\n      <li style=\"background-image: url(https://mirror-api-playground.appspot.com/links/lincoln.png)\"></li>\n      <li style=\"background-image: url(https://mirror-api-playground.appspot.com/links/obama.jpg)\"></li>\n      <li style=\"background-image: url(https://mirror-api-playground.appspot.com/links/washington.jpg)\"></li>\n      <li style=\"background-image: url(https://mirror-api-playground.appspot.com/links/lincoln.png)\"></li>\n    </ul>\n  </figure>\n  <section>\n    <p class=\"text-small muted\">\n      U.S. Presidents\n    </p>\n    <table class=\"text-small align-justify\">\n      <tbody>\n        <tr>\n          <td>Washington</td>\n          <td class=\"muted\">1<sup>st</sup></td>\n        </tr>\n        <tr>\n          <td>Lincoln</td>\n          <td class=\"muted\">16<sup>th</sup></td>\n        </tr>\n        <tr>\n          <td>Obama</td>\n          <td class=\"muted\">44<sup>th</sup></td>\n        </tr>\n      </tbody>\n    </table>\n  </section>\n</article>\n");
			//timelineItem.setHtml("<article>\n  <section>\n    <div class=\"layout-figure\">\n      <div class=\"align-center\">\n        <p class=\"text-x-large\">BOS</p>\n        <img src=\"https://mirror-api-playground.appspot.com/links/plane.png\" width=\"50\" height=\"50\">\n        <p class=\"text-x-large\">SFO</p>\n      </div>\n      <div>\n        <div class=\"text-normal\">\n          <p>Virgin America 351</p>\n          <p>Gate B38</p>\n          <p>8:35am</p>\n          <p class=\"green\">On Time</p>\n        </div>\n      </div>\n    </div>\n  </section>\n</article>\n");
			//timelineItem.setHtml("<article class='auto-paginate'>\n    <div class=\"overlay-full\"/>\n  <header>\n    <img src=\"https://mirror-api-playground.appspot.com/links/lincoln-avatar.png\"/>\n    <h1>@abraham_lincoln</h1>\n    <h2>Gettysburg, Pennsylvania</h2>\n  </header>\n  <section>\n    <p class=\"text-auto-size\">Four score and seven years ago, our fathers brought forth on this continent a new nation, conceived in <span class=\"blue\">#liberty</span></p>\n <img src=\"http://wallpoper.com/images/00/44/86/76/dark-energy_00448676.jpg\">\n </section>\n</article>\n");

			//timelineItem.setHtml(PAGINATED_HTML);
			//timelineItem.setIsBundleCover(true);
			/*timelineItem.setBundleId("abcde");
      List<MenuItem> menuItemList = new ArrayList<MenuItem>();

      List<MenuValue> menuValues = new ArrayList<MenuValue>();
      menuValues.add(new MenuValue().setDisplayName("Startar appen"));

      menuItemList.add(new MenuItem().setValues(menuValues).setAction("TOGGLE_PINNED"));
      timelineItem.setMenuItems(menuItemList);
			 */



			/*if (req.getParameter("message") != null) {
        timelineItem.setText(req.getParameter("message"));
      }*/


			/*
      // Triggers an audible tone when the timeline item is received
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      /////////////////////////////////
      // Om det finns bild länk skicka 
      ////////////////////////////////
      if (req.getParameter("imageUrl") != null) {
        // Attach an image, if we have one
        URL url = new URL(req.getParameter("imageUrl"));
        String contentType = req.getParameter("contentType");
        MirrorClient.insertTimelineItem(credential, timelineItem, contentType, url.openStream());
      } else {
        MirrorClient.insertTimelineItem(credential, timelineItem);
      }

      message = "A timeline item has been inserted.";*/
			/////////////////////////////////
			//	Insert a HTML 
			////////////////////////////////
		} else if (req.getParameter("operation").equals("insertPaginatedItem")) {


			String test = "Its works najs";
			UpdateMirror um = new UpdateMirror();
			Mirror service = MirrorClient.getMirror(credential);
			um.updateTimelineItem(service, message, test, "DEFAULT");

			/*	  
      LOG.fine("Inserting Timeline Item");
      TimelineItem timelineItem = new TimelineItem();
      timelineItem.setHtml(PAGINATED_HTML);


      List<MenuItem> menuItemList = new ArrayList<MenuItem>();
      menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(
          "https://www.google.com/search?q=cat+maintenance+tips"));
      timelineItem.setMenuItems(menuItemList);

      // Triggers an audible tone when the timeline item is received
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      MirrorClient.insertTimelineItem(credential, timelineItem);

      message = "A timeline item has been inserted.";*/

		} else if (req.getParameter("operation").equals("insertItemWithAction")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setText("Här kan du svar tillbaka på mitt kort genom att klicka på det så använda REPLY :) //Andreas");

			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			// Built in actions
			menuItemList.add(new MenuItem().setAction("REPLY"));
			menuItemList.add(new MenuItem().setAction("READ_ALOUD"));

			// And custom actions
			List<MenuValue> menuValues = new ArrayList<MenuValue>();
			menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/drill.png"))
					.setDisplayName("Drill In"));
			menuItemList.add(new MenuItem().setValues(menuValues).setId("drill").setAction("CUSTOM"));

			timelineItem.setMenuItems(menuItemList);
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

			MirrorClient.insertTimelineItem(credential, timelineItem);

			message = "A timeline item with actions has been inserted.";

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
