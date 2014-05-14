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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.Subscription;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility functions used when users first authenticate with this service
 *
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class NewUserBootstrapper {
  private static final Logger LOG = Logger.getLogger(NewUserBootstrapper.class.getSimpleName());

  /**
   * Bootstrap a new user. Do all of the typical actions for a new user:
   * <ul>
   * <li>Creating a timeline subscription</li>
   * <li>Inserting a contact</li>
   * <li>Sending the user a welcome message</li>
   * </ul>
   */
  public static void bootstrapNewUser(HttpServletRequest req, String userId) throws IOException {
    Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);

    // Create contact
    Contact starterProjectContact = new Contact();
    starterProjectContact.setId(MainServlet.CONTACT_ID);
    starterProjectContact.setDisplayName(MainServlet.CONTACT_NAME);
    starterProjectContact.setImageUrls(Lists.newArrayList(WebUtil.buildUrl(req,
        "/static/images/chipotle-tube-640x360.jpg")));
    starterProjectContact.setAcceptCommands(Lists.newArrayList(
        new Command().setType("TAKE_A_NOTE")));
    Contact insertedContact = MirrorClient.insertContact(credential, starterProjectContact);
    LOG.info("Bootstrapper inserted contact " + insertedContact.getId() + " for user " + userId);

    try {
      // Subscribe to timeline updates
      Subscription subscription =
          MirrorClient.insertSubscription(credential, WebUtil.buildUrl(req, "/notify"), userId,
              "timeline");
      LOG.info("Bootstrapper inserted subscription " + subscription
          .getId() + " for user " + userId);
    } catch (GoogleJsonResponseException e) {
      LOG.warning("Failed to create timeline subscription. Might be running on "
          + "localhost. Details:" + e.getDetails().toPrettyString());
    }
    
   //Send welcome timeline item
    TimelineItem timelineItem = new TimelineItem();
    //timelineItem.setIsPinned(true);
    //timelineItem.setPinScore(90);
    //
    
    timelineItem.setText("Welcome to the Glass Learn something");
	//timelineItem.setHtml("<article class='auto-paginate'>\n    <div class=\"overlay-full\"/>\n  <header>\n    <img src=\"https://mirror-api-playground.appspot.com/links/lincoln-avatar.png\"/>\n    <h1>@abraham_lincoln</h1>\n    <h2>Gettysburg, Pennsylvania</h2>\n  </header>\n  <section>\n    <p class=\"text-auto-size\">Four score and seven years ago, our fathers brought forth on this continent a new nation, conceived in <span class=\"blue\">#liberty</span></p>\n <img src=\"http://wallpoper.com/images/00/44/86/76/dark-energy_00448676.jpg\">\n </section>\n</article>\n");

    timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
    timelineItem.setBundleId("abcde");
    timelineItem.setIsBundleCover(true);
    
    
    TimelineItem insertedItem = MirrorClient.insertTimelineItem(credential, timelineItem);
    LOG.info("Bootstrapper inserted welcome message " + insertedItem.getId() + " for user "
        + userId);
       
  }
}
