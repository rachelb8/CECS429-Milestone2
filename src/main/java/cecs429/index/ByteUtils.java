package cecs429.index;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class ByteUtils {
    
    public static List<Integer> VBEncode(Integer intArg){
        List<List<Integer>> encodedBinary = VBEncodeBinList(createIntBin(intArg));
        List<Integer> encode = new ArrayList<Integer>();
        for (List<Integer> lList: encodedBinary){
            encode.add(BinListToInteger(lList));
        }
        if (encode.size() == 0){
            encode.add(128);
        }
        return encode;

    }
    public static List<Integer> IntToBinList(Integer n) {
        int[] binary = new int[8];
        int index = 0;
        Integer convertInt = Byte.toUnsignedInt(n.byteValue());        
        for (int i = 0; i < 8; i++) {
            int testVal = convertInt % 2;
            binary[index++] = testVal;
            convertInt = convertInt / 2;
        }

        List<Integer> realBinary = new ArrayList<>();
        for (int i = index-1; i >= 0; i--) {
            realBinary.add(binary[i]);
        }
        return realBinary;
    }

    public static Integer BinListToInteger(List<Integer> listArg){        
        int[] bitVals = new int[]{128,64,32,16,8,4,2,1};
        int result = 0;
        
        for (int i = 0; i < 8; i++){
            int lInt = listArg.get(i);
            if (lInt == 1){
                result = result + bitVals[i] ;
            }
            else{
                continue;
            }
        }
        return result;
    }

    public static List<List<Integer>> createIntBin(Integer n) {

        byte[] intBytes = getByteArray(n); 
        List<List<Integer>> results = new ArrayList<List<Integer>>();
        for (Byte lbyte : intBytes) {
            int byteToInt = lbyte.intValue();
            List<Integer> temp = IntToBinList(byteToInt);
            results.add(temp);
        }
        return results;
    }

    public static List<List<Integer>> VBEncodeBinList(List<List<Integer>> binListArg){
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        List<Integer> temp = new ArrayList<Integer>();
        for (List<Integer> lList : binListArg ){
            temp.addAll(lList);
        }
        int bytesNeeded = GetRequiredByteCount(temp);
        List<Integer> encodeTemp = new ArrayList<Integer>();
        int k = 0;
        int startIndex = temp.size() - (bytesNeeded * 7);
        if (startIndex < 0){
            encodeTemp.add(0);
            k++;

            while (startIndex < 0){
                encodeTemp.add(0);
                startIndex++;
                k++;
            }
        }
        
        for (int i = startIndex; i < temp.size() ; i++){
            if (k == 0){
                if (bytesNeeded > 1){
                    encodeTemp.add(0);
                }
                else{
                    encodeTemp.add(1);
                }
                k++;
            }
            encodeTemp.add(temp.get(i));
            k++;
            if (k == 8){
                k = 0;
                bytesNeeded--;
                result.add(encodeTemp);
                encodeTemp = new ArrayList<Integer>();
            }
        }

        return result;
    }

    public static Integer GetRequiredByteCount (List<Integer> fullBin){
        int i, remaining, result;
        for (i = 0; i < fullBin.size(); i++){
            if (fullBin.get(i) == 1){
                break;
            }
        }
        remaining = fullBin.size() - i;
        if (remaining % 7 == 0){
            result = remaining / 7;
        }
        else{
            result = (remaining / 7) + 1;
        }
        return result;
    }

    public static Integer DecodeVariableByte(List<Integer> encodingArg) {
        List<List<Integer>> byteList = new ArrayList<List<Integer>>();
        List<Integer> tempBinary = new ArrayList<Integer>();
        List<List<Integer>> completeBinary = new ArrayList<List<Integer>>();
        List<Integer> intEncoding = new ArrayList<Integer>();
        for (Integer lInt : encodingArg){
            byteList.add(IntToBinList(lInt));
        }
        

        for (List<Integer> lList : byteList){
            for (int i = 1; i < 8; i++){
                tempBinary.add(lList.get(i));
            }
        }
        int bitsFilled = tempBinary.size();
        int bitsToFill = 32 - bitsFilled;
        int binaryWalker = 0;
        while (bitsToFill < 0){
            bitsToFill++;
            tempBinary.get(binaryWalker++);
        }
        for (int list = 0; list < 4; list++){
            List<Integer> byteBin = new ArrayList<Integer>();
            for (int ind = 0; ind < 8; ind++){
                if (bitsToFill > 0){                    
                    byteBin.add(0);
                    bitsToFill--;
                }
                else{
                    byteBin.add(tempBinary.get(binaryWalker++));
                }
            }
            completeBinary.add(byteBin);
            

        }
        for (List <Integer> lBits : completeBinary){
            Integer value = BinListToInteger(lBits);
            intEncoding.add(value);
        }
        Integer result = IntFromByteEncoding(intEncoding);       
        return result;
    }

    //-------------------------------------------------------------------------

    public static Integer DecodeNextInt(DataInputStream dataInputStreamArg){
        Integer result;
        List<Integer> byteEncodeList = GetNextByteEncoding(dataInputStreamArg, 4);
        result = IntFromByteEncoding(byteEncodeList);
        return result;
    }
    
    public static Double DecodeNextDouble(DataInputStream dataInputStreamArg){
        Double result;
        List<Integer> byteEncodeList = GetNextByteEncoding(dataInputStreamArg, 8);
        result = DoubleFromByteEncoding(byteEncodeList);
        return result;
    }

    public static List<Integer> GetNextByteEncoding(DataInputStream dataInputStreamArg, Integer sizeArg){
        List<Integer> result = new ArrayList<Integer>();
        //This is what must change for variable byte encoding;
        for (int i = 0; i < sizeArg; i++ ){            
            try {
                result.add(dataInputStreamArg.read());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public static List<Integer> GetNextVariableBytes(DataInputStream dataInputStreamArg){
        List<Integer> result = new ArrayList<Integer>();
        Boolean continueReading = true;
        //This is what must change for variable byte encoding;
        while (continueReading){            
            try {
                Integer currByte = dataInputStreamArg.read(); 
                List<Integer> bits = IntToBinList(currByte);
                while (bits.get(0) != 1){ 
                    result.add(currByte);
                    currByte = dataInputStreamArg.read();
                    bits = IntToBinList(currByte);
                }
                result.add(currByte);
                continueReading = false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Integer IntFromByteEncoding(List<Integer> byteEncoding){
        int size = byteEncoding.size();
        byte[] bList = new byte[size];
        for (int i = 0; i < size; i++){
            bList[i] = (byteEncoding.get(i).byteValue());
        }
        int result = java.nio.ByteBuffer.wrap(bList).getInt();
        return result;
    }

    public static Double DoubleFromByteEncoding(List<Integer> byteEncoding){
        int size = byteEncoding.size();
        byte[] bList = new byte[size];
        for (int i = 0; i < size; i++){
            bList[i] = (byteEncoding.get(i).byteValue());
        }
        double result = java.nio.ByteBuffer.wrap(bList).getDouble();
        return result;
    }

    public static byte[] getByteArray(Integer integerArg) {
        byte[] resultArray = ByteBuffer.allocate(4).putInt(integerArg).array(); 
        return resultArray;
    }

    public static byte getByte(Integer integerArg) {
        int intVal = (int) integerArg; 
        return (byte) intVal;
    }


    public static byte[] getByteArray(Double doubleArg){
        byte[] resultArray = ByteBuffer.allocate(8).putDouble(doubleArg).array();
        return resultArray;
    }   

    public static void appendToArrayList(List<Byte> arrayArg, byte[] byteArg){
        for (byte lByte : byteArg){
            arrayArg.add(lByte);
        }
    }
}

//========================== Code Graveyard ==============
//Here there be monsters
/*
private static ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
private static DataOutputStream byteOutStream = new DataOutputStream(byteArrayStream);

public static void main(String[] args) {

    
    int num = 470;
    byte[] result = convertIntToByteArray(num);

    System.out.println("Input            : " + num);
    for (Byte lbyte : result) {
        String resStr = String.format("%8s", Integer.toBinaryString(lbyte & 0xFF)).replace(' ', '0');
        System.out.print(resStr + " ");
    }
    //System.out.println("Byte Array (Hex) : " + convertBytesToHex(result));
    

    List<List<List<Integer>>> encodeResults = new ArrayList<List<List<Integer>>>();

    
    // encodeResults.add(createIntBin(173));
    // encodeResults.add(createIntBin(203));
    // encodeResults.add(createIntBin(252));

    // encodeResults.add(createIntBin(130));
    // encodeResults.add(createIntBin(240));
    // encodeResults.add(createIntBin(470));
    // encodeResults.add(createIntBin(60000));        
    // encodeResults.add(createIntBin(2460000));
    // encodeResults.add(createIntBin(245900000));
    // encodeResults.add(createIntBin(2147483647));
    
    List<List<Integer>> test1 = VBEncodeBinList(createIntBin(240));
    var test2 = VBEncodeBinList(createIntBin(470));
    var test3 = VBEncodeBinList(createIntBin(60000));   
    var test4 = VBEncodeBinList(createIntBin(8));   
    var test5 = VBEncode(0);
    var test6 = VBEncode(1);

    List<List<List<Integer>>> encodes = new ArrayList<List<List<Integer>>>();
    for (List<List<Integer>> lList: encodeResults){
        encodes.add(VBEncodeBinList(lList));
    }
    for (List<List<Integer>> lList : encodes) {
        List<Integer> binEncodes = new ArrayList<Integer>();
        for (List<Integer> lBin : lList) {
            Integer result = BinListToInteger(lBin);
            binEncodes.add(result);
        }
        System.out.println(DecodeVariableByte(binEncodes));
    }

}
*/
