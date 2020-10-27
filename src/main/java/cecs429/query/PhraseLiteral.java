package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements Query {
	// The list of individual terms in the phrase.
	private List<String> mTerms = new ArrayList<>();
	private TokenProcessor mTokenProcessor;
	
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
	 */
	public PhraseLiteral(List<String> terms, TokenProcessor processor) {
		mTokenProcessor = processor;
		for (String s : terms) {
			mTerms.add(mTokenProcessor.processToken(s).get(0));
		}
	}
	
	/**
	 * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
	 */
	public PhraseLiteral(String terms, TokenProcessor processor) {
		mTokenProcessor = processor;
		for (String s : Arrays.asList(terms.split(" "))) {
			mTerms.add(mTokenProcessor.processToken(s).get(0));
		}
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
		List<Posting> result = new ArrayList<>();

		for (int i = 0; i < mTerms.size(); i++) {
			if (index.getBooleanPostings(mTerms.get(i)) == null) {
				return result;
			}
		}
		int posCheck = 1;
		result = index.getBooleanPostings(mTerms.get(0));
		for (int i = 1; i < mTerms.size(); i++) {
			result = intersection(result, index.getBooleanPostings(mTerms.get(i)), posCheck);
			++posCheck;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}
	
	@Override
	public Boolean isPositive() {
		return true;
	}
}