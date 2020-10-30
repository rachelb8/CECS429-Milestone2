package cecs429.query;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import cecs429.documents.DocumentCorpus;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;
import cecs429.text.MSOneTokenProcessor;

public class RankedRetrieval {
	

	 //1. For each term in the query        
    //  a. Calculate Wq,t = ln (1 + (N / dft)) N - size of corpus, dft - number of documents that contain the term t
    //  b. For each document d in t' posting list
    //      1 - Aquire an accumulater value A_d (the design of this system is up to you)
    //      2 - Calculate W_d,t = 1 + ln(tf_t,d)
    //      3 - Increase A_d by w_d,t x w_q,t
    //2. For each non-zero A_d, Divide A_d by L_d, where L_d is read from the docWeights.bin file
    //3. Select and return the top K = 10 documents by largest A_d value. 
    //      (Use a binary heap priority queue to select the largest results; )
    //      (Do not sort the accumulators)
	
	DocumentCorpus corpusLocal;
    public PriorityQueue<DocumentScore> rankQuery(DocumentCorpus corpus, DiskPositionalIndex diskIndex, String queryString){
    	corpusLocal = corpus;
    	MSOneTokenProcessor mTokenProcessor = new MSOneTokenProcessor();
        String[] queryWords = queryString.split(" ");
        List<DocumentScore> accumulator = new ArrayList<>();
        for (int i = 0; i < queryWords.length; i++){
        	List<String> processedQueries = mTokenProcessor.processToken(queryWords[i]);
        	List<Posting> postings = new ArrayList<Posting>();
        	for (String queryTerm: processedQueries) {
        		postings.addAll(diskIndex.getRankedPostings(queryTerm)); 
        	}
        	 
        	Integer N = corpus.getCorpusSize();
        	Integer dft = postings.size();
        	Double wqt = 0.0; 
        	if (dft != 0) {
        		wqt = Math.log(1 + (N/dft));
        	}
        	
            for (Posting p: postings) {
            	Integer docID = p.getDocumentId();
            	Double wdt = p.getDocScore();
            	DocumentScore docScore = accumulator.stream().filter(doc -> docID.equals(doc.getDocID())).findFirst().orElse(null);
            	if(docScore != null) {
            		Double originalScore = docScore.getScore();
            		docScore.setScore(originalScore + wdt);
            	} else {
            		accumulator.add(new DocumentScore(docID, wdt * wqt));
            	}
            }
        }
        
        PriorityQueue<DocumentScore> pq = new PriorityQueue<DocumentScore>();
        
        for (DocumentScore docScore: accumulator) {
        	Double Ld = diskIndex.getDocWeight(docScore.getDocID());
        	if (docScore.getScore() != 0.0) {
        		Double originalScore = docScore.getScore();
        		docScore.setScore(originalScore/Ld);
        	}
        	pq.add(docScore);
        }
        
        //TODO - Only return top 10 documents
        return pq;
    }

	public class DocumentScore implements Comparable<DocumentScore> {
        private Integer  docID;
        private Double score;

        public DocumentScore(Integer docID, Double score) {
            this.setDocID(docID);
            this.setScore(score);
        }

		public Integer getDocID() {
			return docID;
		}

		public void setDocID(Integer docID) {
			this.docID = docID;
		}

		public Double getScore() {
			return score;
		}

		public void setScore(Double score) {
			this.score = score;
		}
		
		@Override
        public int compareTo(DocumentScore other) {
            return other.getScore().compareTo(this.getScore());
        }
		
		public String toString() {
			return String.format("%d %s (Accumulator Value: %.5f)" , docID, corpusLocal.getDocument(docID).getTitle(), score);
		}
    }
}
