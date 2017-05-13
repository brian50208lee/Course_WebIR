package irsystem.corpus;

import java.util.ArrayList;

public class Article {
	private String path;
	private String id;
	private String date;
	private String title;
	private ArrayList<String> texts;
	
	public Article(){
		this.texts = new ArrayList<String>();
	}

	public void setPath(String path) {this.path = path;}
	public void setId(String id) {this.id = id;}
	public void setDate(String date) {this.date = date;}
	public void setTitle(String title) {this.title = title;}
	public void setTexts(ArrayList<String> texts) {this.texts = texts;}
	
	public String getPath() {return this.path;}
	public String getId() {return this.id;}
	public String getDate() {return this.date;}
	public String getTitle() {return  this.title;}
	public ArrayList<String> getTexts() {return this.texts;}
	
	public void addText(String text){
		if (this.texts == null) {
			this.texts = new ArrayList<String>();
		}
		this.texts.add(text);
	}
	
	public int getLength(){
		int len = 0;
		for (String text : texts) {len += text.length();}
		return len;
	}
}
