package miat.FileHandlers;

import java.util.Arrays;

public class Whitelist {
    public static boolean whitelisted(String userID) {
        return userID.contains(Arrays.toString(ConfigHandler.getArray("Whitelist")));
    }
}