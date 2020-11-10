package com.example.test.PositionalIndTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import cecs429.documents.DirectoryCorpus;

import cecs429.documents.DocumentCorpus;
import cecs429.index.*;


import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.example.test.IndexerService;

public class PositionalInvertedIndexTest {

    //Full vocabulary
    static String[] allTerms = new String[] { "the", "stand", "world", "among", "star", "there", "is", "a", "power",
            "platinum", "what", "time" };

    //Vocabulary and Positions of each document
    static String[] zeroTerms = new String[] { "the", "world", "star", "is", "power", "platinum", "what", "time" };
    static List<Integer[]> zeroPos = new ArrayList<Integer[]>();
    static Integer[] the0Pos = new Integer[] { 2, 5, 14, 17, 22 };
    static Integer[] world0Pos = new Integer[] { 3, 18, 23 };
    static Integer[] star0Pos = new Integer[] { 11, 19 };
    static Integer[] is0Pos = new Integer[] { 1, 7, 10, 16, 21 };
    static Integer[] power0Pos = new Integer[] { 4, 6, 13, 15 };
    static Integer[] platinum0Pos = new Integer[] { 12, 20 };
    static Integer[] what0Pos = new Integer[] { 0, 9 };
    static Integer[] time0Pos = new Integer[] { 8 };

    static String[] oneTerms = new String[] { "the", "world", "among", "star", "there", "is", "a", "power", "what",
            "time" };
    static List<Integer[]> onePos = new ArrayList<Integer[]>();
    static Integer[] the1Pos = new Integer[] { 19 };
    static Integer[] world1Pos = new Integer[] { 20 };
    static Integer[] among1Pos = new Integer[] { 2, 4, 12 };
    static Integer[] star1Pos = new Integer[] { 3, 5, 11, 15 };
    static Integer[] there1Pos = new Integer[] { 8 };
    static Integer[] is1Pos = new Integer[] { 1, 6, 9, 16, 21 };
    static Integer[] a1Pos = new Integer[] { 10, 17 };
    static Integer[] power1Pos = new Integer[] { 7, 13, 18 };
    static Integer[] what1Pos = new Integer[] { 0, 22 };
    static Integer[] time1Pos = new Integer[] { 14 };

    static String[] twoTerms = new String[] { "the", "stand", "world", "among", "star", "there", "is", "a", "power",
            "what" };
    static List<Integer[]> twoPos = new ArrayList<Integer[]>();
    static Integer[] the2Pos = new Integer[] { 0, 4, 10, 14, 18, 21, 23, 27 };
    static Integer[] stand2Pos = new Integer[] { 2, 9, 12, 28 };
    static Integer[] world2Pos = new Integer[] { 1, 11, 15 };
    static Integer[] among2Pos = new Integer[] { 3, 13, 20 };
    static Integer[] star2Pos = new Integer[] { 5, 19, 22 };
    static Integer[] there2Pos = new Integer[] { 6, 16 };
    static Integer[] is2Pos = new Integer[] { 7, 17, 25, 29 };
    static Integer[] a2Pos = new Integer[] { 8 };
    static Integer[] power2Pos = new Integer[] { 24 };
    static Integer[] what2Pos = new Integer[] { 26 };

    static String[] threeTerms = new String[] { "the", "stand", "world", "among", "star", "is", "a", "power", "what",
            "time" };
    static List<Integer[]> threePos = new ArrayList<Integer[]>();
    static Integer[] the3Pos = new Integer[] { 2, 15, 23 };
    static Integer[] stand3Pos = new Integer[] { 11, 12, 19 };
    static Integer[] world3Pos = new Integer[] { 0, 6, 8, 16, 24 };
    static Integer[] among3Pos = new Integer[] { 4, 7 };
    static Integer[] star3Pos = new Integer[] { 3, 5 };
    static Integer[] is3Pos = new Integer[] { 1, 9, 13, 17, 22 };
    static Integer[] a3Pos = new Integer[] { 10, 18 };
    static Integer[] power3Pos = new Integer[] { 14 };
    static Integer[] what3Pos = new Integer[] { 20 };
    static Integer[] time3Pos = new Integer[] { 21 };

    static String[] fourTerms = new String[] { "the", "stand", "world", "among", "star", "there", "is", "a", "power",
            "platinum", "what", "time" };
    static List<Integer[]> fourPos = new ArrayList<Integer[]>();
    static Integer[] the4Pos = new Integer[] { 0, 5, 12, 14, 16, 20 };
    static Integer[] stand4Pos = new Integer[] { 13, 21 };
    static Integer[] world4Pos = new Integer[] { 6, 9 };
    static Integer[] among4Pos = new Integer[] { 17 };
    static Integer[] star4Pos = new Integer[] { 4, 15, 26 };
    static Integer[] there4Pos = new Integer[] { 10 };
    static Integer[] is4Pos = new Integer[] { 2, 7, 11, 19, 23 };
    static Integer[] a4Pos = new Integer[] { 3, 8, 24 };
    static Integer[] power4Pos = new Integer[] { 18 };
    static Integer[] platinum4Pos = new Integer[] { 25 };
    static Integer[] what4Pos = new Integer[] { 1 };
    static Integer[] time4Pos = new Integer[] { 22 };
    
    //Constructs the hand constructed index and the code constructed index.
    public static Stream<PosPairs> GetPosPairs() {
        PositionalInvertedIndex handConsIndex = new PositionalInvertedIndex();
        fillPosArray();
        docPostings(handConsIndex, zeroTerms, 0, zeroPos);
        docPostings(handConsIndex, oneTerms, 1, onePos);
        docPostings(handConsIndex, twoTerms, 2, twoPos);
        docPostings(handConsIndex, threeTerms, 3, threePos);
        docPostings(handConsIndex, fourTerms, 4, fourPos);

        DocumentCorpus testCorpus = DirectoryCorpus.loadMilestone1Directory(Paths.get("Gibberish"));
        Index codeConsIndex = DiskIndexWriter.indexCorpus(testCorpus);
        ArrayList<PosPairs> allPosPairs = new ArrayList<PosPairs>();
        for (int i = 0; i < allTerms.length; i++) {
            List<Posting> handPostings = handConsIndex.getBooleanPostings(allTerms[i]);
            List<Posting> codePostings = codeConsIndex.getBooleanPostings(allTerms[i]);
            for (int k = 0; k < handPostings.size(); k++) {
                Posting handPosPosting = handPostings.get(k);
                Posting codePosPosting = codePostings.get(k);
                List<Integer> handPositions = handPosPosting.getPositions();
                List<Integer> codePositions = codePosPosting.getPositions();
                PosPairs currPair = new PosPairs(handPositions, codePositions);
                allPosPairs.add(currPair);
            }
        }
        Iterable<PosPairs> iPosPairs = allPosPairs;
        Stream<PosPairs> sPosPairs = StreamSupport.stream(iPosPairs.spliterator(),true);
        return sPosPairs;
    }

    @ParameterizedTest (name = "{index} - Run Test with args = {0}")
    @MethodSource("GetPosPairs")
    public void indexTest(PosPairs inputPairs) {
        assertArrayEquals(inputPairs.handList, inputPairs.codeList);   
    } 

    //Creates all postings assosciated with a doc
    static void docPostings (PositionalInvertedIndex indexArg, String[] terms, int docIdArg, List<Integer[]> intListArg){
        for (int i = 0; i < terms.length; i++){
            PositionalInvertedIndex testInd = indexArg;
            String strTest = terms[i];
            int docTest = docIdArg;
            Integer[] intsTest = intListArg.get(i);
            fillPostings(indexArg, terms[i], docIdArg, intListArg.get(i));        
        } 
    }

    //fill the postings of each doc
    static void fillPostings(PositionalInvertedIndex indexArg, String termArg, int docIdArg, Integer[] positionsArg ){
        Posting positionPosting = new Posting(docIdArg, makeArray());
        for (Integer position : positionsArg) {
            positionPosting.addPosition(position);
        }
        indexArg.addTerm(termArg, positionPosting);              
    }

    static ArrayList<Integer> makeArray(){
        return new ArrayList<Integer>();
    }

    static void fillPosArray(){
        zeroPos.add(the0Pos);
        zeroPos.add(world0Pos);
        zeroPos.add(star0Pos);
        zeroPos.add(is0Pos);
        zeroPos.add(power0Pos);
        zeroPos.add(platinum0Pos);
        zeroPos.add(what0Pos);
        zeroPos.add(time0Pos);

        onePos.add(the1Pos);
        onePos.add(world1Pos);
        onePos.add(among1Pos);
        onePos.add(star1Pos);
        onePos.add(there1Pos);
        onePos.add(is1Pos);
        onePos.add(a1Pos);
        onePos.add(power1Pos);
        onePos.add(what1Pos);
        onePos.add(time1Pos);

        twoPos.add(the2Pos);
        twoPos.add(stand2Pos);
        twoPos.add(world2Pos);
        twoPos.add(among2Pos);
        twoPos.add(star2Pos);
        twoPos.add(there2Pos);
        twoPos.add(is2Pos);
        twoPos.add(a2Pos);
        twoPos.add(power2Pos);
        twoPos.add(what2Pos);

        threePos.add(the3Pos);
        threePos.add(stand3Pos);
        threePos.add(world3Pos);
        threePos.add(among3Pos);
        threePos.add(star3Pos);
        threePos.add(is3Pos);
        threePos.add(a3Pos);
        threePos.add(power3Pos);
        threePos.add(what3Pos);
        threePos.add(time3Pos);

        fourPos.add(the4Pos);
        fourPos.add(stand4Pos);
        fourPos.add( world4Pos);
        fourPos.add( among4Pos);
        fourPos.add( star4Pos);
        fourPos.add( there4Pos);
        fourPos.add( is4Pos);
        fourPos.add( a4Pos);
        fourPos.add( power4Pos);
        fourPos.add( platinum4Pos);
        fourPos.add( what4Pos);
        fourPos.add( time4Pos);

    }
}
