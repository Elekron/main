package com.google.glassware;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GetJsonData {


	public String getData(){

		JSONParser parser = new JSONParser();
		String notificationText = "";
		String notificationImgsrc = "";
		String coverText = "";
		try 
		{
			Object obj = parser.parse(new FileReader("databas/test.json"));

			JSONObject jsonObject = (JSONObject) obj;

			String notificationRubrik = (String) jsonObject.get("notificationRubrik");
			//System.out.println(notificationRubrik);

			notificationText =  (String) jsonObject.get("notificationText");
			//System.out.println(notificationText);

			notificationImgsrc =  (String) jsonObject.get("notificationImgsrc");
			//System.out.println(notificationImgsrc);

			coverText =  (String) jsonObject.get("coverText");
			//System.out.println(coverText);

			// loop array
			JSONArray msg = (JSONArray) jsonObject.get("coverCard");
			Iterator<String> iterator = msg.iterator();
			while (iterator.hasNext()) 
			{
				System.out.println(iterator.next());
			}

		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "<article class='author'>"
		+ "<img src='"
		+ notificationImgsrc
		+ "' width='100%' height='100%'>"
		+ "<div class='overlay-full'/><section>"
		+ notificationText
		+ "</section>"
		+ "</article>";
	}



}

