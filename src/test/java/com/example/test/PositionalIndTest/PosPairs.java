package com.example.test.PositionalIndTest;

import java.util.List;

public class PosPairs {
    public Object[] handList;
    public Object[] codeList;
    
    public PosPairs(List<Integer> handArg, List<Integer> codeArg){
        handList = handArg.toArray();
        codeList = codeArg.toArray();
    }     
}
