package miat.UtilityCommands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;

public class DelOwn {
    public static void delOwn(MessageCreateEvent mc, DiscordApi api) {
        mc.getMessage().getMessageReference().get().getMessage().get().delete();

    }
}
