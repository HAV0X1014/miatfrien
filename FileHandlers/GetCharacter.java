package miat.FileHandlers;

import org.json.JSONObject;

import java.util.Iterator;

public class GetCharacter {
    public static String getContext(String characterName, String characterList) {
        JSONObject obj = new JSONObject(characterList);
        String context = obj.getJSONObject("Characters").getString(characterName);
        return context;
    }
    public static boolean inList(String characterName, String characterList) {
        Iterator<String> obj = new JSONObject(characterList).getJSONObject("Characters").keys();
        String currentKey;
        while (obj.hasNext()) {
            currentKey = obj.next();
            if (currentKey.equals(characterName)) {
                return true;
            }
        }
        return false;
    }
    public static String getList(String characterList) {
        Iterator<String> obj = new JSONObject(characterList).getJSONObject("Characters").keys();
        StringBuilder characters = new StringBuilder();
        characters.append(obj.next());
        while (obj.hasNext()) {
            characters.append(", ").append(obj.next());
        }
        return characters.toString();
    }
}
