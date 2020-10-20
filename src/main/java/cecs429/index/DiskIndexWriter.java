package cecs429.index;


import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import cecs429.index.GapUtils;

public class DiskIndexWriter {
    // docFreq doc ID docTermFreq [pos] docIDAsGap [posGap]
    public static List<Integer> writeIndex(Index indArg, String absPathsArg) {
        List<String> lVocab = indArg.getVocabulary();
        List<Integer> byteOffsets = new ArrayList<Integer>();
        
        String postingsBinPath = absPathsArg + "\\Postings.bin";
        DataOutputStream dataStream = null;
        try {
            dataStream = new DataOutputStream(new FileOutputStream(postingsBinPath));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < lVocab.size(); i++) {
            List<Integer> toBeBytes = new ArrayList<>();
            List<Posting> currentPostings = indArg.getPostings(lVocab.get(i));
            List<Integer> docList = new ArrayList<>();
            List<Integer> tFreqList = new ArrayList<>();
            List<List<Integer>> posGapsLists = new ArrayList<>();
            System.out.println("======Vocab=======");
            System.out.println(lVocab.get(i));
            System.out.println("======Vocab=======");

            for (int k = 0; k < currentPostings.size(); k++) {
                Posting currPosting = currentPostings.get(k);
                docList.add(currPosting.getDocumentId());
                List<Integer> postingGaps = GapUtils.getGaps(currPosting.getPositions());
                posGapsLists.add(postingGaps);
                tFreqList.add(postingGaps.size());
                // toBeBytes.add(currDocID);
                // toBeBytes.add(termFreq);

            }

            List<Integer> docsGapsList = GapUtils.getGaps(docList);
            String debugStatement = "";
            debugStatement += docsGapsList.size() + "; ";
            for (int m = 0; m < docsGapsList.size(); m++) {
                debugStatement += docsGapsList.get(m);
                debugStatement += ", ";
                List<Integer> postingGaps = posGapsLists.get(m);
                debugStatement += postingGaps.size() + " [";
                for (Integer lInt : postingGaps) {
                    debugStatement += " " + lInt + ",";
                }
                debugStatement += "]";
            }
            toBeBytes.add(docsGapsList.size());
            for (int m = 0; m < docsGapsList.size(); m++) {
                toBeBytes.add(docsGapsList.get(m));
                List<Integer> postingGaps = posGapsLists.get(m);
                toBeBytes.add(postingGaps.size());
                for (Integer lInt : postingGaps) {
                    toBeBytes.add(lInt);
                }
            }
            System.out.println(debugStatement);            
            // for (int a = 0; a < docsGapsList.size(); a++){
            //     toBeBytes.add(docsGapsList.get(a));
            //     toBeBytes.add(tFreqList.get(a));
            //     List<Integer> positionsList =  posGapsLists.get(a);
            //     for (Integer lInt : positionsList) {
            //         toBeBytes.add(lInt);                    
            //     }                     
            // }
            for (Integer lInt : toBeBytes) {
                try {
                    dataStream.writeInt(lInt);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            byteOffsets.add(dataStream.size());
        }
        
        return byteOffsets;  
    }    
}
