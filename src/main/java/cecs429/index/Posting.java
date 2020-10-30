package cecs429.index;

import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private List<Integer> mPositions;
	private int mTermFreq; 
	private double mDocScore;

	public Posting(int documentId, List<Integer> positionArg) {
		mDocumentId = documentId;
		mPositions = positionArg;
	}

	public Posting(int documentId, int termFreq, double docScore){
		mDocumentId = documentId;
		mTermFreq = termFreq;
		mDocScore = docScore;
	}

	public void addPosition(int positionArg){
		mPositions.add(positionArg);
	}	

	public int getDocumentId() {
		return mDocumentId;
	}

	public int getTermFreq(){
		return mTermFreq;
	}
	
	public double getDocScore(){
		return mDocScore;
	}

	public List<Integer> getPositions() {
		return mPositions;
	}
}
