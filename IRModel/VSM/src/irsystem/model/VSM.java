package irsystem.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import irsystem.corpus.Article;
import irsystem.corpus.Corpus;
import irsystem.parser.Parser;
import irsystem.query.Query;
import structure.Tuple;

public class VSM {
	private Corpus corpus;
	private boolean relevance_feedback = false;
	private int feedback_article_number = 1;

	public void setCorpus(Corpus corpus) {this.corpus = corpus;}
	public void setRelevanceFeedback(boolean relevance_feedback) {this.relevance_feedback = relevance_feedback;}
	public void setFeedbackArticleNumber(int feedback_article_number) {this.feedback_article_number = feedback_article_number;}
	
	public Corpus getCorpus() {return this.corpus;}
	public boolean isRelevanceFeedback() {return this.relevance_feedback;}
	public int getFeedbackArticleNumber() {return this.feedback_article_number;}
	
	public VSM(Corpus corpus) {
		this.corpus = corpus;
	}

	public void train() {
		
	}

	public ArrayList<String> search(Query query, int result_size) {
		// parse query
		String number = query.getNumber();
		String title = query.getTitle();
		String concepts = query.getConcepts();
		HashSet<String> ignore_tokens = new HashSet<String>(Arrays.asList("、", "。","「","」","（","）","？","是","的","可","或"));
		ArrayList<String> title_tokens = Parser.parse_segmentation(title, 2, ignore_tokens);
		ArrayList<String> concepts_tokens = Parser.parse_segmentation(concepts, 2, ignore_tokens);
		System.out.println();
		System.out.println("Query Number: " + number);
		System.out.println("Query Title: " + title);
		System.out.println("Query Title: " + title_tokens);
		System.out.println("Query Concepts: " + concepts);
		System.out.println("Query Concepts: " + concepts_tokens);

		// score article
		HashMap<Integer, Double> article_score = new HashMap<Integer, Double>();
		score_article(article_score, title_tokens, 1.0);
		score_article(article_score, concepts_tokens, 1.0);
		
		// sort score map
		ArrayList<Map.Entry<Integer, Double>> score_list = new ArrayList<Map.Entry<Integer, Double>>(
				article_score.entrySet());
		Comparator<Map.Entry<Integer, Double>> entry_comparator = new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		};
		Collections.sort(score_list, entry_comparator);

		// relevance feedback if relevance_feedback == true
		if (this.relevance_feedback) {
			ArrayList<String> feedback_article_id_list = new ArrayList<String>();
			for (int i = 0; i < score_list.size() && i < this.feedback_article_number; i++) {
				int article_idx = score_list.get(i).getKey();
				String article_id = this.corpus.getArticleIdByIndex(article_idx);
				feedback_article_id_list.add(article_id);
			}
			
			// modify query
			System.out.println("Feedback id: " + feedback_article_id_list);
			Query modified_query = relevance_feedback(query, feedback_article_id_list);
			
			// search
			return search(modified_query, result_size);
		}

		// top score to answer_list
		ArrayList<String> answer_list = new ArrayList<String>();
		for (int i = 0; i < score_list.size() && i < result_size; i++) {
			int article_idx = score_list.get(i).getKey();
			String article_id = this.corpus.getArticleIdByIndex(article_idx);
			Double score = score_list.get(i).getValue();
			answer_list.add(article_id);
			System.out.println(String.format("%s\t%d\t%s\t%f", query.getNumber(), i, article_id, score));
		}
		
		/* 
		//print top article content
		for (int i = 0; i < 5; i++){
			System.out.println(corpus.getArticle(answer_list.get(i)).getTitle());
			System.out.println(corpus.getArticle(answer_list.get(i)).getTexts());
		}
		*/
		
		return answer_list;
	}
	
	private void score_article(HashMap<Integer, Double> article_score, ArrayList<String> query_tokens, Double weight){
		// score concept 
		for (String token : query_tokens) {
			ArrayList<Tuple<Integer, Integer>> invliest = this.corpus.getInvFileList(token);
			for (Tuple<Integer, Integer> tuple : invliest) {
				Integer article_idx = tuple.x;
				Integer token_appear = tuple.y;
				Integer total_article = corpus.getFileMap().getSize();
				Double tf = 3 + Math.log(token_appear.doubleValue());
				Double idf = Math.log(30 + total_article.doubleValue() / invliest.size());
				article_score.put(article_idx,
						article_score.getOrDefault(article_idx, 0.0) + weight*tf*idf);
			}
		}
	}
	
	private Query relevance_feedback(Query query, ArrayList<String> feedback_article_id_list) {
		// init
		Query modified_query = query.clone();

		// modify query
		String new_concepts = query.getConcepts();
		for (String article_id : feedback_article_id_list) {
			Article article = this.corpus.getArticle(article_id);
			new_concepts = new_concepts + " " + article.getTitle();
		}
		modified_query.setConcepts(new_concepts);
		
		return modified_query;
	}

}
