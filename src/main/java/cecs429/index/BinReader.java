package cecs429.index;

import java.io.*;
import java.util.List;
import java.util.NavigableSet;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class BinReader {

    public static void main(String[] args) {
        PrintBin("Gibberish");
    }

    public static void PrintBin(String path) {
        String dbName = path + ".db";
        DB db = DBMaker.fileDB(dbName).make();
        BTreeMap<String, Integer> map = db.treeMap("map").keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.INTEGER).createOrOpen();
        DataInputStream dataInStrm = null;

        NavigableSet<String> allVocab = map.getKeys();
        for (String string : allVocab) {
            System.out.println(string);
        }
        for (String term : allVocab) {
            //String term = "think";
            try {
                dataInStrm = new DataInputStream(new FileInputStream(path + "\\Postings.bin"));

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Integer postingStartOffset = map.get(term);

            try {
                dataInStrm.skipBytes(postingStartOffset);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
            
            System.out.println("Term - " + term);
            int docFreq;
            docFreq = ByteUtils.DecodeNextInt(dataInStrm);
            System.out.println("Doc Freq - " + docFreq);
            int docIDSum = 0;
            for (int i = 0; i < docFreq; i++){
                int docGap = ByteUtils.DecodeNextInt(dataInStrm);
                docIDSum = docIDSum + docGap;
                System.out.println("Doc ID  - " + docIDSum);
                double docScore = ByteUtils.DecodeNextDouble(dataInStrm);
                System.out.println("Doc Score - " + docScore);
                int termFreq = ByteUtils.DecodeNextInt(dataInStrm);
                System.out.println("Term Freq - " + termFreq);
                int lastPosSum = 0;
                System.out.println("==Positions==");
                for (int k = 0; k < termFreq; k++){
                    List<Integer> VBEncode = ByteUtils.GetNextVariableBytes(dataInStrm);
                    Integer posGapInt = ByteUtils.DecodeVariableByte(VBEncode);
                    lastPosSum = lastPosSum + posGapInt;
                    System.out.println(lastPosSum);
                }
            }
            System.out.println("=====================================================");
            System.out.println("=====================================================");

        }
        db.close();
    }
    
}
