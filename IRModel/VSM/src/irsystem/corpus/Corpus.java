package irsystem.corpus;

import java.io.File;
import java.util.ArrayList;

import irsystem.parser.Parser;
import structure.Tuple;

public class Corpus {

	//private HashMap<String, Article> articles;
	private FileMap fileMap;
	private VocabMap vocabMap;
	private InvertedFileLists invertedFileLists;
	private String NTCIR_dir;
	
	public Corpus(String model_dir, String NTCIR_dir) {
		//this.articles = new HashMap<String,Article>();
		this.fileMap = Parser.parse_filelist(model_dir + File.separator +"file-list" );
		this.vocabMap = Parser.parse_vocab(model_dir + File.separator + "vocab.all");
		this.invertedFileLists = Parser.parse_inverted_filelists(model_dir + File.separator + "inverted-file");
		this.NTCIR_dir = NTCIR_dir;
	}

	public FileMap getFileMap() {return this.fileMap;}
	public VocabMap getVocabMap() {return this.vocabMap;}
	public InvertedFileLists getInvertedFileLists() {return this.invertedFileLists;}
	
	public Article getArticle(String id){
		String article_path_tokens[] = this.fileMap.id_to_path(id).split(File.separator);
		String article_path = this.NTCIR_dir;
		for (int token_index = 1; token_index < article_path_tokens.length; token_index++){
			article_path = article_path + File.separator + article_path_tokens[token_index];
		}
		Article article = Parser.parse_article(article_path);
		return article;
	}
	
	public Article getArticle(Integer index){
		String article_id = this.fileMap.index_to_id(index);
		return getArticle(article_id);
	}

	public Integer getVocabIndex(String token) {
		return this.vocabMap.getIdx(token);
	}

	public Integer getArticleIndexById(String article_id) {
		return this.fileMap.id_to_index(article_id);
	}

	public String getArticleIdByIndex(Integer article_index) {
		return this.fileMap.index_to_id(article_index);
	}

	public ArrayList<Tuple<Integer, Integer>> getInvFileList(String tokens) {
		// init
		ArrayList<Tuple<Integer, Integer>> invFileList = new ArrayList<Tuple<Integer, Integer>>();
		
		// tokenize
		ArrayList<String> tokenList = Parser.tokenize(tokens);
		
		// get inverted file list 
		if (tokenList.size() == 1) { // unitgram
			Integer gram_idx = this.vocabMap.getIdx(tokenList.get(0));
			if (gram_idx != null) { 
				invFileList = this.invertedFileLists.getUnitGramList(gram_idx);
			}
		}else if (tokenList.size() == 2){ // bigram
			Integer gram1_idx = this.vocabMap.getIdx(tokenList.get(0));
			Integer gram2_idx = this.vocabMap.getIdx(tokenList.get(1));
			if (gram1_idx != null && gram2_idx != null) { 
				invFileList = this.invertedFileLists.getBiGramList(gram1_idx, gram2_idx);
			}
		}else { // can't handle
			
		}
		//System.out.println(tokens + "\t" + invFileList);
		if (invFileList.size() == 0){
			System.out.printf("Tokens(%s) not in corpus size:%d\n", tokens, tokenList.size());
		}
		return invFileList;
	}
	
	
}
