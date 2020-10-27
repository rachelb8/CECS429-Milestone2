package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * An AndQuery composes other Query objects and merges their postings in an intersection-like operation.
 */
public class AndQuery implements Query {
	// The components of the AND query
	private List<Query> mChildren;
	
	public AndQuery(Iterable<Query> children) {
		mChildren = new ArrayList<>();
		for (Query lquery : children) {
			mChildren.add(lquery);
		}
	}

	/**
	 * Aids in the intersection of two lists
	 */
	 public List<Posting> intersection(List<Posting> list1, List<Posting> list2) {
		List<Posting> result = new ArrayList<>();
		List<Integer> temp1 = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++) { temp1.add(list1.get(i).getDocumentId()); }
		List<Integer> temp2 = new ArrayList<>();
		for (int i = 0; i < list2.size(); i++) { temp2.add(list2.get(i).getDocumentId()); }
		for (int n : temp1) {
			if (temp2.contains(n)) {
				result.add(list1.get(temp1.indexOf(n)));
			}	
		}
		return result;
	}

	public List<Posting> betterIntersection(List<Posting> list1, List<Posting> list2) {
		List<Posting> result = new ArrayList<>();

		int listPtr1 = 0;
		int listPtr2 = 0;
		while (list1 != null && list2 != null) {
			if (listPtr1 == list1.size()-1 || listPtr2 == list2.size()-1) {
				break;
			} else if (list1.get(listPtr1).getDocumentId() == (list2.get(listPtr2).getDocumentId())) {
				result.add(list1.get(listPtr1));
				++listPtr1;
				++listPtr2;
			} else if (list1.get(listPtr1).getDocumentId() < list2.get(listPtr2).getDocumentId()) {
				++listPtr1;
			} else {
				++listPtr2;
			}
		}
		return result;
	}

	/**
	 * Aids in the interesection of an AND NOT query
	 * */
	public List<Posting> intersectionNOT(List<Posting> list1, List<Posting> list2) {
		List<Posting> result = new ArrayList<>();
		List<Integer> temp1 = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++) { temp1.add(list1.get(i).getDocumentId()); }

		List<Integer> temp2 = new ArrayList<>();
		for (int i = 0; i < list2.size(); i++) { temp2.add(list2.get(i).getDocumentId()); }

		for (int n : temp1) {
			if (!temp2.contains(n)) {
				if (result.contains(list1.get(temp1.indexOf(n)))) {
					result.remove(list1.get(temp1.indexOf(n)));
				} else {
					result.add(list1.get(temp1.indexOf(n)));
				}
			}
		}
		return result;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = new ArrayList<>();
		
		for (Query q : mChildren) {
			if (q.getPostings(index) == null) {
				return result;
			}
		}

		List<Query> posQueries = new ArrayList<>();
		List<Query> negQueries = new ArrayList<>();
		for (Query checkQ : mChildren) {
			if (!checkQ.isPositive()) {
				negQueries.add(checkQ);
			} else {
				posQueries.add(checkQ);
			}
		}

		if (!negQueries.isEmpty()) {
			NotQuery notQuery = new NotQuery(negQueries);
			List<Posting> negPostings = notQuery.getPostings(index);
			result = posQueries.get(0).getPostings(index);
			for (int i = 1; i < posQueries.size(); i++) {
				result = intersection(result, posQueries.get(i).getPostings(index));
			}
			result = intersectionNOT(result, negPostings);
		} else {
			result = posQueries.get(0).getPostings(index);
			for (int i = 1; i < posQueries.size(); i++) {
				result = betterIntersection(result, posQueries.get(i).getPostings(index));
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return String.join(" ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}

	@Override
	public Boolean isPositive() {
		return true;
	}
}