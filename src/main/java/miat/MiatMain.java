package miat;

import me.bush.translator.Language;
import me.bush.translator.Translator;
import miat.FileHandlers.*;
import miat.FunFeatures.*;
import miat.UtilityCommands.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Random;

public class MiatMain {
    static boolean debugmessagelog;
    static String token = ConfigHandler.getString("Token");
    static String prefix = ConfigHandler.getString("Prefix");
    static String botName = ConfigHandler.getString("BotName");
    static boolean deeplEnabled = Boolean.parseBoolean(ConfigHandler.getString("DeepLEnabled"));
    static String deepLEmoji = ConfigHandler.getString("DeepLEmoji");
    static boolean useGoogleAsFallbackForDeepL = Boolean.parseBoolean(ConfigHandler.getString("UseGoogleTranslateAsFallbackForDeepL"));
    static String deepLKey = ConfigHandler.getString("DeepLKey");
    static String[] ignoredChannels = ConfigHandler.getArray("TranslatorFlagIgnoredChannels");
    static String[] reWordsGoodWords = ConfigHandler.getArray("ReWordsGoodWords");
    static String[] reWordsGoodWordsExactMatch = ConfigHandler.getArray("ReWordsGoodWordsExactMatch");
    static String[] reWordsBadWords = ConfigHandler.getArray("ReWordsBadWords");
    static String[] reWordsBadWordsExactMatch = ConfigHandler.getArray("ReWordsBadWordsExactMatch");
    static Boolean registerSlashCommands = Boolean.valueOf(ConfigHandler.getString("RegisterSlashCommands"));

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        System.out.println(botName + " logged in.");
        int startTime = (int) (System.currentTimeMillis() / 1000);
        User self = api.getYourself();
        String time = new Date().toString();
        Permissions admin = new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build();
        Translator translator = new Translator(); //google translate object
        com.deepl.api.Translator deepLTranslator = new com.deepl.api.Translator(deepLKey); //deepL translator object

        api.updateActivity(ActivityType.PLAYING, botName + " has Flag Translation!");

        if (registerSlashCommands) {
            System.out.println("Registering slash commands...");
            SlashCommand.with("ping", "Check if the bot is up.").createGlobal(api).join();
            SlashCommand.with("uptime", "Get the uptime of the bot.").createGlobal(api).join();
            SlashCommand.with("purge","Delete the specified number of messages.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Messages", "Amount of messages to delete.", true))).createGlobal(api).join();
            SlashCommand.with("delete","Delete the specified message by ID.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "MessageID", "MessageID of the message you want to delete.", true))).createGlobal(api).join();
            SlashCommand.with("pfp","Get the avatar of a member.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "User","The user's PFP you want.", false))).createGlobal(api).join();
            SlashCommand.with("serverinfo","Get info about the server.").createGlobal(api).join();
            SlashCommand.with("setlogchannel","Set the deleted message log channel.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Channel", "The channel you want to log deleted messages to.", true))).createGlobal(api).join();
            SlashCommand.with("ban","Ban the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User", "The user to ban.",true))).createGlobal(api).join();
            SlashCommand.with("kick","Kick the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User","User to kick.", true))).createGlobal(api).join();
            SlashCommand.with("miathelp","Show help for the selected category.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "fun","Shows a list of fun commands.", false), SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"utility","Shows a list of utility commands.", false))).createGlobal(api).join();
            SlashCommand.with("invite","Get an invite link for this bot with permissions needed to function.").createGlobal(api).join();

            SlashCommand.with("wiki","Get a random Wikipedia article.").createGlobal(api).join();
            SlashCommand.with("pointcheck","Check your ReWords points, the top overall, or another user's points.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "User to check. (Use <@ (userID) > to check across servers).",false), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,"top","See the top 5 earners."))).createGlobal(api).join();
            SlashCommand.with("inspiro","Get an \"inspirational\" post").createGlobal(api).join();
            SlashCommand.with("randfr","Get a random Kemono Friends character article from Japari Library.").createGlobal(api).join();
            SlashCommand.with("godsays","Get the latest word from god, courtesy of Terry A. Davis.").createGlobal(api).join();
            SlashCommand.with("miat","Get an image of a Miat(a).").createGlobal(api).join();
            SlashCommand.with("youchat","Ask you.com/chat (YouChat) a question.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Prompt", "The prompt you wish to ask YouChat.",true))).createGlobal(api).join();
            SlashCommand.with("animalfact","Get a random animal fact.").createGlobal(api).join();
            SlashCommand.with("joke","Get a random joke from jokeapi.dev.").createGlobal(api).join();
            SlashCommand.with("createqr","Create a QR code with goqr.me.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING,"Data","Data to encode into the QR code.", true))).createGlobal(api).join();

            System.out.println("SLASH COMMANDS REGISTERED! Set \"RegisterSlashCommands\" to \"false\" in config.json!");
        }
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
            String command = interaction.getCommandName();
            switch(command) {
                case "ping":
                    interaction.createImmediateResponder().setContent("Pong. Plain and simple.").setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "uptime":
                    interaction.createImmediateResponder().setContent(Uptime.uptime(startTime)).respond();
                    break;
                case "purge":
                    Purge.purge(interaction);
                    break;
                case "delete":
                    DeleteMessage.deleteMessage(interaction, api);
                    break;
                case "pfp":
                    Pfp.pfp(interaction);
                    break;
                case "serverinfo":
                    interaction.createImmediateResponder().setContent("").addEmbed(ServerInfo.serverInfo(interaction, api)).respond();
                    break;
                case "setlogchannel":
                    interaction.createImmediateResponder().setContent(SetLogChannel.setlogchannel(interaction)).respond();
                    break;
                case "ban":
                    interaction.createImmediateResponder().setContent(Ban.ban(interaction)).setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "kick":
                    interaction.createImmediateResponder().setContent(Kick.kick(interaction)).setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "miathelp":
                    interaction.createImmediateResponder().setContent("").addEmbed(Help.help(interaction)).respond();
                    break;
                case "invite":
                    interaction.createImmediateResponder().setContent(api.createBotInvite(admin)).setFlags(MessageFlag.EPHEMERAL).respond();
                    break;

                //fun commands below
                case "pointcheck":
                    if (interaction.getArgumentUserValueByIndex(0).isPresent()) {
                        ReWordsPointChecker.user(interaction);
                    }
                    if (interaction.getArgumentBooleanValueByIndex(0).isPresent()) {
                        ReWordsPointChecker.top(interaction);
                    }
                    if (!interaction.getOptionByIndex(0).isPresent()) {
                        ReWordsPointChecker.pointCheck(interaction);
                    }
                    break;
                case "wiki":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent(Wikipedia.randomArticle()).update();
                    });
                    break;
                case "miat":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 13) + ".png?raw=true").update();
                    });
                    break;
                case "inspiro":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent(Inspiro.inspiro()).update();
                    });
                    break;
                case "randfr":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent(RandFr.randomFriend()).update();
                    });
                    break;
                case "godsays":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent(Godsays.godSays()).update();
                    });
                    break;
                case "animalfact":
                    interaction.createImmediateResponder().setContent("").addEmbed(AnimalFact.animalFact()).respond();
                    break;
                case "joke":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(RandomJoke.randomJoke()).update();
                    });
                    break;
                case "createqr":
                    String data = interaction.getArgumentStringValueByIndex(0).get();
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(QRCodeCreate.qrCodeCreate(data)).update();
                    });
                    break;
            }
        });

        //legacy commands and ReWords
        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();
            String author = mc.getMessageAuthor().toString();
            String server = mc.getServer().get().toString();

            if (debugmessagelog) {
                if (!mc.getMessageAuthor().equals(self) && !mc.getMessageAuthor().toString().equals("MessageAuthor (id: 919786500890173441, name: Miat Bot)")) {
                    try {
                        Webhook.send(ConfigHandler.getString("WebhookURL"), "'" + m + "'\n\n- " + author + "\n- At " + time + " \n- " + server);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (m.startsWith(prefix)) {
                System.out.println(m);
            }

            if (m.startsWith(prefix)) {
                String[] parts = m.split(" ", 2);
                String command = parts[0].toLowerCase().replace(prefix, "");

                switch (command) {
                    case "randfr":
                        mc.getMessage().reply(RandFr.randomFriend());
                        break;
                    case "inspiro":
                        mc.getMessage().reply(Inspiro.inspiro());
                        break;
                    case "godsays":
                        mc.getMessage().reply(Godsays.godSays());
                        break;
                    case "miat":
                        mc.getMessage().reply("https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 17) + ".png?raw=true");
                        break;
                    case "base64":
                        mc.getMessage().reply(Vase64.vase64(m));
                        break;
                    case "help":
                        mc.getMessage().reply("Help is in the ``/miathelp`` slash command now!");
                        break;
                    case "setactivity":
                        mc.getMessage().reply(SetActivity.setactivity(m, mc, api, prefix));
                        break;
                    case "animalfact":
                        mc.getMessage().reply(AnimalFact.animalFact());
                        break;
                    case "wiki":
                        mc.getMessage().reply(Wikipedia.randomArticle());
                        break;
                    case "uptime":
                        mc.getMessage().reply(Uptime.uptime(startTime));
                        break;
                    case "joke":
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
                        break;
                    case "qr":
                        String data = m.replace(prefix + "qr ", "");
                        mc.getMessage().reply(QRCodeCreate.qrCodeCreate(data));
                        break;
                    case "yc":
                        String prompt = m.replace(prefix + "yc ", "");
                        mc.addReactionsToMessage("\uD83D\uDCE8");

                        Thread youThread = new Thread(() -> {
                            YouChat.youChat(prompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDCE8");
                        });
                        youThread.start();
                        break;
                    case "topi":
                        String topiPrompt = m.replace(prefix + "topi ", "");
                        mc.addReactionsToMessage("\uD83C\uDFDE️");

                        Thread topiThread = new Thread(() -> {
                            KemoYou.kemoYou("Topi", topiPrompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                        });
                        topiThread.start();
                        break;
                    case "serval":
                        String servalPrompt = m.toLowerCase().replace(prefix + "serval ", "");
                        mc.addReactionsToMessage("\uD83C\uDFDE️");

                        Thread servalThread = new Thread(() -> {
                            KemoYou.kemoYou("Serval", servalPrompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                        });
                        servalThread.start();
                        break;
                    case "blackbuck":
                        String blackbuckPrompt = m.toLowerCase().replace(prefix + "blackbuck ", "");
                        mc.addReactionsToMessage("\uD83C\uDFDE️");

                        Thread blackbuckThread = new Thread(() -> {
                            KemoYou.kemoYou("Blackbuck", blackbuckPrompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                        });
                        blackbuckThread.start();
                        break;
                    case "wolverine":
                        String wolverinePrompt = m.toLowerCase().replace(prefix + "wolverine ", "");
                        mc.addReactionsToMessage("\uD83C\uDFDE");

                        Thread wolverineThread = new Thread(() -> {
                            KemoYou.kemoYou("Wolverine", wolverinePrompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                        });
                        wolverineThread.start();
                        break;
                    case "silverfox":
                        String silverfoxPrompt = m.toLowerCase().replace(prefix + "silverfox ", "");
                        mc.addReactionsToMessage("\uD83C\uDFDE");

                        Thread silverfoxThread = new Thread(() -> {
                            KemoYou.kemoYou("SilverFox", silverfoxPrompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                        });
                        silverfoxThread.start();
                        break;
                    case "tact":
                        String tactPrompt = m.toLowerCase().replace(prefix + "tact ", "");
                        mc.addReactionsToMessage("\uD83D\uDE80");

                        Thread tactThread = new Thread(() -> {
                            KemoYou.kemoYou("Tact", tactPrompt, mc);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDE80");
                        });
                        tactThread.start();
                        break;
                    case "bestclient":
                        Color seppuku = new Color(153, 0, 238);
                        EmbedBuilder e = new EmbedBuilder()
                                .setTitle("Seppuku")
                                .setDescription("Seppuku is one of the best clients of all time, ever!")
                                .setAuthor("Seppuku", "https://github.com/seppukudevelopment/seppuku", "https://github.com/seppukudevelopment/seppuku/raw/master/res/seppuku_full.png")
                                .addField("Seppuku Download", "https://github.com/seppukudevelopment/seppuku/releases")
                                .addInlineField("Github", "https://github.com/seppukudevelopment/seppuku")
                                .addInlineField("Website", "https://seppuku.pw")
                                .setColor(seppuku)
                                .setFooter("Seppuku", "https://github.com/seppukudevelopment/seppuku")
                                .setImage("https://github.com/seppukudevelopment/seppuku/blob/master/res/seppuku_full.png?raw=true")
                                .setThumbnail("https://github.com/seppukudevelopment/seppuku/blob/master/src/main/resources/assets/seppukumod/textures/seppuku-logo.png?raw=true");
                        mc.getMessage().reply(e);
                        break;
                    case "remove":
                        //String miatId = mc.getMessage().getMessageReference().get().getMessage().get().getAuthor().getIdAsString();
                        //gets the author id of the message that 'should' have the command response. This is the variable that stores the message that is requested to be deleted.
                        String miatId = mc.getMessage().requestReferencedMessage().get().join().getAuthor().getIdAsString();

                        //String commandIssuer = mc.getMessage().getMessageReference().get().getMessage().get().getReferencedMessage().get().getAuthor().getIdAsString();
                        //gets the author id of the command issuer. This is the variable that stores the author of the original command so only the author can delete their command response.
                        String commandIssuer = mc.getMessage().requestReferencedMessage().get().join().requestReferencedMessage().get().join().getAuthor().getIdAsString();

                        if (miatId.equals(self.getIdAsString()) && mc.getMessageAuthor().getIdAsString().equals(commandIssuer)) {
                            DelOwn.delOwn(mc, api);
                            mc.getMessage().delete();
                        } else {
                            mc.getMessage().reply("You cannot delete others' messages using this command.");
                        }
                        break;
                    case "purge":
                        String amt = m.replace(prefix + "purge ", "");
                        mc.getMessage().reply(Purge.purge(mc, amt));
                        Thread removeNotice = new Thread(() -> {
                            Message notif = mc.getChannel().getMessages(1).join().getNewestMessage().get();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            notif.delete();
                        });
                        removeNotice.start();
                        break;
                    case "translate":
                        String textToTranslate = m.replace(prefix + "translate ", "");
                        mc.getMessage().reply(Trnsl.trnsl(translator, textToTranslate, Language.ENGLISH));
                        break;
                    case "deepl":
                        String deepLTextToTranslate = m.replace(prefix + "deepl ", "");
                        mc.getMessage().reply(DeepL.deepl(deepLTranslator, deepLTextToTranslate, "en-US"));
                        break;
                    case "ml":
                        String toggle = parts[1];
                        String id = mc.getMessageAuthor().getIdAsString();
                        switch (toggle) {
                            case "on":
                                if (Whitelist.whitelisted(id)) {
                                    debugmessagelog = true;
                                    mc.getMessage().reply("Debug Message Log on.");
                                } else {
                                    mc.getMessage().reply("You are not on the debug whitelist.");
                                }
                                break;
                            case "off":
                                if (Whitelist.whitelisted(id)) {
                                    debugmessagelog = false;
                                    mc.getMessage().reply("Debug Message Log off.");
                                } else {
                                    mc.getMessage().reply("You are not on the debug whitelist.");
                                }
                                break;
                        }
                        break;
                }
            }

            for (String wordsGoodWordsExactMatch : reWordsGoodWordsExactMatch) {
                if (m.contains(wordsGoodWordsExactMatch)) {
                    ReWords.scoreModifier(mc, 1);
                }
            }
            for (String reWordsGoodWord : reWordsGoodWords) {
                if (m.toLowerCase().contains(reWordsGoodWord)) {
                    ReWords.scoreModifier(mc, 1);
                }
            }
            for (String wordsBadWordsExactMatch : reWordsBadWordsExactMatch) {
                if (m.contains(wordsBadWordsExactMatch)) {
                    ReWords.scoreModifier(mc, -1);
                }
            }
            for (String reWordsBadWord : reWordsBadWords) {
                if (m.toLowerCase().contains(reWordsBadWord)) {
                    ReWords.scoreModifier(mc, -1);
                }
            }

            //this is hardcoded and will stay hardcoded because i find it funny when people get told to not say the n word.
            //i dont even care about the word itself, its funny to see people act tough against a bot when they say a word.
            if (m.toLowerCase().contains("nigg") || m.toLowerCase().contains("n1gg") || m.toLowerCase().contains("kotlin user")) {
                mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__");
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
            } catch (NoSuchElementException ignored) {
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
            join.setTitle("Hello," + botName + " is here!");
            join.setThumbnail("https://cdn.discordapp.com/attachments/919786447488290816/920839787789836288/miat.jpeg");
            join.addField("Information :", "Slash Commands are supported! \nPrefix : ``" + prefix +"``\nCreator : ``HAV0X#1009`` & ``arsonfrog#9475``");
            join.addField("Get Started :", "Help : ``/miathelp``\nSet Deleted Message Log Channel : ``/setlogchannel``");
            botJoin.getServer().getSystemChannel().get().sendMessage(join);
        });

        api.addReactionAddListener(ra -> {
            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");
            String allemoji = ra.requestMessage().join().getReactions().toString();

            String messageContent = ra.requestMessage().join().getContent();

            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            Language targetLang;
            String dlLang = null;

            switch(emoji) {
                case "❌":
                    if (deleteCandidate.equals(self.getIdAsString())) { //self delete when X emoji is seen
                        if (ra.requestMessage().join().getEmbeds().get(0).getTitle().get().startsWith("Translated Text")) { //only delete if it is a translation message
                            String del = ra.requestMessage().join().getIdAsString();
                            api.getMessageById(del, ra.getChannel()).join().delete();
                        }
                    }
                    return;

                case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
                case "\uD83C\uDDE8\uD83C\uDDE6": //Canada English
                case "\uD83C\uDDFB\uD83C\uDDEE": //US Virgin Islands English
                case "\uD83C\uDDEC\uD83C\uDDFA": //Guam English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-US";
                    break;
                case "\uD83C\uDDE6\uD83C\uDDFA": //Australia English
                case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
                case "\uD83C\uDDF3\uD83C\uDDFF": //New Zealand English
                case "\uD83C\uDDFB\uD83C\uDDEC": //British Virgin Islands English
                case "\uD83C\uDDEC\uD83C\uDDEE": //Gibraltar English
                case "\uD83C\uDDEE\uD83C\uDDF2": //Isle of Man English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-GB";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                    targetLang = Language.POLISH;
                    dlLang = "pl";
                    break;
                case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
                case "\uD83C\uDDF8\uD83C\uDDEC": //Singapore Chinese Simplified
                    targetLang = Language.CHINESE_SIMPLIFIED;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDED\uD83C\uDDF0": //Hong Kong Chinese Traditional
                case "\uD83C\uDDF9\uD83C\uDDFC": //Taiwan Chinese Traditional
                    targetLang = Language.CHINESE_TRADITIONAL;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
                case "\uD83C\uDDF5\uD83C\uDDF0": //Pakistan Hindi
                    targetLang = Language.HINDI;
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
                case "\uD83C\uDDE8\uD83C\uDDF4": //Colombia Spanish
                case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
                case "\uD83C\uDDE6\uD83C\uDDF7": //Argentina Spanish
                case "\uD83C\uDDF5\uD83C\uDDEA": //Peru Spanish
                case "\uD83C\uDDFB\uD83C\uDDEA": //Venezuela Spanish
                case "\uD83C\uDDE8\uD83C\uDDF1": //Chile Spanish
                case "\uD83C\uDDEC\uD83C\uDDF9": //Guatemala Guatemala
                case "\uD83C\uDDF5\uD83C\uDDF7": //Puerto Rico Spanish
                    targetLang = Language.SPANISH;
                    dlLang = "es";
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7": //France French
                case "\uD83C\uDDF2\uD83C\uDDEC": //Madagascar French
                case "\uD83C\uDDE8\uD83C\uDDF2": //Cameroon French
                case "\uD83C\uDDE8\uD83C\uDDEE": //Cote d' Ivoire French
                case "\uD83C\uDDF3\uD83C\uDDEA": //Niger French
                case "\uD83C\uDDE7\uD83C\uDDEF": //Benin French
                    targetLang = Language.FRENCH;
                    dlLang = "fr";
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
                case "\uD83C\uDDE7\uD83C\uDDFE": //Belarus Russian
                    targetLang = Language.RUSSIAN;
                    dlLang = "ru";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
                case "\uD83C\uDDF2\uD83C\uDDFF": //Mozambique Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-PT";
                    break;
                case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-BR";
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
                case "\uD83C\uDDF1\uD83C\uDDEE": //Liechtenstein German
                    targetLang = Language.GERMAN;
                    dlLang = "de";
                    break;
                case "\uD83C\uDDEF\uD83C\uDDF5": //Japan Japanese
                    targetLang = Language.JAPANESE;
                    dlLang = "ja";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDED": //Philippines Filipino
                    targetLang = Language.FILIPINO;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF7": //South Korea Korean
                case "\uD83C\uDDF0\uD83C\uDDF5": //North Korea Korean
                    targetLang = Language.KOREAN;
                    dlLang = "ko";
                    break;
                case "\uD83C\uDDFB\uD83C\uDDF3": //Vietnam Vietnamese
                    targetLang = Language.VIETNAMESE;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF9": //Italy Italian
                    targetLang = Language.ITALIAN;
                    dlLang = "it";
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFE": //Malaysia Malay
                case "\uD83C\uDDE7\uD83C\uDDF3": //Brunei Malay
                    targetLang = Language.MALAY;
                    break;
                default:
                    return;
            }
            if (!Arrays.toString(ignoredChannels).contains(ra.requestMessage().join().getChannel().getIdAsString())) { //if IgnoredChannels does NOT include the reaction channel, continue
                if (allemoji.contains(deepLEmoji)) {
                    if (deeplEnabled) { //Check for the DeepL emoji (the one that says to use DeepL if reacted)
                        if (dlLang == null) { //some languages aren't on DeepL. If the language isn't supported, it will be null because it wasn't set in the switch case
                            if (useGoogleAsFallbackForDeepL) { //if DeepL isn't able to translate into the language, use Google as a fallback translator.
                                ra.getChannel().sendMessage(Trnsl.trnsl(translator, messageContent, targetLang));
                            } else {
                                ra.getChannel().sendMessage("This language is not supported by DeepL.");
                            }
                        } else {
                            ra.getChannel().sendMessage(DeepL.deepl(deepLTranslator, messageContent, dlLang)); //Translate if successful
                        }
                    } else {
                        ra.getChannel().sendMessage(Trnsl.trnsl(translator, messageContent, targetLang)); //use Google if DeepL isnt enabled.
                    }
                } else {
                    ra.getChannel().sendMessage(Trnsl.trnsl(translator, messageContent, targetLang)); //use Google if there isnt a DeepL emoji.
                }
            }
        });
    }
}