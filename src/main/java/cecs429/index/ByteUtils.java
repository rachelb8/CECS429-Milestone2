package cecs429.index;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ByteUtils {
    private static ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    private static DataOutputStream byteOutStream = new DataOutputStream(byteArrayStream);

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
