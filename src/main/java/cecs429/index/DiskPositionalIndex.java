package cecs429.index;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.text.EnglishTokenStream;
import cecs429.text.MSOneTokenProcessor;
import cecs429.text.TokenProcessor;

public class DiskPositionalIndex implements Index {
    
    String corpusPath = "";
    String binPath = "";
    String weightsBinPath = "";

    public DiskPositionalIndex(String pathArg) {
        corpusPath = pathArg;
        binPath = corpusPath + "\\Postings.bin";
        weightsBinPath = corpusPath + "\\docWeights.bin";
    }


    public static void main(String[] args) {
        Scanner dirScanner = new Scanner(System.in);
        //System.out.print("Please enter a directory: ");
        //String dirSelection = dirScanner.next();
        String dirSelection = "Gibberish";
        DocumentCorpus corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(dirSelection).toAbsolutePath());
        PositionalInvertedIndex invertedIndex = DiskIndexWriter.indexCorpus(corpus);
        DiskIndexWriter.writeIndex(invertedIndex, dirSelection);
        DiskPositionalIndex tIndex = new DiskPositionalIndex(dirSelection );
        tIndex.getVocabulary();
        //List<Posting> testVar = tIndex.getBooleanPostings("worship");
        System.out.println();
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
            Integer docGapInt = ByteUtils.DecodeNextInt(dataInStrm);
            lastDocIDSum = lastDocIDSum + docGapInt;
            Integer docId = lastDocIDSum;
            Double docScore = ByteUtils.DecodeNextDouble(dataInStrm);
            Integer termFrequency = ByteUtils.DecodeNextInt(dataInStrm);            
            for (int k = 0; k < termFrequency; k++){
                 
                List<Integer> VBEncode = ByteUtils.GetNextVariableBytes(dataInStrm);
                Integer posGapInt = ByteUtils.DecodeVariableByte(VBEncode);
            }
            postingsResult.add(new Posting(docId, termFrequency, docScore));
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
            Integer docGapInt = ByteUtils.DecodeNextInt(dataInStrm);            
            lastDocIDSum = lastDocIDSum + docGapInt;
            Integer docId = lastDocIDSum;
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

    

    // private static PositionalInvertedIndex indexCorpus(DocumentCorpus corpus) {
	// 	Iterable<Document> allDocs = corpus.getDocuments();
	// 	TokenProcessor processor = new MSOneTokenProcessor();
    //     DataOutputStream dOutputStream = null;
    //     try {
    //         dOutputStream = new DataOutputStream(new FileOutputStream(corpus.getPathString() + "\\docWeights.bin"));
    //     } catch (FileNotFoundException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //     PositionalInvertedIndex positionalInvertedIndex = new PositionalInvertedIndex();        
	// 	for (Document lDoc : allDocs) {
    //         HashMap<String, Integer> docScores = new HashMap<String, Integer>();
	// 		EnglishTokenStream eStream = new EnglishTokenStream(lDoc.getContent());
	// 		//process into Tokens
	// 		Iterable<String> eTokens = eStream.getTokens();
	// 		//Make into arraylist to track position
	// 		ArrayList<String> tokenList = new ArrayList<>();
	// 		for (String lString : eTokens) {
	// 			tokenList.add(lString);						
	// 		}
	// 		//Starting from the first word, going to the last
	// 		for (int i = 0; i < tokenList.size(); i++){
    //             List<String> processedTokens = new ArrayList<>();
	// 			//The current term
    //             String currentTerm = tokenList.get(i);                
	// 			List<String> processedStrings = processor.processToken(currentTerm);
	// 			for (String lToken : processedStrings) {
	// 				processedTokens.add(lToken);
	// 			}				
	// 			for (String proToken: processedTokens) {
    //                 Integer tokenScore = docScores.get(proToken);
    //                 if (tokenScore == null){
    //                     docScores.put(proToken, 0);
    //                     tokenScore = docScores.get(proToken);
    //                 }
    //                 docScores.put(proToken, ++tokenScore);
    //                 //Get list of positions if it exists
	// 				List<Posting> existingPostings = positionalInvertedIndex.getBooleanPostings(proToken);					
	// 				//If it already exists
	// 				if (existingPostings != null){
	// 					Posting lastPosting = existingPostings.get(existingPostings.size()-1);
	// 					if (lastPosting.getDocumentId() != lDoc.getId()){
	// 						List<Integer> lPositions = new ArrayList<>();
	// 						Posting lPosting = new Posting(lDoc.getId(), lPositions);
	// 						lPosting.addPosition(i);
	// 						positionalInvertedIndex.addTerm(proToken, lPosting);
	// 					}
	// 					else{
	// 						lastPosting.addPosition(i);
	// 					}
	// 				}
	// 				//If it does not yet have an existing list
	// 				else{
	// 					//Create a new list, add the index
	// 					List<Integer> lPositions = new ArrayList<>();
	// 					Posting lPosting = new Posting(lDoc.getId(), lPositions);
	// 					lPosting.addPosition(i);
	// 					//and put as a new entry to the map
	// 					positionalInvertedIndex.addTerm(proToken, lPosting);
	// 				}
	// 			}
				
    //         }
    //         Double sumScores = 0.0;
    //         for (Integer lInt: docScores.values()){
    //             double lnScore = 1 + (Math.log(lInt));
    //             Double sqrdInt = (lnScore*lnScore);                
    //             sumScores += sqrdInt;
    //         }
    //         Double docWeight = Math.sqrt(sumScores);
    //         try {
    //             dOutputStream.writeDouble(docWeight);
    //         } catch (IOException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
	// 	}		
	// 	return positionalInvertedIndex;
	// }

    public Double getDocWeight(Integer checkDocID) {
        Integer numOfBytes = 8;
        Integer weightsStartOffset = numOfBytes * (checkDocID - 1) ;
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
