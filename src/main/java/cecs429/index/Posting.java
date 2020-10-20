package cecs429.index;

import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private List<Integer> mPositions;

	public Posting(int documentId, List<Integer> positionArg) {
		mDocumentId = documentId;
		mPositions = positionArg;
	}

	public void addPosition(int positionArg){
		mPositions.add(positionArg);
	}	

	public int getDocumentId() {
		return mDocumentId;
	}

	public List<Integer> getPositions() {
		return mPositions;
	}
}
