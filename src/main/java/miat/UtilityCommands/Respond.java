package miat.UtilityCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import static miat.MiatMain.noReplyPing;

public class Respond {
    //Regular text
    public static void replyNoPing(MessageCreateEvent mc, String content) {
        new MessageBuilder().setAllowedMentions(noReplyPing).setContent(content).replyTo(mc.getMessageId()).send(mc.getChannel());
    }
    //With embed
    public static void replyNoPing(MessageCreateEvent mc, EmbedBuilder embed) {
        new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(embed).replyTo(mc.getMessageId()).send(mc.getChannel());
    }
}
