package irsystem.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import irsystem.corpus.Article;
import irsystem.corpus.FileMap;
import irsystem.corpus.InvertedFileLists;
import irsystem.corpus.VocabMap;
import irsystem.query.Query;

public class Parser {
	public static ArrayList<Query> parse_queries(String file_path) {
		// init
		ArrayList<Query> queries = new ArrayList<Query>();

		// get file content
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				content.append(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// parse query
		ArrayList<String> topics = extract_from_tag(content.toString(), "topic");
		for (String topic : topics) {
			Query query = new Query();

			ArrayList<String> number = extract_from_tag(topic, "number");
			ArrayList<String> title = extract_from_tag(topic, "title");
			ArrayList<String> question = extract_from_tag(topic, "question");
			ArrayList<String> narrative = extract_from_tag(topic, "narrative");
			ArrayList<String> concepts = extract_from_tag(topic, "concepts");

			query.setNumber(number.size() > 0 ? number.get(0) : "");
			query.setTitle(title.size() > 0 ? title.get(0) : "");
			query.setQuestion(question.size() > 0 ? question.get(0) : "");
			query.setNarrative(narrative.size() > 0 ? narrative.get(0) : "");
			query.setConcepts(concepts.size() > 0 ? concepts.get(0) : "");

			queries.add(query);
		}

		return queries;
	}

	public static Article parse_article(String file_path) {
		// init
		Article article = new Article();

		// get file content
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				content.append(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// parse article
		String article_content = content.toString();
		ArrayList<String> id = extract_from_tag(article_content, "id");
		ArrayList<String> date = extract_from_tag(article_content, "date");
		ArrayList<String> title = extract_from_tag(article_content, "title");
		ArrayList<String> texts_block = extract_from_tag(article_content, "text");
		ArrayList<String> texts = new ArrayList<String>();
		if (texts_block.size() > 0) {
			texts = extract_from_tag(texts_block.get(0), "p");
		}
		article.setId(id.size() > 0 ? id.get(0) : "");
		article.setDate(date.size() > 0 ? date.get(0) : "");
		article.setTitle(title.size() > 0 ? title.get(0) : "");
		for (String text : texts) {
			article.addText(text);
		}
		return article;
	}

	public static VocabMap parse_vocab(String file_path) {
		// init
		VocabMap vocavMap = new VocabMap();

		// get file content
		try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String token = line;
				vocavMap.addToken(token);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return vocavMap;
	}

	public static FileMap parse_filelist(String file_path) {
		FileMap fileMap = new FileMap();
		try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String article_path = line;
				fileMap.addPath(article_path);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileMap;
	}

	public static InvertedFileLists parse_inverted_filelists(String file_path) {
		// init
		InvertedFileLists invertedFileLists = new InvertedFileLists();

		int count = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (++count % 1000000 == 0) {
					System.out.println("Parse InvList: " + count);
				}
				String digit[] = line.split("\\s+");
				int gram1_idx = Integer.parseInt(digit[0]);
				int gram2_idx = Integer.parseInt(digit[1]);
				int file_number = Integer.parseInt(digit[2]);
				for (int i = 0; i < file_number; i++) {
					String tuple[] = br.readLine().split("\\s+");
					if (++count % 1000000 == 0) {
						System.out.println("Parse InvList: " + count);
					}
					int file_idx = Integer.parseInt(tuple[0]);
					int appear_time = Integer.parseInt(tuple[1]);
					if (gram2_idx == -1) {
						invertedFileLists.addUnitGram(gram1_idx, file_idx, appear_time);
					} else {
						invertedFileLists.addBiGram(gram1_idx, gram2_idx, file_idx, appear_time);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return invertedFileLists;
	}

	public static ArrayList<String> parse_segmentation(String text, int gram_number, HashSet<String> ignore_tokens) {
		if (ignore_tokens == null) {
			ignore_tokens = new HashSet<String>();
		}

		ArrayList<String> ngram_list = new ArrayList<String>();

		// ignore token
		for (String token : ignore_tokens) {
			text = text.replaceAll(token, "<split>");
		}
		
		// parse to ngram
		for (String m_text : text.split("<split>")) {
			List<String> tokens = parse_ngram(m_text, gram_number);
			
			for (String token : tokens){
				if (tokenize(token).size() > gram_number){
					ngram_list.addAll(parse_ngram(token, gram_number));
				}else{
					ngram_list.add(token);
				}
			}
		}
	
		// parse ascii
		text = convert_to_halfwidth(text);
		for (String m_text : text.split("<split>")) {
			List<String> tokens = tokenize(m_text);
			for (String token : tokens){
				if (token.matches("\\A\\p{ASCII}*\\z")) {ngram_list.add(token);}
			}
		}
		
		
		
		return ngram_list;
	}
	
	private static ArrayList<String> parse_ngram(String text, int gram_number){
		ArrayList<String> ngram_list = new ArrayList<String>();
		
		ArrayList<String> tokenList = tokenize(text);
		for (int tok_index = 0; tok_index + gram_number <= tokenList.size(); tok_index++) {
			String ngram = "";
			for(int gram_index = 0; gram_index < gram_number; gram_index++){
				ngram += tokenList.get(tok_index + gram_index) + " ";
			}
			ngram_list.add(ngram.trim());
		}
		return ngram_list;
	}

	private static ArrayList<String> extract_from_tag(String text, String tag) {
		ArrayList<String> matchs = new ArrayList<String>();
		String strTag = String.format("<%s>", tag);
		String endTag = String.format("</%s>", tag);
		String regexp = String.format("%s(.+?)%s", strTag, endTag);

		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			matchs.add(matcher.group(1));
		}
		return matchs;
	}

	public static String convert_to_halfwidth(String text) {
		String helfwidth = text;
		for (char c : helfwidth.toCharArray()) {
			helfwidth = helfwidth.replaceAll("　", " ");
			int new_c = c - 65248; // diff ascii
			if (126 >= new_c && new_c >= 32) { // all printable ascii char from
												// 32'(space)' to 126'~'
				helfwidth = helfwidth.replace(c, (char) new_c);
			}
		}
		return helfwidth;
	}

	public static ArrayList<String> tokenize(String text) {
		ArrayList<String> tokenList = new ArrayList<String>();

		String batchs[] = text.split("\\s+");
		for (String batch : batchs) {
			String token = "";
			for (char c : batch.toCharArray()) {
				if (('9' >= c && c >= '0') || ('z' >= c && c >= 'a') || ('Z' >= c && c >= 'A')) {
					token += c;
				} else {
					if (token.length() > 0) {
						tokenList.add(token);
						token = "";
					}
					tokenList.add(c + "");
				}
			}
			if (token.length() > 0) {
				tokenList.add(token);
			}
		}

		return tokenList;
	}
}
