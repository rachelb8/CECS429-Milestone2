package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

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

		for (Query q : mChildren) {
            List<Posting> tempPostings = q.getPostings(index);
			if (tempPostings != null) {
				for (Posting p : tempPostings) {
					result.add(p);
				}
			}
		}
		result = result.stream().distinct().collect(Collectors.toList());
		result.sort(Comparator.comparing(Posting::getDocumentId));
		return result;
    }

    @Override
    public Boolean isPositive() {
        return false;
    }
}