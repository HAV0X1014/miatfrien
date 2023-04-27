package miat.UtilityCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;

public class Help {
    public static EmbedBuilder help(SlashCommandInteraction interaction) {
        String page = interaction.getFullCommandName().toLowerCase();
        Color seppuku = new Color(153,0,238);
        EmbedBuilder e = new EmbedBuilder();

        switch (page) {
            case "miathelp fun":
                e.setTitle("Fun Features");
                e.addField("__**Slash Commands**__", "``wiki``: Get a random Wikipedia article." +
                "\n\n``pointcheck``: Check your own balance of V0Xpoints, the top 5 earners, or another user's balance. Increase your score by saying specific phrases." +
                "\n\n``inspiro``: Get an \"inspirational\" image." +
                "\n\n``randfr``: Get a random Kemono Friends character article." +
                "\n\n``godsays``: Get the latest word from God, courtesy of Terry A. Davis." +
                "\n\n``youchat``: Use YouChat to answer a question. (Powered by YouChat and betterAPI.)" +
                "\n\n``miat``: run it and figure it out");
                e.addField("__**Legacy Commands**__","``[base64 [encode|decode] [text]``: Encodes or Decodes the supplied text." +
                "\n\n``[askserval``: Ask Serval from Kemono Friends a question. (Powered by YouChat and betterAPI.)" +
                "\n\n``[bestclient``: Informs you about the best client.");
                e.addField("Created By :", "``HAV0X#1009`` & ``arsonfrog#9475``");
                e.setColor(Color.orange);
                break;

            case "miathelp utility":
                e.setTitle("Utility Features");
                e.addField("__**Slash Commands**__","``ping``: Check if the bot is online or not." +
                "\n\n``uptime``: Check the uptime of the bot." +
                "\n\n``purge``: Purge the desired amount of messages." +
                "\n\n``delete``: Delete the specified message - both message ID types work." +
                "\n\n``pfp``: Get a user's PFP. If no argument is sent, it will get your own." +
                "\n\n``serverinfo``: Get info about the server." +
                "\n\n``setlogchannel``: Set the deleted message log channel." +
                "\n\n``ban``: Ban the specified user." +
                "\n\n``kick``: Kick the specified user." +
                "\n\n``miathelp``: Get help with the bot." +
                "\n\n``invite`` : Get an invite for the bot with all permissions needed.");
                e.addField("__**Legacy Commands**__", "``!ml [on|off]``: Enable or Disable the Debug Message Log. Default off.");
                e.addField("Created By :", "``HAV0X#1009`` & ``arsonfrog#9475``");
                e.setColor(seppuku);
                break;

            default:
                e.setTitle("Help Command");
                e.addField("Miat","This command will show you the commands this bot has.");
                e.setColor(Color.RED);
                break;
        }
        return e;
    }
}
