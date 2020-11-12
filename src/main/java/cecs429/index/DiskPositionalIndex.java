package cecs429.index;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class DiskPositionalIndex implements Index {
    
    String corpusPath = "";
    String binPath = "";
    String weightsBinPath = "";

    public DiskPositionalIndex(String pathArg) {
        corpusPath = pathArg;
        binPath = corpusPath + "\\Postings.bin";
        weightsBinPath = corpusPath + "\\docWeights.bin";
    }

    @Override
    public List<Posting> getRankedPostings(String term) {
        List<Posting> postingsResult = new ArrayList<Posting>();
        String dbName = corpusPath + ".db";
        DB db = DBMaker.fileDB(dbName).make();
        BTreeMap<String, Integer> map = db.treeMap("map").keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.INTEGER).createOrOpen();
        Integer postingStartOffset = map.get(term);
        DataInputStream dataInStrm = null;
        if(postingStartOffset != null) {
	        try {
	            dataInStrm = new DataInputStream(new FileInputStream(binPath));
	
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        try {
	            dataInStrm.skipBytes(postingStartOffset);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        Integer docFrequency = ByteUtils.DecodeNextInt(dataInStrm);
	        Integer lastDocIDSum = 0;            
	        for (int j = 0; j < docFrequency; j++){
                List<Integer> docGapEncode = ByteUtils.GetNextVariableBytes(dataInStrm);
                Integer docGapInt = ByteUtils.DecodeVariableByte(docGapEncode);
                lastDocIDSum = lastDocIDSum + docGapInt;
                Integer docId = lastDocIDSum;
                Double docScore = ByteUtils.DecodeNextDouble(dataInStrm);
	            Integer termFrequency = ByteUtils.DecodeNextInt(dataInStrm);            
	            for (int k = 0; k < termFrequency; k++){
	                 
	                List<Integer> VBEncode = ByteUtils.GetNextVariableBytes(dataInStrm);
	                @SuppressWarnings("unused")
					Integer posGapInt = ByteUtils.DecodeVariableByte(VBEncode);
	            }
	            postingsResult.add(new Posting(docId, termFrequency, docScore));
	        }
        }
        db.close();
        
        return postingsResult;
    }


    @Override
    public List<Posting> getBooleanPostings(String term) {
        List<Posting> postingsResult = new ArrayList<Posting>();
        String dbName = corpusPath + ".db";
        DB db = DBMaker.fileDB(dbName).make();
        BTreeMap<String, Integer> map = db.treeMap("map").keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.INTEGER).createOrOpen();
        Integer postingStartOffset = map.get(term);
        
        if (postingStartOffset != null) {
        	DataInputStream dataInStrm = null;
            try {
                dataInStrm = new DataInputStream(new FileInputStream(binPath));

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        try {
	            dataInStrm.skipBytes(postingStartOffset);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        Integer docFrequency = ByteUtils.DecodeNextInt(dataInStrm);
	        Integer lastDocIDSum = 0;            
	        for (int j = 0; j < docFrequency; j++){
                List<Integer> docGapEncode = ByteUtils.GetNextVariableBytes(dataInStrm);
                Integer docGapInt = ByteUtils.DecodeVariableByte(docGapEncode);
                lastDocIDSum = lastDocIDSum + docGapInt;
                Integer docId = lastDocIDSum;
                @SuppressWarnings("unused")
				Double docScore = ByteUtils.DecodeNextDouble(dataInStrm);
	            Integer termFrequency = ByteUtils.DecodeNextInt(dataInStrm);
	            List<Integer> positionList = new ArrayList<Integer>();
	            Integer lastPosSum = 0;
	            for (int k = 0; k < termFrequency; k++){
	                List<Integer> VBEncode = ByteUtils.GetNextVariableBytes(dataInStrm);
	                Integer posGapInt = ByteUtils.DecodeVariableByte(VBEncode);
	                lastPosSum = lastPosSum + posGapInt;
	                positionList.add(lastPosSum);
	            }
	            postingsResult.add(new Posting(docId, positionList));
	        }
        }
        db.close();
        
        return postingsResult;
    }

    @Override
    public List<String> getVocabulary() {
        List<String> result = new ArrayList<String>();
        String dbName = corpusPath + ".db";
        DB db = DBMaker.fileDB(dbName).make();
        BTreeMap<String, Integer> map = db.treeMap("map").keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.INTEGER).createOrOpen();
        Iterator<String> keys = map.keyIterator();
        while (keys.hasNext()){
            result.add(keys.next());
        }
        db.close();
        // TODO Auto-generated method stub
        return result;
    }

    public Double getDocWeight(Integer checkDocID) {
        Integer numOfBytes = 8;
        Integer weightsStartOffset = numOfBytes * checkDocID;
        DataInputStream dataInStrm = null;
        FileInputStream fileInStrm = null;
        
        try {
            fileInStrm = new FileInputStream(weightsBinPath);
            dataInStrm = new DataInputStream(fileInStrm);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            dataInStrm.skipBytes(weightsStartOffset);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Double docWeight = ByteUtils.DecodeNextDouble(dataInStrm);
        
        try {
            fileInStrm.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return docWeight;    
    }
}
