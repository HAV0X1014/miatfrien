package miat.UtilityCommands;

import me.bush.translator.Language;
import me.bush.translator.Translator;
import me.bush.translator.Translation;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class Trnsl {
    public static EmbedBuilder trnsl(Translator translator, String textToTranslate, Language targetLang) {
        String translatedText = null;

        Translation tr = translator.translateBlocking(textToTranslate, targetLang, Language.AUTO);
        translatedText = tr.getTranslatedText();

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Translated Text - Google Translate");
        e.setColor(new Color(46,125,50));
        e.setFooter(tr.getSourceLanguage() + " -> " + tr.getTargetLanguage() + "\n(Use ❌ to remove this message.)");
        e.setDescription(translatedText);

        return e;
    }
}