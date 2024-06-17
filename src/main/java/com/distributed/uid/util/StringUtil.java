package com.distributed.uid.util;

/**
 * @author typhoon
 **/
public class StringUtil {

    public static boolean isNumeric(String cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }
}
