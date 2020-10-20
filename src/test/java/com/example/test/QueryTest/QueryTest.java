package com.example.test.QueryTest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.test.IndexerService;

import org.junit.Before;
import org.junit.Test;

import cecs429.documents.Document;
import cecs429.index.PositionalInvertedIndex;
import cecs429.query.Query;


public class QueryTest {
    IndexerService testIndServ;
    
    @Before
    public void Initialize(){
        testIndServ = new IndexerService();
        long queryTime = testIndServ.run("Gibberish");
    } 

    @Test
    public void AndPartialTest(){
        List<Document> allResult = testIndServ.search("Stand Platinum");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();        
        allDocIds.add(4);
        Collections.sort(resultIds);
        Collections.sort(allDocIds);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }
    

    @Test
    public void AndNullTest(){
        List<Document> nullResult = testIndServ.search("Heirophant Green");
        Object[] emptyArray = {};
        List<Document> allResult = testIndServ.search("The World");       
        assertArrayEquals(nullResult.toArray(), emptyArray);
        
        
    }

    @Test
    public void AndFullTest(){
        List<Document> allResult = testIndServ.search("The World");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        Object[] allDocIds = new Integer[] {0,1,2,3,4};
        assertArrayEquals(resultIds.toArray(), allDocIds);
        
    }

    @Test
    public void OrPartialTest(){
        List<Document> allResult = testIndServ.search("Stand + Platinum");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();        
        allDocIds.add(0);
        allDocIds.add(2);
        allDocIds.add(3);
        allDocIds.add(4);
        Collections.sort(resultIds);
        Collections.sort(allDocIds);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }

    @Test
    public void OrNullTest(){
        List<Document> nullResult = testIndServ.search("Heirophant + Green");
        Object[] emptyArray = {};
        assertArrayEquals(nullResult.toArray(), emptyArray);
    }

    @Test
    public void OrFullTest(){
        List<Document> allResult = testIndServ.search("Among + Platinum");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();
        allDocIds.add(0);
        allDocIds.add(1);
        allDocIds.add(2);
        allDocIds.add(3);
        allDocIds.add(4);
        Collections.sort(resultIds);
        Collections.sort(allDocIds);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }

    @Test
    public void NotPartialTest(){
        List<Document> allResult = testIndServ.search("Star -Platinum");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();        
        allDocIds.add(1);
        allDocIds.add(2);
        allDocIds.add(3);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }
    
    @Test
    public void NotNullTest(){
        List<Document> nullResult = testIndServ.search("World -The");
        Object[] emptyArray = {};
        assertArrayEquals(nullResult.toArray(), emptyArray);
    }

    @Test
    public void NotFullTest(){
        List<Document> allResult = testIndServ.search("The -Heirophant");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();
        allDocIds.add(0);
        allDocIds.add(1);
        allDocIds.add(2);
        allDocIds.add(3);
        allDocIds.add(4);
        Collections.sort(resultIds);
        Collections.sort(allDocIds);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }

    @Test
    public void PhraseTest(){
        List<Document> allResult = testIndServ.search("\"Stand among the world\"");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();
        allDocIds.add(2);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }

    @Test
    public void LongPhraseTest(){
        List<Document> allResult = testIndServ.search("\"What is the world's power?\n The power is time\n What is star platinum's power?\n The power is the world.\n Star platinum is the world?\"");
        List<Integer> resultIds = new ArrayList<Integer>();
        for (Document lDoc: allResult){
            resultIds.add(lDoc.getId());
        }
        List<Integer> allDocIds = new ArrayList<Integer>();
        allDocIds.add(0);
        assertArrayEquals(resultIds.toArray(), allDocIds.toArray());
    }
}
