<!--
Copyright (C) 2013 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<%@ page import="com.google.api.client.auth.oauth2.Credential" %>
<%@ page import="com.google.api.services.mirror.model.Contact" %>
<%@ page import="com.google.glassware.MirrorClient" %>
<%@ page import="com.google.glassware.WebUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.api.services.mirror.model.TimelineItem" %>
<%@ page import="com.google.api.services.mirror.model.Subscription" %>
<%@ page import="com.google.api.services.mirror.model.Attachment" %>
<%@ page import="com.google.glassware.MainServlet" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="java.io.*"  %>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<!doctype html>
<%
  String userId = com.google.glassware.AuthUtil.getUserId(request);
  String appBaseUrl = WebUtil.buildUrl(request, "/");

  Credential credential = com.google.glassware.AuthUtil.getCredential(userId);

  Contact contact = MirrorClient.getContact(credential, MainServlet.CONTACT_ID);

  List<TimelineItem> timelineItems = MirrorClient.listItems(credential, 3L).getItems();


  List<Subscription> subscriptions = MirrorClient.listSubscriptions(credential).getItems();
  boolean timelineSubscriptionExists = false;
  boolean locationSubscriptionExists = false;


  if (subscriptions != null) {
    for (Subscription subscription : subscriptions) {
      if (subscription.getId().equals("timeline")) {
        timelineSubscriptionExists = true;
      }
      if (subscription.getId().equals("locations")) {
        locationSubscriptionExists = true;
      }
    }
  }

/*  String str = "DAMN";

  String file = "";
  try {   
      PrintWriter pw = new PrintWriter(new FileOutputStream(file));
      pw.println(str);
      //clean up
      pw.close();
  } catch(IOException e) {
     out.println(e.getMessage());
  }*/

%>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Glass for education</title>
  <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet"
        media="screen">
  <link href="/static/bootstrap/css/bootstrap-responsive.min.css"
        rel="stylesheet" media="screen">
  <link href="/static/main.css" rel="stylesheet" media="screen">
  <link href='http://fonts.googleapis.com/css?family=Roboto:100,300' rel='stylesheet' type='text/css'>
</head>
<body>
<div class="navbar navbar-inverse navbar-static-top" id="main-header">
  <div class="navbar-inner">
    <div class="container">
      <a class="navbar-brand" href="#">Education for Google Glass</a>
    </div>
  </div>
</div>

<div class="container">

  <% String flash = WebUtil.getClearFlash(request);
    if (flash != null) { %>
  <div class="alert alert-info"><%= StringEscapeUtils.escapeHtml4(flash) %></div>
  <% } %>

  <div class="row">
    <div class="col-md-4 col-sm-6 col-xs-12">

        <div class="hidden-xs teacher-info">
          <h2>Teacher tool</h2>
          <p>This is the control panel for Education for Glass. Here you can add homework, tasks or challenges that will be pushed to the students Glass. Just use the form to fill in the task information, and use the buttons below to push them to the timeline.</p>
        </div>

      <h2>Manage the timeline</h2>

      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="InsertStartCard">
        <button class="btn btn-info btn-lg btn-block" type="submit">
          Insert start card
        </button>
      </form>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="InsertNotification">
        <button class="btn btn-info btn-lg btn-block" type="submit">
          Insert notification
        </button>
      </form>
     <!-- <hr>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="UpdateCoverCard">
        <button class="btn btn-info btn-lg btn-block" type="submit">
          Update CoverCard 
        </button>
      </form>
      <hr>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="InsertBundleCard">
        <button class="btn btn-info btn-lg btn-block" type="submit">
          Insert BundleCard
        </button>
      </form>
      <hr>-->
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="DeleteAllCard">
        <button class="btn btn-info btn-lg btn-block" type="submit">
          Delete all cards</button>
      </form>
      <hr>
    </div>

    <div class="col-md-6 col-sm-6 col-xs-12" id="content-form">
      <form name="frm" method="post" action="service.jsp">    

        <h3>Title</h3>
        <div class="form-group"><label>Title for bundle cover</label><input class="form-control" type="text" name="coverOfBundle"></div>

      <h3>Notification</h3>
        <div class="form-group"><label>Text</label><input class="form-control" type="text" name="notificationText"></div>
        
        <div class="form-group"><label>Image</label><input class="form-control" type="text" name="notificationImg">
        <p class="small help-block">http://example.com/imageadress.jpg</p>
        </div>
        
        <h3>Bundled information</h3>
        <div id="bundle-info">
          <p>Card 1</p>
          <div class="form-group"><label>Text</label><input class="form-control" type="text" name="bundleText1"> </div>
          
          <div class="form-group"><label>Image</label><input class="form-control" type="text" name="bundleImg1">
          <p class="help-block small">http://example.com/imageadress.jpg</p> 
          </div>
        </div>
        <button onclick="addBundle();" type="button" id="btn-bundle" class="btn btn-info">Add bundle</button><br />
        
        <input type="submit" name="submit" value="Submit to database" class="btn btn-lg btn-info" />
      </form>
    </div>
  </div>

  <h1>Your Recent Timeline</h1>
  <div class="row">

    <div style="margin-top: 5px;">

      <% if (timelineItems != null && !timelineItems.isEmpty()) {
        for (TimelineItem timelineItem : timelineItems) { %>
      <div class="span4">
        <table class="table table-bordered">
          <tbody>
            <tr>
              <th>ID</th>
              <td><%= timelineItem.getId() %></td>
            </tr>
            <tr>
              <th>Text</th>
              <td><%= StringEscapeUtils.escapeHtml4(timelineItem.getText()) %></td>
            </tr>
            <tr>
              <th>HTML</th>
              <td><%= StringEscapeUtils.escapeHtml4(timelineItem.getHtml()) %></td>
            </tr>
            <tr>
              <th>Attachments</th>
              <td>
                <%
                if (timelineItem.getAttachments() != null) {
                  for (Attachment attachment : timelineItem.getAttachments()) {
                    if (MirrorClient.getAttachmentContentType(credential, timelineItem.getId(), attachment.getId()).startsWith("")) { %>
                <img src="<%= appBaseUrl + "attachmentproxy?attachment=" +
                  attachment.getId() + "&timelineItem=" + timelineItem.getId() %>">
                <%  } else { %>
                <a href="<%= appBaseUrl + "attachmentproxy?attachment=" +
                  attachment.getId() + "&timelineItem=" + timelineItem.getId() %>">
                  Download</a>
                <%  }
                  }
                } else { %>
                <span class="muted">None</span>
                <% } %>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <form class="form-inline"
                      action="<%= WebUtil.buildUrl(request, "/main") %>"
                      method="post">
                  <input type="hidden" name="itemId"
                         value="<%= timelineItem.getId() %>">
                  <input type="hidden" name="operation"
                         value="deleteTimelineItem">
                  <button class="btn btn-block btn-danger"
                          type="submit">Delete</button>
                </form>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <% }
      } else { %>
      <div class="span12">
        <div class="alert alert-info">
          You haven't added any items to your timeline yet. Use the controls
          above to add something!
        </div>
      </div>
      <% } %>
    </div>
    <div style="clear:both;"></div>
  </div>

</div>

<footer>&copy; EduGlass Project Group</footer>

<script
    src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script src="/static/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">

  var i = 1;

  function addBundle() {

    i++;

    var temp = "<p>Card " + i + "</p><div class=\"form-group\"><label>Text</label><input class=\"form-control\" type=\"text\" name=\"bundleText" + i + "\"></div><div class=\"form-group\"><label>Image</label><input class=\"form-control\" type=\"text\" name=\"bundleImg" + i + "\"><p class=\"help-block small\">http://example.com/imageadress.jpg</p></div>";

    $('#bundle-info').append(temp);

  }

</script>
</body>
</html>
