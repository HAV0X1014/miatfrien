package miat.FileHandlers;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import java.io.FileNotFoundException;

public class CheckPermission {

    public static boolean checkPermission(SlashCommandInteraction interaction, PermissionType permcheck) {
        boolean hasPerm = interaction.getServer().get().hasPermission(interaction.getUser(), permcheck);

        try {
            if (Whitelist.whitelisted(interaction.getUser().getIdAsString())) {
                hasPerm = true;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return hasPerm;
    }

    public static boolean checkPermission(MessageCreateEvent mc, PermissionType permcheck) {
        boolean hasPerm = mc.getServer().get().hasPermission(mc.getMessageAuthor().asUser().get(), permcheck);

        try {
            if (Whitelist.whitelisted(mc.getMessageAuthor().getIdAsString())) {
                hasPerm = true;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        }

        return hasPerm;
    }
}
