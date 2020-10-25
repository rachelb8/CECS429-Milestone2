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

		List<Integer> temp1 = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++) { temp1.add(list1.get(i).getDocumentId()); }

		List<Integer> temp2 = new ArrayList<>();
		for (int i = 0; i < list2.size(); i++) { temp2.add(list2.get(i).getDocumentId()); }

		for (int n : temp1) {
			if (temp2.contains(n)) {
				Posting comparePosting = list2.get(temp2.indexOf(n));
				for (int x : list1.get(temp1.indexOf(n)).getPositions()) {
					if (comparePosting.getPositions().contains(x + posCheck)) {
						if (!result.contains(list1.get(temp1.indexOf(n)))) {
							result.add(list1.get(temp1.indexOf(n)));
						}
					}
				}
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