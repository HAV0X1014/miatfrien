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
            description = description + "\n\nFriends are human girls with animal-like features. They are animals that have transformed into human girls through a substance called Sandstar. While retaining influences from their animal form, such as ears, tail, wings (for bird friends), and personality traits, friends do not have paws, claws, or fur and are fully human. There are friends representing both extant and extinct animal species. Sandstar, a mineral, interacts with animals to create friends in their human form. Japari Park is an island safari that encompasses various biomes and ecosystems, housing the corresponding animal and plant life. At the center of the park is a large mountain, which generates Sandstar at its peak with crystalline structures extending beyond it. Celliens are cell-like alien creatures with the sole goal of consuming friends, causing them to revert to their animal form.";
        }

        String json = ReadFull.read("ServerFiles/characterList.json");
        JSONObject jsonObject = new JSONObject(json);
        jsonObject.getJSONObject("Characters").put(name,description);

        System.out.println(jsonObject.toString(4));
        Writer.write("ServerFiles/characterList.json", jsonObject.toString(2));
        return "Character ``" + name + "`` added.";
    }
}
