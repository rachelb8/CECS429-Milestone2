package cecs429.query;

import java.util.List;

public class RankedRetrieval {
    public void RankQuery(String queryString){
        String[] queryWords = queryString.split(" ");
        for (int i = 0; i < queryWords.length; i++){
            
        }
        
        //1. For each term in the query        
        //  a. Calculate Wq,t = ln (1 + (N / dft))
        //  b. For each document d in t' posting list
        //      1 - Aquire an accumulater value A_d (the design of this system is up to you)
        //      2 - Calculate W_d,t = 1 + ln(tf_t,d)
        //      3 - Increase A_d by w_d,t x w_q,t
        //2. For each non-zero A_d, Divide A_d by L_d, where L_d is read from the docWeights.bin file
        //3. Select and return the top K -10 documents by largest A_d value. 
        //      (Use a binary heap priority queue to select the largest results; )
        //      (Do not sort the accumulators)
    }
}
