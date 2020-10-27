package cecs429.index;

import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private List<Integer> mPositions;
	private int mTermFreq; 

	public Posting(int documentId, List<Integer> positionArg) {
		mDocumentId = documentId;
		mPositions = positionArg;
	}

	public Posting(int documentId, int termFreq){
		mDocumentId = documentId;
		mTermFreq = termFreq;
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

	public List<Integer> getPositions() {
		return mPositions;
	}

	// Helps distinguish postings from one another for comparing
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mDocumentId;
		return result;
	}

	// Determines whether Posting objects are equal to each other
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		Posting other = (Posting) obj;
		if (mDocumentId != other.mDocumentId) { return false; }
		return true;
	}
}
