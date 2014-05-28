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
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
	
	//private TimelineItem temp;
	private Boolean firstTimeNotification = true;
	static int counter = 0;
	private Credential credential;

	
	/*
	 * databas 		<string, string, stack<string>> for each moment 
	 * bundleCover 	Is a list that save all cover list items so than we can show the user a list with the previous monent 
	 */
	private Stack databas = new Stack();
	private ArrayList<String> bundleCover = new ArrayList<String>();;
	private String bundleCoverId;
	private String firstCoverText; 
	
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

	/*
	 * Function that check the first notification card get pinned by the user 
	 * When pin the notification transfer to a bundle cover and create and send bundle card
	 */
	private class CheckIfPin implements Runnable {

		/*
		 * 	
		 */
		List<TimelineItem> items = new ArrayList<TimelineItem>();
		Mirror service;
		TimelineListResponse timelineItems;
		List<TimelineItem> result = new ArrayList<TimelineItem>();
		Timeline.List request;
		UpdateMirror um = new UpdateMirror();


		/*
		 * Cards that create and update bundle in a second thread if is pin 
		 * Stop if they not pin card in 5 min 
		 */
		public void run() {
			
			for (int i = 0; i < 100; i++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				updateConnection();
				try{
					if(result.get(result.size()-1).getIsPinned()){
						
						bundleCoverId = result.get(result.size()-1).getId();
						updateCoverCard(um,service);
						
						Stack<String> bundleStack = new Stack<String>();
						bundleStack = (Stack)databas.pop();
						
						while(!bundleStack.empty()){
							CreateBundleCards((String)bundleStack.pop()); 
						}
						
						Thread.currentThread().stop();
					}
				}catch(NullPointerException e){
					System.out.println("null");
				}
			}
			System.out.println("Its stop");
			Thread.currentThread().stop();
		}

		/*
		 * updateConnection
		 * get new timeline items data 
		 */
		public void updateConnection(){
			service = MirrorClient.getMirror(credential);
			try {
				request = service.timeline().list();
				timelineItems = request.execute();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			result = timelineItems.getItems();
		}
	}

	
	public static final String CONTACT_ID = "com.google.glassware.contact.java-quick-start";
	public static final String CONTACT_NAME = "Java Quick Start";

/**
 * 
 */
	private static final String PAGINATED_HTML =
			"<article class='author'>"
					+ "<img src='http://littleswedenart.files.wordpress.com/2012/11/ok_glass_wp_hallgren.jpg?w=700&h=' width='100%' height='100%'>"
					+ "<div class='overlay-full'/><section><a href='"
					+ "https://www.google.com/maps/place/Tvistev%C3%A4gen+46/@63.8166591,20.3176932,17z/data=!3m1!4b1!4m2!3m1!1s0x467c5afa16a46649:0x4fc0ec6289014d5a"
					+ "'>maps</a></section>"
					+ "</article>";

	
	/*
	 * 	Create bundle card 
	 */
	public void CreateBundleCards(String bundleCard){
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setBundleId("sun");
		timelineItem.setHtml(bundleCard);
		timelineItem.setNotification(new NotificationConfig().setLevel("NULL"));

		try {
			MirrorClient.insertTimelineItem(credential, timelineItem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		counter++;
	}
	
	
	
	/*
	 * Find and Delete the rest
	 */
	void findCoverIdAndDeleteRest(List<TimelineItem> result){
		for(int i = 0; i < result.size(); i++){
			try{
				if(result.get(i).getIsBundleCover()){
					bundleCoverId = result.get(i).getId();
				}
			}catch(NullPointerException e){
				System.out.println("null");
				try {
					MirrorClient.deleteTimelineItem(credential,result.get(i).getId());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Add new list element from database
	 * write list element to string reverse order 
	 * when the list contains more then 6 element stop writing  
	 * the update cover card in mirror
	 */
	void updateCoverCard(UpdateMirror um, Mirror service){
		int numbOfList = 0;
		String temp = "";
		
		bundleCover.add((String)databas.pop());
		
		for(int j = bundleCover.size()-1; j >= 0; j--){
			if(numbOfList < 6){
				if(numbOfList < 1){
					temp  += "<li>"+bundleCover.get(j)+"</li>";
				}else{
					temp  += "<li class='gray'>"+bundleCover.get(j)+"</li>";
				}
			}
			numbOfList++;
		}
		um.updateTimelineItem(service, bundleCoverId, "<article><section> <ul class='text-x-small'>"+temp+ "</ul></section></article>", "NULL");
	}
	

	/**
	 * Do stuff when buttons on index.jsp are clicked
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String userId = AuthUtil.getUserId(req);
		credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
		String message = "";

		//////////////////////////////////
		//	InsertNotification 
		////////////////////////////////
		// Uppgift.moment
		////////////////////////////////
		if (req.getParameter("operation").equals("InsertStartCard")) {
			GetJsonData jsonData = new GetJsonData();
			jsonData.writeToStack();
			//jsonData.printStack();
			databas = jsonData.getStack();
			
			
			LOG.fine("Inserting Timeline Item");

			TimelineItem timelineItem = new TimelineItem();
			//timelineItem.setText("Notification");
			timelineItem.setHtml((String)databas.pop());

			if(firstTimeNotification){
				timelineItem.setBundleId("sun");
				timelineItem.setIsBundleCover(true);

				List<MenuItem> menuItemList = new ArrayList<MenuItem>();
				List<MenuValue> menuValues = new ArrayList<MenuValue>();
				menuValues.add(new MenuValue().setDisplayName("Starta Uppgiften"));
				menuItemList.add(new MenuItem().setValues(menuValues).setAction("TOGGLE_PINNED"));
				timelineItem.setMenuItems(menuItemList);

				firstTimeNotification=false;
				Runnable runTemp = new CheckIfPin();
				new Thread(runTemp).start();
			}

			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			MirrorClient.insertTimelineItem(credential, timelineItem);


			/////////////////////////////////
			//	UpdateCoverCard 
			////////////////////////////////
		} else if (req.getParameter("operation").equals("InsertNotification")) {
			
			UpdateMirror um = new UpdateMirror();

			List<TimelineItem> items = new ArrayList<TimelineItem>();
			Mirror service = MirrorClient.getMirror(credential);
			TimelineListResponse timelineItems;
			List<TimelineItem> result = new ArrayList<TimelineItem>();
			Timeline.List request;

			request = service.timeline().list();
			timelineItems = request.execute();
			result = timelineItems.getItems();

			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setHtml((String)databas.pop());

			findCoverIdAndDeleteRest(result);
			updateCoverCard(um, service);
			
			//timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			MirrorClient.insertTimelineItem(credential, timelineItem);
			
			Stack<String> bundleStack = new Stack<String>();
			bundleStack = (Stack)databas.pop();
			
			while(!bundleStack.empty()){
				CreateBundleCards((String)bundleStack.pop()); 
			} 
			
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

			System.out.print(result.size() + " cards deleted");

			for (int i=0; i < result.size();i++) {
				MirrorClient.deleteTimelineItem(credential,result.get(i).getId());
			}
			for(int i=0; i < bundleCover.size(); i++){
				bundleCover.remove(i);
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
				allUsersItem.setText("Hello Everyone!");

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
