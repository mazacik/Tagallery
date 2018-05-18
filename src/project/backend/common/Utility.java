package project.backend.common;

public abstract class Utility {


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c == '.' || c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
