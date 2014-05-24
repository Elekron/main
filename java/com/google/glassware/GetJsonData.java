package com.google.glassware;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GetJsonData {

	Stack databas = new Stack();
	int bundleCoverListLength = 0;
	String bundleCoverListString = "";
	
	
	public void printStack(){
		for(int j=0; j<2; j++){

			Stack<String> bundleStack = new Stack<String>();
//			System.out.println();
//			System.out.println();
//
//			System.out.println((String)databas.pop());
//			System.out.println();
//			System.out.println((String)databas.pop());
//			System.out.println();
//			System.out.println();

			bundleStack = (Stack)databas.pop();
			for(int i=0; i<3; i++){

				System.out.println(bundleStack.pop());
			}
		}
	}

	public Stack getStack(){
		return databas;
	}


	JSONParser parser = new JSONParser();

	public void writeToStack(){
int k;
		try 
		{
			Object obj = parser.parse(new FileReader("databas/temp2.json"));
			JSONArray array=(JSONArray)obj;

			for(int j=array.size()-1; j>=0; j--){

				//Read the first object from json array	
				JSONObject array1=(JSONObject)array.get(j);

				//Read in the bundle list
				JSONArray bundleArray=(JSONArray)array1.get("bundle");

				Stack<String> bundleStack = new Stack<String>();
				//System.out.println(bundleArray.size()-1);
				for(int i = 0; i<bundleArray.size(); i++){
					
					//Create a Stack for all bundle card 
					JSONObject bundle =(JSONObject)bundleArray.get(i);

					//Push a bundle card to stack
					bundleStack.push("<article><img src='"+bundle.get("b1I")
							+ "' width='100%' height='100%'><div class='overlay-full'/><section>"+bundle.get("b1t")
							+ "</section></article>");
				}

				databas.push(bundleStack);
				databas.push("<article><section>"+array1.get("coverOfBundle")+ "</section></article>");
				
//				bundleCoverListString += "<li>"+array1.get("coverOfBundle")+ "</li>";
//				System.out.print("\nbundleCoverList size: " + bundleCoverListLength);
//				databas.push("<article><section>" + bundleCoverListString + "</section></article>");
//				bundleCoverListLength++;

				
				JSONObject notification=(JSONObject)array1.get("notification");
				databas.push("<article><img src='"+notification.get("nI")
						+ "' width='100%' height='100%'><div class='overlay-full'/><section>"+notification.get("nT")
						+ "</section></article>");
			}

		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

