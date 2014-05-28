<%@ page import="java.io.*"  %>

<%@ page contentType="text/html; charset=iso-8859-1" import="org.json.simple.JSONObject"%>

<%
  String notificationText=request.getParameter("notificationText");
  String notificationImg=request.getParameter("notificationImg");
  String coverCardText=request.getParameter("coverCardText");
  String bundleTitle=request.getParameter("bundleTitle");
  String bundleText=request.getParameter("bundleText");
  String bundleImg=request.getParameter("bundleImg");
  
  String nameOfTextFile = "databas/test.json"; //directory path and name of file
  
/*  JSONObject obj=new JSONObject();
    obj.put("notificationText", notificationText);
    obj.put("notificationImg", notificationImg);
    obj.put("coverCardText", coverCardText;
    obj.put("bundleTitle", bundleTitle);
    obj.put("bundleText", bundleText);
    obj.put("bundleImg", bundleImg);

/*    "coverText":"Uppgift H1 finns nu",
  "coverCard":["msg 1","msg 2","msg 3"],
  "notificationImgsrc":"http:\/\/hdwallpapermania.com\/wp-content\/uploads\/2014\/02\/wallpaper-new-cute-cats-hd.jpg",
  "notificationText":
  "This is a first try to read from databas",
  "notificationRubrik":"Welcome to learn something"
*/
  try {   
  PrintWriter printWriter=null;
     printWriter = new PrintWriter(new FileWriter(new File(nameOfTextFile), true));  
     // PrintWriter pw = new PrintWriter(new FileOutputStream(nameOfTextFile));
      printWriter.print("{");
      printWriter.print("\"notificationText\": \"" + notificationText + "\",");
      printWriter.print("\"notificationImg\": \"" + notificationImg + "\",");
      printWriter.print("\"coverCardText\": \"" + coverCardText + "\",");
      printWriter.print("\"bundleTitle\": \"" + bundleTitle + "\",");
      printWriter.print("\"bundleText\": \"" + bundleText + "\",");
      printWriter.print("\"bundleImg\": \"" + bundleImg + "\"");
      printWriter.print("}");

      printWriter.close();
      
  /*    out.print(obj);
      out.flush();
    */  } catch(IOException e) {
     out.println(e.getMessage());
  }

    String redirectURL = "index.jsp";
    response.sendRedirect(redirectURL);
%>

<html>
<body>
</body>
</html>