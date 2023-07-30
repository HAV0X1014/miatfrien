package miat;

import me.bush.translator.Language;
import miat.FileHandlers.*;
import miat.FunFeatures.*;
import miat.UtilityCommands.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

public class MiatMain {
    static boolean debugmessagelog;
    static String token = MiatToken.read("ServerFiles/token.txt"); //replace token.txt with the filename of the txt with your token

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        System.out.println("Miat logged in.");
        int startTime = (int) (System.currentTimeMillis() / 1000);
        User self = api.getYourself();
        String time = new Date().toString();
        Permissions admin = new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build();

        api.updateActivity(ActivityType.PLAYING,"Miat has AI and animal facts!");

        //SlashCommand.with("ping", "Check if the bot is up.").createGlobal(api).join();
        //SlashCommand.with("uptime", "Get the uptime of the bot.").createGlobal(api).join();
        //SlashCommand.with("purge","Delete the specified number of messages.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Messages", "Amount of messages to delete.", true))).createGlobal(api).join();
        //SlashCommand.with("delete","Delete the specified message by ID.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "MessageID", "MessageID of the message you want to delete.", true))).createGlobal(api).join();
        //SlashCommand.with("pfp","Get the avatar of a member.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "User","The user's PFP you want.", false))).createGlobal(api).join();
        //SlashCommand.with("serverinfo","Get info about the server.").createGlobal(api).join();
        //SlashCommand.with("setlogchannel","Set the deleted message log channel.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Channel", "The channel you want to log deleted messages to.", true))).createGlobal(api).join();
        //SlashCommand.with("ban","Ban the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User", "The user to ban.",true))).createGlobal(api).join();
        //SlashCommand.with("kick","Kick the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User","User to kick.", true))).createGlobal(api).join();
        //SlashCommand.with("miathelp","Show help for the selected category.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "fun","Shows a list of fun commands.", false), SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"utility","Shows a list of utility commands.", false))).createGlobal(api).join();
        //SlashCommand.with("invite","Get an invite link for this bot with permissions needed to function.").createGlobal(api).join();

        //SlashCommand.with("wiki","Get a random Wikipedia article.").createGlobal(api).join();
        //SlashCommand.with("pointcheck","Check your V0Xpoints, the top overall, or another user's points.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "User to check. (Use <@ (userID) > to check across servers).",false), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,"top","See the top 5 earners."))).createGlobal(api).join();
        //SlashCommand.with("inspiro","Get an \"inspirational\" post").createGlobal(api).join();
        //SlashCommand.with("randfr","Get a random Kemono Friends character article from Japari Library.").createGlobal(api).join();
        //SlashCommand.with("godsays","Get the latest word from god, courtesy of Terry A. Davis.").createGlobal(api).join();
        //SlashCommand.with("miat","Get an image of a Miat(a).").createGlobal(api).join();
        //SlashCommand.with("youchat","Ask you.com/chat (YouChat) a question.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Prompt", "The prompt you wish to ask YouChat.",true))).createGlobal(api).join();
        //SlashCommand.with("animalfact","Get a random animal fact.").createGlobal(api).join();
        //SlashCommand.with("joke","Get a random joke from jokeapi.dev.").createGlobal(api).join();
        //SlashCommand.with("createqr","Create a QR code with goqr.me.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING,"Data","Data to encode into the QR code.", true))).createGlobal(api).join();

        /*
        String slashCommandID = "1100917267182669944";
            try {
                api.getGlobalSlashCommandById(Long.parseLong(slashCommandID)).get().delete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        */

        //slash commands
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();

            if (interaction.getCommandName().equals("ping")) {
                interaction.createImmediateResponder().setContent("Pong. Plain and simple.").setFlags(MessageFlag.EPHEMERAL).respond();
            }

            if (interaction.getCommandName().equals("uptime")) {
                interaction.createImmediateResponder().setContent(Uptime.uptime(startTime)).respond();
            }

            if (interaction.getCommandName().equals("purge")) {
                Purge.purge(interaction);
            }

            if (interaction.getCommandName().equals("delete")) {
                DeleteMessage.deleteMessage(interaction, api);
            }

            if (interaction.getCommandName().equals("pfp")) {
                Pfp.pfp(interaction);
            }

            if (interaction.getCommandName().equals("serverinfo")) {
                interaction.createImmediateResponder().setContent("").addEmbed(ServerInfo.serverInfo(interaction, api)).respond();
            }

            if (interaction.getCommandName().equals("setlogchannel")) {
                interaction.createImmediateResponder().setContent(SetLogChannel.setlogchannel(interaction)).respond();
            }

            if (interaction.getCommandName().equals("ban")) {
                interaction.createImmediateResponder().setContent(Ban.ban(interaction)).setFlags(MessageFlag.EPHEMERAL).respond();
            }

            if (interaction.getCommandName().equals("kick")) {
                interaction.createImmediateResponder().setContent(Kick.kick(interaction)).setFlags(MessageFlag.EPHEMERAL).respond();
            }

            if (interaction.getCommandName().equals("miathelp")) {
                interaction.createImmediateResponder().setContent("").addEmbed(Help.help(interaction)).respond();
            }

            if (interaction.getCommandName().equals("invite")) {
                interaction.createImmediateResponder().setContent(api.createBotInvite(admin)).setFlags(MessageFlag.EPHEMERAL).respond();
            }

            //fun commands below

            if (interaction.getCommandName().equals("pointcheck")) {
                if (interaction.getArgumentUserValueByIndex(0).isPresent()) {
                    V0XpointChecker.user(interaction);
                }
                if (interaction.getArgumentBooleanValueByIndex(0).isPresent()) {
                    V0XpointChecker.top(interaction);
                }
                if (!interaction.getOptionByIndex(0).isPresent()) {
                    V0XpointChecker.pointCheck(interaction);
                }
            }

            if (interaction.getCommandName().equals("wiki")) {
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(Wikipedia.randomArticle()).update();
                });
            }

            if (interaction.getCommandName().equals("miat")) {
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 13) + ".png?raw=true").update();
                });
            }

            if (interaction.getCommandName().equals("inspiro")) {
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(Inspiro.inspiro()).update();
                });
            }

            if (interaction.getCommandName().equals("randfr")) {
               interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                   interactionOriginalResponseUpdater.setContent(RandFr.randomFriend()).update();
               });
            }

            if (interaction.getCommandName().equals("godsays")) {
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(Godsays.godSays()).update();
                });
            }

            if (interaction.getCommandName().equals("animalfact")) {
                interaction.createImmediateResponder().setContent("").addEmbed(AnimalFact.animalFact()).respond();
            }

            if (interaction.getCommandName().equals("joke")) {
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("").addEmbed(RandomJoke.randomJoke()).update();
                });
            }

            if (interaction.getCommandName().equals("createqr")) {
                String data = interaction.getArgumentStringValueByIndex(0).get();
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("").addEmbed(QRCodeCreate.qrCodeCreate(data)).update();
                });
            }
        });

        //legacy commands and V0Xpoints
        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();
            String author = mc.getMessageAuthor().toString();
            String server = mc.getServer().get().toString();

            if (debugmessagelog) {
                if (!mc.getMessageAuthor().equals(self) && !mc.getMessageAuthor().toString().equals("MessageAuthor (id: 919786500890173441, name: Miat Bot)")) {
                    try {
                        Webhook.send(ReadFirstLine.read("ServerFiles/webhookURL.txt"), "'" + m + "'\n\n- " + author + "\n- At " + time + " \n- " + server);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (m.startsWith("[")) {
                System.out.println(m);
            }

            if (m.toLowerCase().startsWith("[randfr")) {
                mc.getMessage().reply(RandFr.randomFriend());
            } else

            if (m.toLowerCase().startsWith("[inspiro")) {
                mc.getMessage().reply(Inspiro.inspiro());
            } else

            if (m.toLowerCase().startsWith("[godsays")) {
                mc.getMessage().reply(Godsays.godSays());
            } else

            if (m.toLowerCase().startsWith("[miat")) {
                mc.getMessage().reply("https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 17) + ".png?raw=true");
            } else

            if (m.toLowerCase().startsWith("[base64")) {
                mc.getMessage().reply(Vase64.vase64(m));
            } else

            if (m.toLowerCase().startsWith("[help")) {
                mc.getMessage().reply("Help is in the ``/miathelp`` slash command now!");
            } else

            if (m.toLowerCase().startsWith("[setactivity")) {
                mc.getMessage().reply(SetActivity.setactivity(m,mc,api));
            } else

            if (m.toLowerCase().startsWith("[animalfact")) {
                mc.getMessage().reply(AnimalFact.animalFact());
            } else

            if (m.toLowerCase().startsWith("[wiki")) {
                mc.getMessage().reply(Wikipedia.randomArticle());
            } else

            if (m.toLowerCase().startsWith("[uptime")) {
                mc.getMessage().reply(Uptime.uptime(startTime));
            } else

            if (m.toLowerCase().startsWith("[joke")) {
                Random random = new Random();
                int randomNumber = random.nextInt(15);
                if (randomNumber == 0 || randomNumber == 1) {
                    mc.addReactionsToMessage("\uD83E\uDDBA");
                    mc.addReactionsToMessage("\uD83D\uDEE0️");
                    mc.addReactionsToMessage("\uD83D\uDEA7");
                    mc.addReactionsToMessage("\uD83D\uDC77");
                    mc.getMessage().reply("https://cdn.discordapp.com/attachments/1100888255483875428/1123410595333537943/under_construction.mp4");
                } else {
                    mc.getMessage().reply(RandomJoke.randomJoke());
                }
            } else

            if (m.toLowerCase().startsWith("[qr")) {
                String data = m.replace("[qr ","");
                mc.getMessage().reply(QRCodeCreate.qrCodeCreate(data));
            } else

            if (m.toLowerCase().startsWith("[yc")) {
                String prompt = m.replace("[yc ", "");
                mc.addReactionsToMessage("\uD83D\uDCE8");

                Thread youThread = new Thread(() -> {
                    YouChat.youChat(prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDCE8");
                });
                youThread.start();
            } else

            if (m.toLowerCase().startsWith("[topi")) {
                String prompt = m.toLowerCase().replace("[topi ", "");
                mc.addReactionsToMessage("\uD83C\uDFDE️");

                Thread kemoThread = new Thread(() -> {
                    KemoYou.kemoYou("Topi",prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                });
                kemoThread.start();
            } else

            if (m.toLowerCase().startsWith("[serval")) {
                String prompt = m.toLowerCase().replace("[serval ", "");
                mc.addReactionsToMessage("\uD83C\uDFDE️");

                Thread kemoThread = new Thread(() -> {
                    KemoYou.kemoYou("Serval",prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                });
                kemoThread.start();
            } else

            if (m.toLowerCase().startsWith("[blackbuck")) {
                String prompt = m.toLowerCase().replace("[blackbuck ", "");
                mc.addReactionsToMessage("\uD83C\uDFDE️");

                Thread kemoThread = new Thread(() -> {
                    KemoYou.kemoYou("Blackbuck",prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                });
                kemoThread.start();
            } else

            if (m.toLowerCase().startsWith("[wolverine")) {
                String prompt = m.toLowerCase().replace("[wolverine ", "");
                mc.addReactionsToMessage("\uD83C\uDFDE");

                Thread kemoThread = new Thread(() -> {
                    KemoYou.kemoYou("Wolverine",prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                });
                kemoThread.start();
            } else

            if (m.toLowerCase().startsWith("[silverfox")) {
                String prompt = m.toLowerCase().replace("[silverfox ", "");
                mc.addReactionsToMessage("\uD83C\uDFDE");

                Thread kemoThread = new Thread(() -> {
                    KemoYou.kemoYou("SilverFox",prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                });
                kemoThread.start();
            } else

            if (m.toLowerCase().startsWith("[tact")) {
                String prompt = m.toLowerCase().replace("[tact ","");
                mc.addReactionsToMessage("\uD83D\uDE80");

                Thread tactThread = new Thread(() -> {
                    KemoYou.kemoYou("Tact",prompt,mc);
                    mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDE80");
                });
                tactThread.start();
            } else

            if (m.toLowerCase().startsWith("[bestclient")) {
                Color seppuku = new Color(153,0,238);
                EmbedBuilder e = new EmbedBuilder()
                        .setTitle("Seppuku")
                        .setDescription("Seppuku is one of the best clients of all time, ever!")
                        .setAuthor("Seppuku","https://github.com/seppukudevelopment/seppuku", "https://github.com/seppukudevelopment/seppuku/raw/master/res/seppuku_full.png")
                        .addField("Seppuku Download", "https://github.com/seppukudevelopment/seppuku/releases")
                        .addInlineField("Github", "https://github.com/seppukudevelopment/seppuku")
                        .addInlineField("Website", "https://seppuku.pw")
                        .setColor(seppuku)
                        .setFooter("Seppuku","https://github.com/seppukudevelopment/seppuku")
                        .setImage("https://github.com/seppukudevelopment/seppuku/blob/master/res/seppuku_full.png?raw=true")
                        .setThumbnail("https://github.com/seppukudevelopment/seppuku/blob/master/src/main/resources/assets/seppukumod/textures/seppuku-logo.png?raw=true");
                mc.getMessage().reply(e);
            } else

            if (m.toLowerCase().startsWith("[ml on")) {
                String id = mc.getMessageAuthor().getIdAsString();
                try {
                    if (Whitelist.whitelisted(id)) {
                        debugmessagelog = true;
                        mc.getMessage().reply("Debug Message Log on.");
                    } else {
                        mc.getMessage().reply("You are not on the debug whitelist.");
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else

            if (m.toLowerCase().startsWith("[ml off")) {
                String id = mc.getMessageAuthor().getIdAsString();
                try {
                    if (Whitelist.whitelisted(id)) {
                        debugmessagelog = false;
                        mc.getMessage().reply("Debug Message Log off.");
                    } else {
                        mc.getMessage().reply("You are not on the debug whitelist.");
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else

            if (m.toLowerCase().startsWith("[remove")){
                String miatId = mc.getMessage().getMessageReference().get().getMessage().get().getAuthor().getIdAsString();
                //gets the author id of the message that 'should' have the command response. This is the variable that stores the message that is requested to be deleted.

                String commandIssuer = mc.getMessage().getMessageReference().get().getMessage().get().getReferencedMessage().get().getAuthor().getIdAsString();
                //gets the author id of the command issuer. This is the variable that stores the author of the original command so only the author can delete their command response.

                System.out.println(commandIssuer);
                //dont knock it if it works
                if (miatId.equals(self.getIdAsString()) && mc.getMessageAuthor().getIdAsString().equals(commandIssuer)) {
                    DelOwn.delOwn(mc, api);
                    mc.getMessage().delete();
                } else {
                    mc.getMessage().reply("You cannot delete others' messages using this command.");
                }
            } else

            if (m.startsWith("[purge ")) {
                String amt = m.replace("[purge ","");
                mc.getMessage().reply(Purge.purge(mc, amt));
                Thread removeNotice = new Thread(() -> {
                    Message notif = mc.getChannel().getMessages(1).join().getNewestMessage().get();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    notif.delete();
                });
                removeNotice.start();
            } else

            if (m.startsWith("[translate")) {
                String textToTranslate = m.replace("[translate ", "");
                mc.getMessage().reply(Trnsl.trnsl(textToTranslate, Language.ENGLISH));
            } else

            if (m.toLowerCase().contains("nigg") || m.toLowerCase().contains("n1gg") || m.toLowerCase().contains("kotlin user")) {
                mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__");
            } else

            if (m.contains("HAV0X")) {
                int up = 1;
                V0Xpoints.hV0Xpoints(mc, api, up);
            } else

            if (m.toLowerCase().contains("topi")) {
                int up = 1;
                V0Xpoints.hV0Xpoints(mc, api, up);
            } else

            if (m.toLowerCase().contains("grey")) {
                int up = 1;
                V0Xpoints.hV0Xpoints(mc, api, up);
            } else

            if (m.toLowerCase().contains("seppuku")) {
                int up = 1;
                V0Xpoints.hV0Xpoints(mc, api, up);
            } else

            if (m.toLowerCase().contains("havox") || m.toLowerCase().contains("habox") || m.toLowerCase().contains("havowox") || m.toLowerCase().contains("have cocks") || m.toLowerCase().contains("havix")) {
                int down = -1;
                V0Xpoints.hV0Xpoints(mc, api, down);
            } else

            if (m.toLowerCase().contains("i like kotlin")) {
                int down = -1;
                V0Xpoints.hV0Xpoints(mc, api, down);
            } else

            if (m.startsWith("[")) {
                mc.getChannel().sendMessage("Unknown command. Use ``/miathelp`` for a list of commands.");
            }
        });

        //deleted message logger
        api.addMessageDeleteListener(md -> {
            try {
                if (!md.getMessageAuthor().get().getIdAsString().equals(self)) {
                   Channel logChannel = md.getServer().get().getChannelById(DeletedMessageLogChannel.channelRetriever(md.getServer().get().getIdAsString())).get();

                   EmbedBuilder e = new EmbedBuilder();
                   e.setAuthor(md.getMessageAuthor().get().asUser().get());
                   e.setColor(Color.orange);
                   e.addField("\u200b", "'" + md.getMessageContent().get() + "' \n\n - " + time + "\n" + "Deleted in : " + md.getChannel().toString());
                   logChannel.asServerTextChannel().get().sendMessage(e);
                }
            } catch (NoSuchElementException nse) {
                return;
            }
        });

        api.addServerJoinListener(botJoin -> {
           String serverID = botJoin.getServer().getIdAsString();
            try {
                FileWriter fw = new FileWriter("ServerFiles/" + serverID + ".txt");
                fw.write("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            User me = api.getYourself();
            EmbedBuilder join = new EmbedBuilder();
            join.setAuthor(me);
            join.setTitle("Hello, Miat Fren is here!");
            join.setThumbnail("https://cdn.discordapp.com/attachments/919786447488290816/920839787789836288/miat.jpeg");
            join.addField("Information :", "Slash Commands are supported! \nPrefix : ``[``\nCreator : ``HAV0X#1009`` & ``arsonfrog#9475``");
            join.addField("Get Started :", "Help : ``/miathelp``\nSet Deleted Message Log Channel : ``/setlogchannel``");
            botJoin.getServer().getSystemChannel().get().sendMessage(join);
        });

        api.addReactionAddListener(ra -> {
            //general idea for how to select target language to translate to
            // read flag/target lang -> set as target lang -> get content of message -> send translation
            // the long part is the flags. gonna suck making all of these flags go to a lang
            // 7/29/2023

            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");

            Language targetLang = Language.ENGLISH;
            String messageContent = ra.getMessageContent().orElse("NoContent").toString();

            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            switch(emoji) {
                case "❌":
                    if (deleteCandidate.equals(self.getIdAsString())) {
                        String del = ra.requestMessage().join().getIdAsString();
                        api.getMessageById(del, ra.getChannel()).join().delete();
                    }
                    return;
                case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
                    targetLang = Language.ENGLISH;
                    break;
                case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
                    targetLang = Language.ENGLISH;
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                    targetLang = Language.POLISH;
                    break;
                case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
                    targetLang = Language.CHINESE_SIMPLIFIED;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
                    targetLang = Language.HINDI;
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
                    targetLang = Language.SPANISH;
                    break;
                case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
                    targetLang = Language.SPANISH;
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7": //France French
                    targetLang = Language.FRENCH;
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
                    targetLang = Language.RUSSIAN;
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
                    targetLang = Language.PORTUGUESE;
                    break;
                case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                    targetLang = Language.PORTUGUESE;
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
                    targetLang = Language.GERMAN;
                    break;
                case "\uD83C\uDDEF\uD83C\uDDF5": //Japan Japanese
                    targetLang = Language.JAPANESE;
                    break;
                case "\uD83C\uDDF5\uD83C\uDDED": //Philippines Filipino
                    targetLang = Language.FILIPINO;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF7": //South Korea Korean
                    targetLang = Language.KOREAN;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF5": //North Korea Korean
                    targetLang = Language.KOREAN;
                    break;
                case "\uD83C\uDDFB\uD83C\uDDF3": //Vietnam Vietnamese
                    targetLang = Language.VIETNAMESE;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF9": //Italy Italian
                    targetLang = Language.ITALIAN;
                    break;
                default:
                    return;
            }
            ra.getChannel().sendMessage(Trnsl.trnsl(messageContent, targetLang));
        });
    }
}
