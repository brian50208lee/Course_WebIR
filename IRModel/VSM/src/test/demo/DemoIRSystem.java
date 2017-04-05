package test.demo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import irsystem.corpus.Corpus;
import irsystem.model.VSM;
import irsystem.parser.Parser;
import irsystem.query.Query;

public class DemoIRSystem {
	private static boolean relevance_feedback = false; // -r
	private static String query_file = "/Users/selab/git/Course_WebIR/Data/queries/query-test.xml"; // -i
	private static String ranked_list = "/Users/selab/git/Course_WebIR/MAP/ans.csv"; // -o
	private static String model_dir = "/Users/selab/git/Course_WebIR/Data/model"; // -m
	private static String NTCIR_dir = "/Users/selab/git/Course_WebIR/Data/CIRB010"; // -d

	public static void main(String[] args) {
		System.out.println("Argument:" + Arrays.asList(args));
		// parse arguments
		for(int i = 0; i < args.length; i++){
			if (args[i].equals("-r")) {relevance_feedback = true;}
			else if (args[i].equals("-i")) {query_file = args[++i];}
			else if (args[i].equals("-o")) {ranked_list = args[++i];}
			else if (args[i].equals("-m")) {model_dir = args[++i];}
			else if (args[i].equals("-d")) {NTCIR_dir = args[++i];}
			else {
				System.err.println("Exit => Unknow Argument:" + args[i]);
				System.exit(1);
			}
		}
		
		// load corpus
		Corpus corpus = new Corpus(model_dir, NTCIR_dir);
		
		// build model
		VSM vsm = new VSM(corpus);
		vsm.setRelevanceFeedback(relevance_feedback);
		vsm.setFeedbackArticleNumber(1);

		// test queries
		ArrayList<Query> queries = Parser.parse_queries(query_file);
		outputMessage("query_id,retrieved_docs\n", ranked_list, false);
		for (Query query : queries) {			
			// parse query id
			String queryNumber = query.getNumber();
			queryNumber = queryNumber.substring(queryNumber.length()-3, queryNumber.length());
			int query_id = Integer.parseInt(queryNumber);
			
			// search
			ArrayList<String> ans = vsm.search(query, 100);
			
			// create output line
			String mes = query_id + ",";
			for (String file_id : ans) {mes += file_id.toLowerCase() + " ";}
			mes = mes.trim() + "\n";
			
			// output
			outputMessage(mes, ranked_list, true);
		}
	}

	public static void outputMessage(String mes, String ofile, boolean append) {
		try(BufferedWriter br = new BufferedWriter(new FileWriter(ofile, append))){
			br.write(mes);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
