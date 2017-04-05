package irsystem.corpus;

import java.util.HashMap;

public class VocabMap {
	private HashMap<String, Integer> token_to_idx_map;
	private HashMap<Integer, String> idx_to_token_map;
	private int size;
	
	public VocabMap(){
		this.token_to_idx_map = new HashMap<String, Integer>();
		this.idx_to_token_map = new HashMap<Integer, String>();
		this.size = 0;
	}
	public int getSize() { return this.size;}
	
	/** if not found, return null */
	public Integer getIdx(String token) { return this.token_to_idx_map.get(token);}
	
	/** if not found, return null */
	public String getToken(int idx) { return this.idx_to_token_map.get(idx);}
	
	public void addToken(String token)
	{
		if (this.token_to_idx_map.containsKey(token)) { return;}

		this.token_to_idx_map.put(token, this.size);
		this.idx_to_token_map.put(this.size, token);
		this.size++;
	}
	
}
