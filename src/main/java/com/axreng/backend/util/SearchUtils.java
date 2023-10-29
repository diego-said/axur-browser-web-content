package com.axreng.backend.util;

import java.security.SecureRandom;

public class SearchUtils {

    public final static int MAX_ID_LENGTH = 8;
    private final static String ID_VALID_SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static SecureRandom secureRnd = new SecureRandom();

    public static String randomId() {
        StringBuilder sb = new StringBuilder(MAX_ID_LENGTH);
        for(int i = 0; i < MAX_ID_LENGTH; i++)
            sb.append(ID_VALID_SYMBOLS.charAt(secureRnd.nextInt(ID_VALID_SYMBOLS.length())));
        return sb.toString();
    }

}
