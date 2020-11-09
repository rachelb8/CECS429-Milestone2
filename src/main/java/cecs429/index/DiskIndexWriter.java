package cecs429.index;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class DiskIndexWriter {
    // docFreq doc ID docTermFreq [pos] docIDAsGap [posGap]
    public static List<Integer> writeIndex(Index indArg, String absPathsArg) {
               
        DB db = DBMaker.fileDB("file.db").make();
        BTreeMap<String, Integer> map = db.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.INTEGER).createOrOpen();
        List<String> lVocab = indArg.getVocabulary();
        List<Integer> byteOffsets = new ArrayList<Integer>();
        List<Double> docScores = new ArrayList<Double>();
        String postingsBinPath = absPathsArg + "\\Postings.bin";
        HashMap<Integer,HashMap<String, Integer>> fullSend = new HashMap<Integer, HashMap<String, Integer>>();
        DataOutputStream dataStream = null;
        try {
            dataStream = new DataOutputStream(new FileOutputStream(postingsBinPath));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < lVocab.size(); i++) {
            String currentVocab = lVocab.get(i);
            map.put(currentVocab, dataStream.size());
            List<Byte> toBeBytes = new ArrayList<>();
            List<Posting> currentPostings = indArg.getBooleanPostings(currentVocab);
            List<Integer> docList = new ArrayList<>();
            List<Double> scoreList = new ArrayList<>();
            List<Integer> tFreqList = new ArrayList<>();
            List<List<Integer>> posGapsLists = new ArrayList<>();
            /*System.out.println("======Vocab=======");
            System.out.println(lVocab.get(i));
            System.out.println("======Vocab=======");*/

            for (int k = 0; k < currentPostings.size(); k++) {
                Posting currPosting = currentPostings.get(k);
                Integer docId = currPosting.getDocumentId();
                docList.add(docId);
                HashMap<String,Integer> docMap = fullSend.get(docId);
                if (docMap == null){
                    fullSend.put(docId, new HashMap<String, Integer>());
                    docMap = fullSend.get(docId); 
                }
                
                List<Integer> postingGaps = GapUtils.getGaps(currPosting.getPositions());
                posGapsLists.add(postingGaps);
                Integer termFreq = postingGaps.size();
                docMap.put(currentVocab, termFreq);
                tFreqList.add(termFreq);
                double lnScore = 1 + (Math.log(termFreq));
                scoreList.add(lnScore);   

            }

            List<Integer> docsGapsList = GapUtils.getGaps(docList);
            
            //Doc Frequency
            Integer DocFreq = docsGapsList.size();
            byte[] DocFreqByteArray = ByteUtils.getByteArray(DocFreq);
            ByteUtils.appendToArrayList(toBeBytes, DocFreqByteArray);
            
            for (int m = 0; m < docsGapsList.size(); m++) {
                //Add Doc ID gap
                Integer docIDGap = docsGapsList.get(m);                
                byte[] DocIdGapByte = ByteUtils.getByteArray(docIDGap);
                ByteUtils.appendToArrayList(toBeBytes, DocIdGapByte);    
                byte[] scoreByte = ByteUtils.getByteArray(scoreList.get(m));
                ByteUtils.appendToArrayList(toBeBytes, scoreByte);    
                List<Integer> postingGaps = posGapsLists.get(m);
                byte[] termFreqByte = ByteUtils.getByteArray(postingGaps.size());
                ByteUtils.appendToArrayList(toBeBytes, termFreqByte);    
                for (Integer lInt : postingGaps) {
                    List <Integer> encodeInts = ByteUtils.VBEncode(lInt);
                    byte posByte;
                    byte[] singleByte = new byte[1]; 
                    for (Integer eInt : encodeInts) {
                        posByte = ByteUtils.getByte(eInt);
                        singleByte [0] = posByte;
                        ByteUtils.appendToArrayList(toBeBytes, singleByte);
                    }                    
                    
                }
            }
            //System.out.println(debugStatement);            
            // for (int a = 0; a < docsGapsList.size(); a++){
            //     toBeBytes.add(docsGapsList.get(a));
            //     toBeBytes.add(tFreqList.get(a));
            //     List<Integer> positionsList =  posGapsLists.get(a);
            //     for (Integer lInt : positionsList) {
            //         toBeBytes.add(lInt);                    
            //     }                     
            // }
            for (byte lByte : toBeBytes) {
                try {
                    dataStream.write(lByte);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            byteOffsets.add(dataStream.size());            
        }
        db.close();
        return byteOffsets;  
    }    
}
// String debugStatement = "";
            // debugStatement += docsGapsList.size() + "; ";
            // for (int m = 0; m < docsGapsList.size(); m++) {
            //     debugStatement += docsGapsList.get(m);
            //     debugStatement += ", ";
            //     List<Integer> postingGaps = posGapsLists.get(m);
            //     debugStatement += postingGaps.size() + " [";
            //     for (Integer lInt : postingGaps) {
            //         debugStatement += " " + lInt + ",";
            //     }
            //     debugStatement += "]";
            // }