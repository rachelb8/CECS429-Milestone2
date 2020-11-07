package cecs429.query;

import java.util.ArrayList;
import java.util.List;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;

/**
 * A NearLiteral represents a positional merge of 2 terms
 * where the 2nd term is at most k positions away from the 1st term
 */
public class NearLiteral implements Query {

    private String mTerm;
    private TokenProcessor mTokenProcessor;

    //private List<Posting> term1Postings = new ArrayList<>();
    //private List<Posting> term2Postings = new ArrayList<>();
    //private int k;

    public NearLiteral(String term, TokenProcessor processor) {
        mTerm = term;
        mTokenProcessor = processor;
    }

    /**
	 * Aids in the intersection of two lists and performs a positional merge
	 */
	public List<Posting> intersection(List<Posting> list1, List<Posting> list2, int posCheck) {
		List<Posting> result = new ArrayList<>();
		
		int listPtr1 = 0;
		int listPtr2 = 0;
		while (list1 != null && list2 != null) {
			if (listPtr1 == list1.size()-1 || listPtr2 == list2.size()-1) {
				break;
			} else if (list1.get(listPtr1).getDocumentId() == list2.get(listPtr2).getDocumentId()) {
				int posPtr1 = 0;
				int posPtr2 = 0;
				while (list1.get(listPtr1).getPositions() != null && list2.get(listPtr2).getPositions() != null) {
					if (posPtr1 == list1.get(listPtr1).getPositions().size()-1 || posPtr2 == list2.get(listPtr2).getPositions().size()-1) {
						++listPtr1;
						++listPtr2;
						break;
					} else if (list1.get(listPtr1).getPositions().get(posPtr1) + posCheck == list2.get(listPtr2).getPositions().get(posPtr2)) {
						result.add(list1.get(listPtr1));
						++listPtr1;
						++listPtr2;
						break;
					} else if (list1.get(listPtr1).getPositions().get(posPtr1) < list2.get(listPtr2).getPositions().get(posPtr2)) {
						++posPtr1;
					} else {
						++posPtr2;
					}
				}
			} else if (list1.get(listPtr1).getDocumentId() < list2.get(listPtr2).getDocumentId()) {
				++listPtr1;
			} else {
				++listPtr2;
			}
		}
		return result;
	}

    @Override
    public List<Posting> getPostings(Index index) {

        // Format the near literal to seperate the 1st term, k and 2nd term -----------------------
        int subLength = mTerm.length();
        int lengthOut;
        int startIndex = 0;

        List<Posting> term1Postings = new ArrayList<>();
        int k;
        List<Posting> term2Postings = new ArrayList<>();

        // Check if first term is a phrase literal ------------------------------------------------
        if (mTerm.charAt(startIndex) == '"') {
            ++startIndex;
            int nextQuote = mTerm.indexOf('"', startIndex);
            if (nextQuote < 0) {
                lengthOut = subLength - startIndex;
            } else {
                lengthOut = nextQuote - startIndex;
            }
            PhraseLiteral myPhraseLiteral = new PhraseLiteral(mTerm.substring(startIndex, startIndex + lengthOut), mTokenProcessor);
            term1Postings = myPhraseLiteral.getPostings(index);
            startIndex = lengthOut + 2;
            // Else it is a term literal
        } else {
            int nextSpace = mTerm.indexOf(' ', startIndex);
            if (nextSpace < 0) {
                lengthOut = subLength - startIndex;
            } else {
                lengthOut = nextSpace - startIndex;
            }
            TermLiteral myTermLiteral = new TermLiteral(mTerm.substring(startIndex, startIndex + lengthOut), mTokenProcessor);
            term1Postings = myTermLiteral.getPostings(index);
            startIndex += mTerm.substring(startIndex, startIndex + lengthOut).length();
        }
        // Converts the k to an int
        // ---------------------------------------------------------------
        while (mTerm.charAt(startIndex) == ' ') {
            ++startIndex;
        }
        int nextSpaceAfterK = mTerm.indexOf(' ', startIndex);
        if (nextSpaceAfterK < 0) {
            lengthOut = subLength - startIndex;
        } else {
            lengthOut = nextSpaceAfterK - startIndex;
        }
        k = Integer.parseInt(mTerm.substring(startIndex, startIndex + lengthOut));
        startIndex += mTerm.substring(startIndex, startIndex + lengthOut).length();

        // Check if second term is a phrase literal
        // -----------------------------------------------
        while (mTerm.charAt(startIndex) == ' ') {
            ++startIndex;
        }
        if (mTerm.charAt(startIndex) == '"') {
            ++startIndex;
            int nextQuote = mTerm.indexOf('"', startIndex);
            if (nextQuote < 0) {
                lengthOut = subLength - startIndex;
            } else {
                lengthOut = nextQuote - startIndex;
            }
            PhraseLiteral myPhraseLiteral = new PhraseLiteral(mTerm.substring(startIndex, startIndex + lengthOut), mTokenProcessor);
            term2Postings = myPhraseLiteral.getPostings(index);
            // Else it is a term literal
        } else {
            int nextSpace = mTerm.indexOf(' ', startIndex);
            if (nextSpace < 0) {
                lengthOut = subLength - startIndex;
            } else {
                lengthOut = nextSpace - startIndex;
            }
            TermLiteral myTermLiteral = new TermLiteral(mTerm.substring(startIndex, startIndex + lengthOut), mTokenProcessor);
            term2Postings = myTermLiteral.getPostings(index);
        }
        // ----------------------------------------------------------------------------------------

        List<Posting> result = new ArrayList<>();
        if (term1Postings == null || term2Postings == null) {
            return result;
        } else {
            result = intersection(term1Postings, term2Postings, k);
        }
        return result;
    }

    @Override
    public Boolean isPositive() {
        return true;
    }
}