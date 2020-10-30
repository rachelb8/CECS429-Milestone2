package cecs429.index;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class ByteUtils {
    private static ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    private static DataOutputStream byteOutStream = new DataOutputStream(byteArrayStream);

    public static void main(String[] args) {

        /*
        int num = 470;
        byte[] result = convertIntToByteArray(num);

        System.out.println("Input            : " + num);
        for (Byte lbyte : result) {
            String resStr = String.format("%8s", Integer.toBinaryString(lbyte & 0xFF)).replace(' ', '0');
            System.out.print(resStr + " ");
        }
        //System.out.println("Byte Array (Hex) : " + convertBytesToHex(result));
        */

        List<List<List<Integer>>> encodeResults = new ArrayList<List<List<Integer>>>();

        //encodeResults.add(variableByteEncode(7));
        encodeResults.add(createByteList(173));
        encodeResults.add(createByteList(203));
        encodeResults.add(createByteList(252));

        encodeResults.add(createByteList(130));
        encodeResults.add(createByteList(240));
        encodeResults.add(createByteList(470));
        encodeResults.add(createByteList(60000));        
        encodeResults.add(createByteList(2460000));
        encodeResults.add(createByteList(245900000));
        encodeResults.add(createByteList(2147483647));

    }
    
    //-------------------------------------------------------------------------

    public static byte[] convertIntToByteArray(int value) {
        return  ByteBuffer.allocate(4).putInt(value).array();
    }

     public static int convertByteArrayToBinaryArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static List<Integer> convertIntToBinList(Integer n) {
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


    public static List<List<Integer>> createByteList(Integer n) {

        byte[] intBytes = getByteArray(n); 
        List<List<Integer>> results = new ArrayList<List<Integer>>();
        for (Byte lbyte : intBytes) {
            int byteToInt = lbyte.intValue();
            List<Integer> temp = convertIntToBinList(byteToInt);
            results.add(temp);
            //System.out.print(temp.toString() + " ");
            //System.out.println(convertToBinary(lbyte));
        }
        //System.out.println();
        return results;
    }

    public static Integer variableByteDecode(List<Byte> byteList) {
        Integer result = 0;
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
        // try {            
        //     byteOutStream.writeInt(integerArg);
        //     byteOutStream.flush();
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        // resultArray = byteArrayStream.toByteArray();
        // byteArrayStream.reset();        
        return resultArray;
    }

    public static byte[] getByteArray(Double doubleArg){
        byte[] resultArray = null;
        try {
            byteOutStream.writeDouble(doubleArg);
            byteOutStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        resultArray = byteArrayStream.toByteArray(); 
        byteArrayStream.reset();  
        return resultArray;
    }

   


    public static void appendToArrayList(List<Byte> arrayArg, byte[] byteArg){
        for (byte lByte : byteArg){
            arrayArg.add(lByte);
        }
    }
}
