package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.index.DiskIndexWriter;
import cecs429.index.DiskPositionalIndex;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.query.RankedRetrieval;
import cecs429.query.RankedRetrieval.DocumentScore;
import cecs429.text.TokenProcessor;
import cecs429.text.MSOneTokenProcessor;
import java.nio.file.Paths;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * 
 * Console-based Main Application
 *
 */
public class ConsoleApplication {
	public static void main(String[] args) {
		Scanner userScanner = new Scanner(System.in);
		Scanner dirScanner = new Scanner(System.in);
		System.out.print("Would you like to Enter a Path to (1) a Directory or (2) an Existing Index? Please Enter a  Number:  ");
		DocumentCorpus corpus = null;
		String dirSelection = null;
		String userSelection = userScanner.next();
		if (userSelection.equals("1")) {
			System.out.print("Enter a directory path:  ");
			dirSelection = dirScanner.next();
			corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(dirSelection).toAbsolutePath());
			Index invertedIndex = DiskIndexWriter.indexCorpus(corpus);
			DiskIndexWriter.writeIndex(invertedIndex, (dirSelection));
		} else if (userSelection.equals("2"))  {
			System.out.print("Enter the existing index path:  ");
			dirSelection = dirScanner.next();
			corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(dirSelection).toAbsolutePath());
			corpus.getDocuments();
		}
		
		DiskPositionalIndex diskIndex = new DiskPositionalIndex(dirSelection);
		Scanner inScanner = new Scanner(System.in);
		Scanner modeScanner = new Scanner(System.in);
		System.out.print("Select a query mode: \n(1) Boolean \n(2) Ranked  ");
		String modeSelection = modeScanner.next();
		boolean continueSearch = true;
		while (continueSearch){
			System.out.print("\nPlease enter term to search, or enter \"v\" for vocab: ");
			String userTerm = inScanner.nextLine();
			String query = userTerm;
			query = query.toLowerCase();
			if (!query.equals("quit") && !query.equals("v")) {
				TokenProcessor myProcessor = new MSOneTokenProcessor();
				
				if(modeSelection.equals("1")) {
					BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
					Query myQuery = booleanQueryParser.parseQuery(query, myProcessor);
					List<Posting> myQueryPostings = myQuery.getPostings(diskIndex);
					if (myQueryPostings != null){
						if (myQueryPostings.size() > 0) {
							for (Posting p : myQueryPostings) {
								System.out.println("Document " + p.getDocumentId() + ": " + corpus.getDocument(p.getDocumentId()).getTitle());
							}
							System.out.println("Number of Documents: " + myQueryPostings.size());
							System.out.println();
						}
						else {
							System.out.println("Not Found.");
						}
					}	 
					else {
						System.out.println("Not Found.");
					}
				} else if (modeSelection.equals("2")) {
					RankedRetrieval rankedRetrieval  = new RankedRetrieval();
			        PriorityQueue<DocumentScore> rankedResults = rankedRetrieval.rankQuery(corpus, diskIndex, query);
			        
			        // Display PQ
			        DocumentScore docScore = null;
			        while((docScore= rankedResults.poll()) != null) {
			            System.out.print(docScore + "\n");
			        }
				}
				
			} 
			else if (query.equals("v")) {
				for (String lString : diskIndex.getVocabulary()) {
					System.out.println(lString);
				}			
			} 
			else {
				continueSearch = false;
			}
		}
		
		dirScanner.close();
		inScanner.close();
		userScanner.close();
		modeScanner.close();
	}
}