package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	public static void main(String[] args) {
		 try {
			Document doc = Jsoup.connect("https://play.google.com/store/apps/category/FINANCE/collection/topselling_paid").timeout(0).get();
			Document detDoc = null;
			ArrayList<String> descriptions = new ArrayList<String>();
			HashSet<String> hrefs = new HashSet<String>();
			Elements anchors = doc.getElementsByClass("card-click-target");
			for (Element i : anchors) {
				hrefs.add("https://play.google.com/" + i.attr("href").toString());
			}
			
			for (String url : hrefs) {
				detDoc = Jsoup.connect(url).timeout(0).get();
				descriptions.add(detDoc.select("[itemProp='description']").text());
			}
			
			for (String i : descriptions) {
				System.out.println(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
}
