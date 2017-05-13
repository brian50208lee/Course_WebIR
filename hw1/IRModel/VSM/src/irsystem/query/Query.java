package irsystem.query;

public class Query {
	private String number;
	private String title;
	private String question;
	private String narrative;
	private String concepts;

	public void setNumber(String number) {this.number = number;}
	public void setTitle(String title) {this.title = title;}
	public void setQuestion(String question) {this.question = question;}
	public void setNarrative(String narrative) {this.narrative = narrative;}
	public void setConcepts(String concepts) {this.concepts = concepts;}
	
	public String getNumber() {return this.number;}
	public String getTitle() {return this.title;}
	public String getQuestion() {return this.question;}
	public String getNarrative() {return this.narrative;}
	public String getConcepts() {return this.concepts;}
	
	public Query clone(){
		Query query = new Query();
		query.setNumber(this.number);
		query.setTitle(this.title);
		query.setQuestion(this.question);
		query.setNarrative(this.narrative);
		query.setConcepts(this.concepts);
		return query;
	}
}
