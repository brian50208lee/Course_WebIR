package irsystem.corpus;

import java.util.ArrayList;
import java.util.HashMap;
import structure.Tuple;

public class InvertedFileLists {
	private HashMap<String, ArrayList<Tuple<Integer, Integer>>> inverted_list;
	
	public InvertedFileLists(){
		this.inverted_list = new HashMap<String, ArrayList<Tuple<Integer, Integer>>>();
	}
	
	public void addUnitGram(int gram_idx, int file_idx, int appear_time){
		String gram_key = Integer.toString(gram_idx);
		addGram(gram_key, file_idx, appear_time);
	}
	
	public void addBiGram(int gram1_idx, int gram2_idx, int file_idx, int appear_time){
		String gram_key = Integer.toString(gram1_idx) + "_" + Integer.toString(gram2_idx);
		addGram(gram_key, file_idx, appear_time);
		
	}
	
	public ArrayList<Tuple<Integer, Integer>> getUnitGramList(int gram_idx){
		String gram_key = Integer.toString(gram_idx);
		return inverted_list.getOrDefault(gram_key, new ArrayList<Tuple<Integer, Integer>>());
	}
	
	public ArrayList<Tuple<Integer, Integer>> getBiGramList(int gram1_idx, int gram2_idx){
		String gram_key = Integer.toString(gram1_idx) + "_" + Integer.toString(gram2_idx);
		return inverted_list.getOrDefault(gram_key, new ArrayList<Tuple<Integer, Integer>>());
	}
	
	private void addGram(String gram_key, int file_id, int appear_time){
		if (inverted_list.get(gram_key) == null) {
			inverted_list.put(gram_key, new ArrayList<Tuple<Integer, Integer>>());
		}
		inverted_list.get(gram_key).add(new Tuple<Integer, Integer>(file_id, appear_time));
	}
}
