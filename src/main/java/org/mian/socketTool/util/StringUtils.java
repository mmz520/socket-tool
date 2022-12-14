package org.mian.socketTool.util;

public class StringUtils {
    public static boolean isNumeric(String str){
        if(isEmptyOrBlank(str)) return false;
        for (int i = 0; i < str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyOrBlank(String str){
        if(str.equals(null)) return true;
        if(str.trim().equals("")) return true;
        return false;
    }
}
