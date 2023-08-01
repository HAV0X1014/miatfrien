package miat.UtilityCommands;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import miat.FileHandlers.ReadFirstLine;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;


public class DeepL {
    public static EmbedBuilder deepl(Translator deepLTranslator, String textToTranslate, String targetLang) {
        TextResult translatedText = null;
        try {
            translatedText = deepLTranslator.translateText(textToTranslate, null, targetLang);
        } catch (DeepLException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Translated Text - DeepL");
        e.setColor(Color.CYAN);
        e.setFooter(translatedText.getDetectedSourceLanguage() + " -> " + targetLang);
        e.setDescription(translatedText.getText());

        return e;
    }
}