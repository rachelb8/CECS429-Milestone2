package cecs429.index;
import java.util.*;

/**
 * Implements an Inverted Index
 */
public class PositionalInvertedIndex implements Index {
    Map<String, List<Posting>> invIndex = new HashMap<String, List<Posting>>();
	
	public PositionalInvertedIndex() {			
    }
    
    /**
     * Add documentID to the term's postings list if it does not already exist
     */
    public void addTerm(String termArg, Posting postingArg) {
    	// termPostings = the list of postings for that term
    	List<Posting> termPostings = invIndex.get(termArg);
    	// if the term exists
        if (termPostings != null){
        	// add documentId to postings
			List<Posting> postingsOfTerm = invIndex.get(termArg);
        	Posting lastPosting = postingsOfTerm.get(postingsOfTerm.size()-1);
			int lastDocID = lastPosting.getDocumentId();
			//Add in O(1) time by checking document id of last document
        	if (!(lastDocID == postingArg.getDocumentId())){
        		invIndex.get(termArg).add(postingArg);
        	}
        }	
        else{
        	List<Posting> lPostings = new ArrayList<>();
        	lPostings.add(postingArg);
			invIndex.put(termArg, lPostings); 
        }
	}


    /**
     * Retrieves a list of Postings of documents that contain the given term
     */
    public List<Posting> getPostings(String term){
		return invIndex.get(term);
    }

    public List<String> getVocabulary() {
		List<String> lVocab = new ArrayList<>();
		for (String lstring : invIndex.keySet()) {
			lVocab.add(lstring);
		}
		Collections.sort(lVocab);
    	return Collections.unmodifiableList(lVocab);
    }
}
