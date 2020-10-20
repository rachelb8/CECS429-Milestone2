package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An OrQuery composes other Query objects and merges their postings with a union-type operation.
 */
public class OrQuery implements Query {
	// The components of the OR query.
	private List<Query> mChildren;
	
	public OrQuery(Iterable<Query> children) {
		mChildren = new ArrayList<>();
		for (Query lquery : children) {
			mChildren.add(lquery);
		}
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = new ArrayList<>();
		List<Posting> tempPostings = new ArrayList<>();
		List<Integer> tempResults = new ArrayList<>();

		for (Query q : mChildren) {
			if (q.getPostings(index) != null) {
				for (Posting p : q.getPostings(index)) {
					tempPostings.add(p);
				}
			}
		}

		for (Posting p : tempPostings) {
			if (!tempResults.contains(p.getDocumentId())) {
				tempResults.add(p.getDocumentId());
				result.add(p);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" + String.join(" + ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList())) + " )";
	}

	@Override
	public Boolean isPositive() {
		return true;
	}
}