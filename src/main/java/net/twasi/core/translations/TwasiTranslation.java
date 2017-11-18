package net.twasi.core.translations;

import net.twasi.core.database.models.Language;
import net.twasi.core.database.models.User;
import net.twasi.core.logger.TwasiLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class TwasiTranslation {
    private ClassLoader classLoader;

    public TwasiTranslation(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getTranslation(Language language, String translationKey) {
        URL resource = classLoader.getResource("translations/" + language.toString() + ".lang");

        if (resource == null && language == Language.EN_GB) {
            TwasiLogger.log.error("Default English language not found in classLoader resources folder. Please create a folder 'translations' with the file 'EN_GB.lang' in it.");
            return translationKey;
        }

        if (resource == null) {
            return getTranslation(Language.EN_GB, translationKey);
        }

        File translationFile = new File(resource.getFile());

        try (Scanner scanner = new Scanner(translationFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(translationKey + "=")) {
                    return line.substring(translationKey.length() + 1);
                }
            }
        } catch (IOException e) {
            TwasiLogger.log.error(e);
            e.printStackTrace();
        }

        if (language == Language.EN_GB) {
            TwasiLogger.log.warn("Unknown translation key '" + translationKey + "' searched, but not found. This may lead to unexpected results.");
            return translationKey;
        }

        // Default to english if nothing was found
        return getTranslation(Language.EN_GB, translationKey);
    }

    public String getTranslation(User user, String translationKey) {
        return getTranslation(user.getLanguage(), translationKey);
    }

    public String getTranslation(User user, String translationKey, Object... objects) {
        return String.format(getTranslation(user, translationKey), objects);
    }
}