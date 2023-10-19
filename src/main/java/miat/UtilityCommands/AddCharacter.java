package miat.UtilityCommands;

import miat.FileHandlers.ReadFull;
import miat.FileHandlers.Writer;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONObject;

public class AddCharacter {
    public static String add(SlashCommandInteraction interaction, String characterList) {
        String name = interaction.getArgumentStringValueByName("name").get();
        String description = interaction.getArgumentStringValueByName("description").get();
        boolean kfChar = interaction.getArgumentBooleanValueByName("kfchar").get();

        name = name.toLowerCase().replaceAll(" ","");

        if (kfChar) {
            description = description + "\n\nFriends are animals that became human girls, born from Sandstar. Friends are capable of feats that exceed normal human limits. A Friend will preserve some distinct features of their animal form, like ears, tails, and bird Friends with wings on their heads. Friends may also have personality traits which resemble the original animal. Kemonoplasm is a substance derived from Sandstar that can assume functional forms for a Friend, such as their animal features and clothing, while the rest of their body is that of a normal human. Friends' main source of nutrition are Japari Buns and other human foods.";
        }

        String json = ReadFull.read("ServerFiles/characterList.json");
        JSONObject jsonObject = new JSONObject(json);
        jsonObject.getJSONObject("Characters").put(name,description);

        System.out.println(jsonObject.toString(4));
        Writer.write("ServerFiles/characterList.json", jsonObject.toString(2));
        return "Character ``" + name + "`` added.";
    }
}
