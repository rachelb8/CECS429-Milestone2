package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;

import java.util.List;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements Query {
	private String mTerm;
	private TokenProcessor mTokenProcessor;
	
	public TermLiteral(String term, TokenProcessor processor) {
		mTokenProcessor = processor;
		mTerm = mTokenProcessor.processToken(term).get(0);
	}
	
	public String getTerm() {
		return mTerm;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		return index.getBooleanPostings(mTerm);
	}
	
	@Override
	public String toString() {
		return mTerm;
	}

	@Override
	public Boolean isPositive() {
		return true;
	}

}
