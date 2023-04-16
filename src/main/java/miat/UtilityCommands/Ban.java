package miat.UtilityCommands;

import miat.FileHandlers.CheckPermission;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

public class Ban {
    public static String ban(SlashCommandInteraction interaction) {
        User user2ban = interaction.getArgumentByIndex(0).get().getUserValue().get();
        String username2ban = user2ban.getMentionTag();
        String replyContent;

        if (CheckPermission.checkPermission(interaction, PermissionType.BAN_MEMBERS)) {
            interaction.getServer().get().banUser(user2ban);
            replyContent = "Banned user " + username2ban + ".";
        }

        else {
            replyContent = "You do not have BAN_MEMBERS permissions, oops!";
        }
        return replyContent;
    }
}
