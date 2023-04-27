package miat.UtilityCommands;

import miat.FileHandlers.CheckPermission;
import miat.FileHandlers.Whitelist;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.io.FileNotFoundException;

public class Kick {
    public static String kick(SlashCommandInteraction interaction) {
        User user2kick = interaction.getArgumentByIndex(0).get().getUserValue().get();
        String username2kick = user2kick.getMentionTag();
        String replyContent;

        if (CheckPermission.checkPermission(interaction, PermissionType.KICK_MEMBERS)) {
            try {
                if (Whitelist.whitelisted(user2kick.getIdAsString())) {
                    replyContent = "Failed to ban user " + username2kick + ".";
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            interaction.getServer().get().kickUser(user2kick);
            replyContent = "Kicked user " + username2kick + ".";
        }

        else {
            replyContent = "You do not have KICK_MEMBERS permissions, oops!";
        }
        return replyContent;
    }
}
