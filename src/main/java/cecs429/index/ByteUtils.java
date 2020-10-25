package cecs429.index;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ByteUtils {
    private static ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    private static DataOutputStream byteOutStream = new DataOutputStream(byteArrayStream);

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
        byte[] resultArray = null;
        try {            
            byteOutStream.writeInt(integerArg);
            byteOutStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        resultArray = byteArrayStream.toByteArray();
        byteArrayStream.reset();        
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
