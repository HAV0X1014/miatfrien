package miat.UtilityCommands;

import miat.FileHandlers.ReadFile;
import miat.FileHandlers.Writer;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONObject;

public class AddCharacter {
    public static String add(SlashCommandInteraction interaction, String characterList) {
        String name = interaction.getArgumentStringValueByName("name").get();
        String description = interaction.getArgumentStringValueByName("description").get();
        boolean kfChar = interaction.getArgumentBooleanValueByName("kfchar").get();
        String imageURL = interaction.getArgumentStringValueByName("imagepath").orElse("");

        String json = ReadFile.getFull("ServerFiles/characters.json");
        JSONObject jsonObject = new JSONObject(json);

        JSONObject newEntry = new JSONObject();
        newEntry.put("kfChar", kfChar);
        newEntry.put("description", description);
        newEntry.put("image", imageURL);

        jsonObject.put(name,newEntry);

        Writer.write("ServerFiles/characters.json", jsonObject.toString(2));
        return "Character ``" + name + "`` added.";
    }
}
