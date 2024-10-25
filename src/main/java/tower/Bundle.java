package tower;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Structs;

public class Bundle {
    private static final ObjectMap<Locale, ResourceBundle> bundles = new ObjectMap<>();
    private static final ObjectMap<Locale, MessageFormat> formats = new ObjectMap<>();

    public static final Locale defaultLocale = new Locale("en");
    public static final Locale[] supportedLocales;

    private Bundle() {
    }

    static {
        Fi file = new Fi("src/main/resources/bundles/bundle_en.properties");
        if (file.exists()) {
            String code = file.nameWithoutExtension();
            String[] codes;
            if ((codes = code.replace("bundle_", "").split("_")).length == 1)
                supportedLocales = new Locale[] { new Locale(codes[0]) };
            else
                supportedLocales = new Locale[] { new Locale(codes[0], codes[1]) };
        } else {
            supportedLocales = new Locale[] { defaultLocale };
        }
    }

    private static ResourceBundle getResource(Locale locale) {
        ResourceBundle bundle = bundles.get(locale);
        if (bundle == null) {
            if (Structs.contains(supportedLocales, locale))
                bundles.put(locale, bundle = ResourceBundle.getBundle("bundles.bundle", locale));
            else
                bundle = getResource(defaultLocale);
        }
        return bundle;
    }

    public static Locale findLocale(String localeString) {
        Locale locale = Structs.find(supportedLocales,
                l -> localeString.equals(l.toString()) || localeString.startsWith(l.toString()));
        return locale != null ? locale : defaultLocale;
    }

    public static String format(String key, Locale locale, Object... values) {
        String pattern = get(key, locale);
        if (values.length == 0)
            return pattern;

        MessageFormat format = formats.get(locale);
        if (format == null)
            formats.put(locale, format = new MessageFormat(pattern, locale));
        else if (!Structs.contains(supportedLocales, locale))
            format = formats.get(defaultLocale, () -> new MessageFormat(pattern, defaultLocale));
        format.applyPattern(pattern);
        return format.format(values);
    }

    public static String format(String key, Object... values) {
        return format(key, defaultLocale, values);
    }

    public static String get(String key, Locale locale) {
        try {
            return getResource(locale).getString(key);
        } catch (MissingResourceException e) {
            Log.err("Key '@' doesn't exist in locale '@'", key, locale);
            return (locale.equals(defaultLocale) ) ? "?" + key + "?" : get(key);
        }
    }

    public static String get(String key) {
        return get(key, defaultLocale);
    }

    public static String get(String key, String locale) {
        return get(key, findLocale(locale));
    }
}