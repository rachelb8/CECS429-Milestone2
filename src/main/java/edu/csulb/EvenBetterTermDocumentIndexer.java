package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.index.DiskIndexWriter;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.text.TokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.MSOneTokenProcessor;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * Console-based Main Application
 *
 */
public class EvenBetterTermDocumentIndexer {
	public static void main(String[] args) {
		Scanner dirScanner = new Scanner(System.in);
		System.out.print("Please enter a directory: ");
		String dirSelection = dirScanner.next();
		DocumentCorpus corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(dirSelection).toAbsolutePath());
		Index invertedIndex = indexCorpus(corpus);
		BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
		DiskIndexWriter.writeIndex(invertedIndex, (dirSelection + "\\index"));
		Scanner inScanner = new Scanner(System.in);
		boolean continueSearch = true;
		while (continueSearch){
			System.out.print("Please enter term to search, or enter \"v\" for vocab: ");
			String userTerm = inScanner.nextLine();
			String query = userTerm;
			query = query.toLowerCase();
			if (!query.equals("quit") && !query.equals("v")) {
				TokenProcessor myProcessor = new MSOneTokenProcessor();
				Query myQuery = booleanQueryParser.parseQuery(query, myProcessor);
				List<Posting> myQueryPostings = myQuery.getPostings(invertedIndex);
				if (myQueryPostings != null){
					if (myQueryPostings.size() > 0) {
						for (Posting p : myQueryPostings) {
							System.out.println("Document " + p.getDocumentId() + ": " + corpus.getDocument(p.getDocumentId()).getTitle());
						}
						System.out.println();
					}
					else {
						System.out.println("Not Found.");
					}
				}	 
				else {
					System.out.println("Not Found.");
				}
			} 
			else if (query.equals("v")) {
				for (String lString : invertedIndex.getVocabulary()) {
					System.out.println(lString);
				}			
			} 
			else {
				continueSearch = false;
			}
		}
		dirScanner.close();
		inScanner.close();
	}
	
	private static Index indexCorpus(DocumentCorpus corpus) {
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

	
}