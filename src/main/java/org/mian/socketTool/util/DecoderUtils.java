package org.mian.socketTool.util;

public class DecoderUtils {
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hex){
        hex=hex.replace(" ","").replace("-","");
        int len=hex.length();
        byte[] b=new byte[len/2];
        for(int i=0;i<len;i+=2){
            b[i/2]=(byte) ((Character.digit(hex.charAt(i),16)<<4)+Character.digit(hex.charAt(i+1),16));
        }
        return b;
    }
}
