package main;
import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class AppStore {
	//main url where are the hrefs_categories
	final static String main_url_es = "https://itunes.apple.com/co/genre/ios/id36/";
	final static String main_url_en = "https://itunes.apple.com/genre/ios/id36/";
	String main_url = "";
	final static String default_url = "https://itunes.apple.com/co/genre/";
	//hashset containing app store categories
	HashSet<Category> categories = null;
	//hashset containing hrefs categories from main_url with class top-level-genre
	HashSet<String> hrefs_categories = null;
	ArrayList<String> name_categories = null;
	ArrayList<Category> final_categories = null;
	//document with main content
	Document doc;
	//scanner for user input
	Scanner sc; 

	
	
	//inner class App store category
	class Category {
		//hashset containing all the href to popular apps from a specific category
		HashSet<String> hrefs_popular = null;
		ArrayList<App> apps_popular = null;
		String name;
		//document for each  category
		Document category_doc;
		StringBuilder sb;
		PrintWriter pw;
		public Category(Document doc, String name){
			this.category_doc = doc;
			hrefs_popular = new HashSet<String>();
			apps_popular = new ArrayList<App>();
			this.name = name;
			
		}
		
		public void getHrefsPopular(){
			System.out.println("Getting apps urls popular of category "+ name);
			Elements hrefs = category_doc.getElementById("selectedcontent").getElementsByTag("a");
			int i =0;
			for (Element element : hrefs) {
				System.out.println(element);
				apps_popular.add(new App(i,element));
				i++;
				//set the number of apps scraped
				if(i==2)
					break;
				
			}
			System.out.println("got all the popular apps from: "+ this.name+" category.");
		}
		public void generateCSV(){
			System.out.println("Generating CSV");
			App a = apps_popular.get(0);
			writeHeader(name);
			for (int i = 0; i < apps_popular.size(); i++) {
				apps_popular.get(i).generateCSV(sb,i);
			}			
			finishCSV();
			System.out.println(a.url);
			System.out.println(a.name);
			
		}
		
		public void writeHeader(String fileName){
			
			try {
				pw = new PrintWriter(new FileWriter(new File(fileName+".csv")), true);
		        sb.append("id");
		        sb.append(',');
		        sb.append("name");
		        sb.append(',');
		        sb.append("cost");
		        sb.append(',');
		        sb.append("rate_actual");
		        sb.append(',');
		        sb.append("num_ratings_actual");
		        sb.append(',');
		        sb.append("rate_all");
		        sb.append(',');
		        sb.append("nume_ratings_all");
		        sb.append(',');
		        sb.append("description");
		        sb.append(',');
		        sb.append("new_features");
		        sb.append(',');
		        sb.append("avg_last");
		        sb.append('\n');
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("ERROR GENERATING PRINWRITER FOR: "+fileName);
			}
			

		}
		public void finishCSV(){
	        pw.write(sb.toString());
	        pw.close();
	        System.out.println("DONE!");
		}

		
		class App {
			Document app;
			String name;
			int i;
			String url;
	
			public App ( int i, Element e ){
				this.i=i;
				try {
					url = e.attr("href").toString();
					app = Jsoup.connect(url).timeout(0).get();
					name = e.text();
					sb = new StringBuilder();
					
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
					System.out.println("ERROR READING URL: "+url);
				}
			}
			public void generateCSV(StringBuilder sb, int id){
				/*
				 * FORMAT
				 *  sb.append("id");
			        sb.append(',');
			        sb.append("name");
			        sb.append(',');
			        sb.append("cost");
			        sb.append(',');
			        sb.append("rate_actual");
			        sb.append(',');
			        sb.append("num_ratings_actual");
			        sb.append(',');
			        sb.append("rate_all");
			        sb.append(',');
			        sb.append("num_ratings_all");
			        sb.append(',');
			        sb.append("description");
			        sb.append(',');
			        sb.append("new_features");
			        sb.append(',');
			        sb.append("avg_last");
				 * 
				 * */

				String description = app.select("[itemprop='description']").text().replaceAll(",","");
				String new_features = app.getElementsByClass("product-review").get(1).getElementsByTag("p").text().replaceAll(",","");
				String cost = app.getElementsByClass("price").text();
				String rate_actual = app.getElementsByClass("customer-ratings").select("[itemprop='ratingValue']").text().replaceAll(",","");
				String num_ratings_actual = app.getElementsByClass("customer-ratings").select("[itemprop='reviewCount']").text().split(" ")[0];
				//System.out.println("aria-label::"+app.getElementsByClass("customer-ratings").select("[class='rating']").get(1).attr("aria-label"));
				try{
					System.out.println("aria-label::"+app.getElementsByClass("customer-ratings").select("[class='rating']").get(1).attr("aria-label"));
						
				}catch(Exception e){
					System.out.println();
				}
				String[] rate_old = null;
				String rate_all;
				String num_ratings_all;
				try{
					rate_old = app.getElementsByClass("customer-ratings").select("[class='rating']").get(1).attr("aria-label").split(",");
					rate_all = rate_old[0].split(" ")[0];
					num_ratings_all = rate_old[1].split(" ")[0];
				}catch(Exception e){
					rate_all = "";
					num_ratings_all = "";
				}
					
				Elements reviews = app.getElementsByClass("customer-review");
				int total=0;
				for (int i = 0; i < reviews.size(); i++) {
					String raw_value = reviews.get(i).getAllElements().get(1).getAllElements().get(2).attr("aria-label").split(" ")[0];
					
					int val;
					try{
						val = Integer.parseInt(raw_value);
					}catch(Exception e){
						val=1;
					}
					total+=val;
				}
				double avg_last = total/reviews.size(); 
				
		        sb.append(id);
		        sb.append(',');
		        sb.append(name.replaceAll(",", ""));
		        sb.append(',');
		        sb.append(cost);
		        sb.append(',');
		        sb.append(rate_actual);
		        sb.append(',');
		        sb.append(num_ratings_actual);
		        sb.append(',');
		        sb.append(rate_all);
		        sb.append(',');
		        sb.append(num_ratings_all);
		        sb.append(',');
		        sb.append(description);
		        sb.append(',');
		        sb.append(new_features);
		        sb.append(',');
		        sb.append(avg_last);
		        sb.append('\n'); 
		        
			}
			
		}
		
	}
	
	public AppStore(){
		sc = new Scanner(System.in);
		System.out.println("Welcome to AppStore Crawler");
		System.out.println("Please select write 0 for spanish, 1 for english");

		int leng=Integer.parseInt(sc.nextLine());
		if (leng == 1){
			main_url = main_url_en;
		}else {
			main_url = main_url_es;
		}
		
		 try {
			doc = Jsoup.connect(main_url).timeout(0).get();
			categories= new HashSet<Category>();
			name_categories = new ArrayList<String>();
			hrefs_categories = new HashSet<String>();
			final_categories = new ArrayList<Category>();
			getHrefsCategories();
			getCategories();
			int iCategory = sc.nextInt();
			Category category = getCategory(iCategory);
			category.getHrefsPopular();
			category.generateCSV();
			
		} catch (IOException e) {
			System.out.println("ERROR: Jsoup.connect("+main_url+").timeout(0).get()");
			e.getStackTrace();
		}
	}

	public void display(ArrayList<String> arr, String type){
		int i = 0;
		for (String string : arr) {
			System.out.println(i+" - "+string);
			i++;
		}
		System.out.println("---------"+arr.size()+" " + type +" found. ----------");
	}
	/**
	 * get the href links of all the categories
	 * */
	public void getHrefsCategories(){
		System.out.println("Getting the urls of the categories from: " + main_url);
		Elements hrefs = doc.getElementsByClass("top-level-genre");
		for (Element element : hrefs) {
			hrefs_categories.add(element.attr("href").toString());
			name_categories.add(element.text());
		}
		display(name_categories, "categories");
		
		
		
	}
	/**
	 * get the categories
	 * @throws IOException 
	 * */
	public void getCategories() throws IOException{
		int i=0;
		for (String category : hrefs_categories) {
			Document d = Jsoup.connect(category).timeout(0).get();
			Category c = new Category(d, name_categories.get(i));
			categories.add(c);
			i++;
		}
		for (Category category : categories) {
			final_categories.add(category);
		}
		
	}
	public Category getCategory(int i) {

		Category actual = final_categories.get(i);
		System.out.println("Selected " + actual.name);
		return actual;
	}
	
	public static void main(String[] args) {
		AppStore ap = new AppStore();
	}
	
}
