package miat.UtilityCommands;

import miat.FileHandlers.Whitelist;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public class SetActivity {
    public static String setactivity(String m, MessageCreateEvent mc, DiscordApi api, String prefix) {
        String returnString = null;
        String id  = mc.getMessageAuthor().getIdAsString();
        if (Whitelist.whitelisted(id)) {
            m = m.replace(prefix + "setactivity ", "");
            api.updateActivity(m);
            returnString = "Activity Updated";
        } else
            returnString = "You are not on the debug whitelist.";
        return returnString;
    }
}
