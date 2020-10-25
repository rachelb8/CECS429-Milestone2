package cecs429.index;

import java.util.ArrayList;
import java.util.List;

public class GapUtils {    
    
    static List<Integer> getGaps(List<Integer> listArg){
        List <Integer> retList = new ArrayList<Integer>();
        List <Integer> intList = listArg;
        int lastPosition = 0;
        for (int i = 0; i < intList.size(); i++){
            int currPosition = intList.get(i);
            int gapLength = (currPosition - lastPosition);
            retList.add(gapLength);
            lastPosition = currPosition;                        
        }
		return retList;

    }
    
    static List<Integer> deGap(){
        return null;
    }
}
