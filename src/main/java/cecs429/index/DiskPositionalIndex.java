package cecs429.index;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    String binPath = "";

    public DiskPositionalIndex(String binPathArg) {
        binPath = binPathArg;
    }

    public static void main(String[] args) {
        Scanner dirScanner = new Scanner(System.in);
        //System.out.print("Please enter a directory: ");
        //String dirSelection = dirScanner.next();
        String dirSelection = "MobyDick";
        DocumentCorpus corpus = DirectoryCorpus.loadMilestone1Directory(Paths.get(dirSelection).toAbsolutePath());
        Index invertedIndex = indexCorpus(corpus);
        DiskIndexWriter.writeIndex(invertedIndex, dirSelection);
        DiskPositionalIndex tIndex = new DiskPositionalIndex((dirSelection + "\\Postings.bin"));
        tIndex.getPostings("worship");
    }

    @Override
    public List<Posting> getPostings(String term) {
        List<Posting> postingsResult = new ArrayList<Posting>();
        DB db = DBMaker.fileDB("file.db").make();
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
        Integer docFrequency = DecodeNextInt(dataInStrm);
        Integer lastDocIDSum = 0;            
        for (int j = 0; j < docFrequency; j++){
            Integer docGapInt = DecodeNextInt(dataInStrm);
            lastDocIDSum = lastDocIDSum + docGapInt;
            Integer docId = lastDocIDSum;
            Integer termFrequency = DecodeNextInt(dataInStrm);
            List<Integer> positionList = new ArrayList<Integer>();
            Integer lastPosSum = 0;
            for (int k = 0; k < termFrequency; k++){
                Integer posGapInt = DecodeNextInt(dataInStrm);
                lastPosSum = lastPosSum + posGapInt;
                positionList.add(lastPosSum);
            }
            postingsResult.add(new Posting(docId, positionList));
        }
       return postingsResult;
    }

    @Override
    public List<String> getVocabulary() {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer DecodeNextInt(DataInputStream dataInputStreamArg){
        Integer result;
        List<Integer> byteEncodeList = GetNextByteEncoding(dataInputStreamArg);
        result = IntFromByteEncoding(byteEncodeList);
        return result;
    }

    public List<Integer> GetNextByteEncoding(DataInputStream dataInputStreamArg){
        List<Integer> result = new ArrayList<Integer>();
        //This is what must change for variable byte encoding;
        for (int i = 0; i < 4; i++ ){            
            try {
                result.add(dataInputStreamArg.read());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public Integer IntFromByteEncoding(List<Integer> byteEncoding){
        int size = 4;
        byte[] bList = new byte[size];
        for (int i = 0; i < size; i++){
            bList[i] = (byteEncoding.get(i).byteValue());
        }
        int result = java.nio.ByteBuffer.wrap(bList).getInt();
        return result;
    }

    private static Index indexCorpus(DocumentCorpus corpus) {
		Iterable<Document> allDocs = corpus.getDocuments();
		TokenProcessor processor = new MSOneTokenProcessor();

		PositionalInvertedIndex positionalInvertedIndex = new PositionalInvertedIndex();
		for (Document lDoc : allDocs) {
			EnglishTokenStream eStream = new EnglishTokenStream(lDoc.getContent());
			//process into Tokens
			Iterable<String> eTokens = eStream.getTokens();
			//Make into arraylist to track position
			ArrayList<String> tokenList = new ArrayList<>();
			for (String lString : eTokens) {
				tokenList.add(lString);						
			}
			//Starting from the first word, going to the last
			for (int i = 0; i < tokenList.size(); i++){
				List<String> processedTokens = new ArrayList<>();
				//The current term
				String currentTerm = tokenList.get(i);
				List<String> processedStrings = processor.processToken(currentTerm);
				for (String lToken : processedStrings) {
					processedTokens.add(lToken);
				}				
				for (String proToken: processedTokens) {
					//Get list of positions if it exists
					List<Posting> existingPostings = positionalInvertedIndex.getPostings(proToken);					
					//If it already exists
					if (existingPostings != null){
						Posting lastPosting = existingPostings.get(existingPostings.size()-1);
						if (lastPosting.getDocumentId() != lDoc.getId()){
							List<Integer> lPositions = new ArrayList<>();
							Posting lPosting = new Posting(lDoc.getId(), lPositions);
							lPosting.addPosition(i);
							positionalInvertedIndex.addTerm(proToken, lPosting);
						}
						else{
							lastPosting.addPosition(i);
						}
					}
					//If it does not yet have an existing list
					else{
						//Create a new list, add the index
						List<Integer> lPositions = new ArrayList<>();
						Posting lPosting = new Posting(lDoc.getId(), lPositions);
						lPosting.addPosition(i);
						//and put as a new entry to the map
						positionalInvertedIndex.addTerm(proToken, lPosting);
					}
				}
				
			}			
		}
		
		return positionalInvertedIndex;
	}
    
}
