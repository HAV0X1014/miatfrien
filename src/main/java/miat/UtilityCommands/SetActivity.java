package miat.UtilityCommands;

import miat.FileHandlers.Whitelist;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.FileNotFoundException;

public class SetActivity {
    public static String setactivity(String m, MessageCreateEvent mc, DiscordApi api) {
        String returnString = null;
        String id  = mc.getMessageAuthor().getIdAsString();
        try {
            if (Whitelist.whitelisted(id)) {
                m = m.replace("[setactivity ", "");
                api.updateActivity(m);
                returnString = "Activity Updated";
            } else
                returnString = "You are not on the debug whitelist.";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return returnString;
    }
}
