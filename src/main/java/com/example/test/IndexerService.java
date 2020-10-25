package com.example.test;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.text.EnglishTokenStream;
import cecs429.text.MSOneTokenProcessor;
import cecs429.text.TokenProcessor;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;

/**
 * 
 * Service to help the UI index the corpus and search for queries
 *
 */
public class IndexerService {
	
	static DocumentCorpus corpus;
	static Index index;
	
	/**
	 * Run indexing the corpus
	 * @param selectedDir - User selected directory
	 * @return result - time in milliseconds it took to index the corpus
	 */
	public long run(String selectedDir) {
		StopWatch watch = new StopWatch();
 		watch.start();
 		
		corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(selectedDir).toAbsolutePath());
		index = indexCorpus(corpus);
		
		watch.stop();
 		long result = watch.getTime(); 
 		
 		return result;
	}
	
	/**
	 * Search user queries 
	 * @param query - query the user entered
	 * @return documents - list of documents that match the query
	 */
	public ArrayList<Document> search(String query){
		BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
		TokenProcessor myProcessor = new MSOneTokenProcessor();
		ArrayList<Document> documents = new ArrayList<>();
		query = query.toLowerCase();
		Query myQuery = booleanQueryParser.parseQuery(query, myProcessor);
		List<Posting> myQueryPostings = myQuery.getPostings(index);
		if (myQueryPostings != null){
			if (myQueryPostings.size() > 0) {
				for (Posting p : myQueryPostings) {
					documents.add(corpus.getDocument(p.getDocumentId()));
				}
			} 
		}

		return documents;
	} 
	
	/**
	 * Return positional index for a given corpus
	 * @param corpus - corpus to-be indexed
	 * @return positionalInvertedIndex - positional index returned
	 */
	public static Index indexCorpus(DocumentCorpus corpus) {
		Iterable<Document> allDocs = corpus.getDocuments();
		TokenProcessor processor = new MSOneTokenProcessor();

		PositionalInvertedIndex positionalInvertedIndex = new PositionalInvertedIndex();
		for (Document lDoc : allDocs) {
			EnglishTokenStream eStream = new EnglishTokenStream(lDoc.getContent());
			//process into Tokens
			Iterable<String> eTokens = eStream.getTokens();
			//Make into arraylist to track position
			ArrayList<String> tokenList = new ArrayList<>();
			for (String lString : eTokens) {
				tokenList.add(lString);						
			}
			//Starting from the first word, going to the last
			for (int i = 0; i < tokenList.size(); i++){
				List<String> processedTokens = new ArrayList<>();
				//The current term
				String currentTerm = tokenList.get(i);
				List<String> processedStrings = processor.processToken(currentTerm);
				for (String lToken : processedStrings) {
					processedTokens.add(lToken);
				}				
				for (String proToken: processedTokens) {
					//Get list of positions if it exists
					List<Posting> existingPostings = positionalInvertedIndex.getBooleanPostings(proToken);					
					//If it already exists
					if (existingPostings != null){
						Posting lastPosting = existingPostings.get(existingPostings.size()-1);
						if (lastPosting.getDocumentId() != lDoc.getId()){
							List<Integer> lPositions = new ArrayList<>();
							Posting lPosting = new Posting(lDoc.getId(), lPositions);
							lPosting.addPosition(i);
							positionalInvertedIndex.addTerm(proToken, lPosting);
						}
						else{
							lastPosting.addPosition(i);
						}
					}
					//If it does not yet have an existing list
					else{
						//Create a new list, add the index
						List<Integer> lPositions = new ArrayList<>();
						Posting lPosting = new Posting(lDoc.getId(), lPositions);
						lPosting.addPosition(i);
						//and put as a new entry to the map
						positionalInvertedIndex.addTerm(proToken, lPosting);
					}
				}
				
			}			
		}
		
		return positionalInvertedIndex;
	}
	
	/**
	 * Return the vocab list for the index
	 * @return vocabList - vocab list for the index
	 */
	public ArrayList<String> getVocab() {
		ArrayList<String> vocabList = new ArrayList<>();
		for (String lString : index.getVocabulary()) {
			vocabList.add(lString);
		}
		return vocabList;
	}
}
