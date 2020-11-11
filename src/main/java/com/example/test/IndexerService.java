package com.example.test;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.DiskIndexWriter;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.query.RankedRetrieval;
import cecs429.query.RankedRetrieval.DocumentScore;
import cecs429.text.MSOneTokenProcessor;
import cecs429.text.TokenProcessor;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.lang3.time.StopWatch;

/**
 * 
 * Service to help the UI index the corpus and search for queries
 *
 */
public class IndexerService {
	
	DocumentCorpus corpus;
	DiskPositionalIndex diskIndex;
	static Index index;
	
	/**
	 * Run indexing a new corpus
	 * @param selectedDir - User selected directory
	 * @return result - time in milliseconds it took to index the corpus
	 */
	public long runNew(String selectedDir) {
		StopWatch watch = new StopWatch();
 		watch.start();
 		
 		corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(selectedDir).toAbsolutePath());
		index = DiskIndexWriter.indexCorpus(corpus);
		DiskIndexWriter.writeIndex(index, (selectedDir));
		diskIndex = new DiskPositionalIndex((selectedDir));
		
		watch.stop();
 		long result = watch.getTime(); 
 		
 		return result;
	}
	
	/**
	 * Run loading existing corpus
	 * @param selectedDir - User selected directory
	 * @return result - time in milliseconds it took to load the corpus
	 */
	public long runExisting(String selectedDir) {
		StopWatch watch = new StopWatch();
 		watch.start();
 		
 		corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(selectedDir).toAbsolutePath());
		corpus.getDocuments();
		diskIndex = new DiskPositionalIndex(selectedDir);
		
		watch.stop();
 		long result = watch.getTime(); 
 		
 		return result;
	}
	
	/**
	 * Search user queries in Boolean Mode 
	 * @param query - query the user entered
	 * @return documents - list of documents that match the query
	 */
	public ArrayList<Document> searchBoolean(String query){
		BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
		TokenProcessor myProcessor = new MSOneTokenProcessor();
		ArrayList<Document> documents = new ArrayList<>();
		query = query.toLowerCase();
		Query myQuery = booleanQueryParser.parseQuery(query, myProcessor);
		List<Posting> myQueryPostings = myQuery.getPostings(diskIndex);
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
	 * Search user queries in Ranked Mode 
	 * @param query - query the user entered
	 * @return documents - list of documents that match the query
	 */
	public PriorityQueue<DocumentScore> searchRanked(String query){
		RankedRetrieval rankedRetrieval  = new RankedRetrieval();
        PriorityQueue<DocumentScore> rankedResults = rankedRetrieval.rankQuery(corpus, diskIndex, query);

		return rankedResults;
	} 
	
	/**
	 * Return the vocab list for the index
	 * @return vocabList - vocab list for the index
	 */
	public ArrayList<String> getVocab() {
		ArrayList<String> vocabList = new ArrayList<>();
		for (String lString : diskIndex.getVocabulary()) {
			vocabList.add(lString);
		}
		return vocabList;
	}
}
