package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.List;
import java.util.ArrayList;

/**
 * A NotQuery composes other Query objects and merges their postings in an union-like operation.
 */
public class NotQuery implements Query {
    // The components of the NOT query
    private List<Query> mChildren;

    public NotQuery(Iterable<Query> children) {
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
    public Boolean isPositive() {
        return false;
    }
}