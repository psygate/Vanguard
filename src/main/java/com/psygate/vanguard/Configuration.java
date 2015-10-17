/*
 The MIT License (MIT)

 Copyright (c) 2015 psygate (https://github.com/psygate)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.psygate.vanguard;

import com.psygate.vanguard.data.AuthType;
import com.psygate.vanguard.internationalization.MapStringContainer;
import com.psygate.vanguard.internationalization.StringContainer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class Configuration {

    private static FileConfiguration conf;
    private static ArrayList<AuthType> enabledTypes = new ArrayList<>(AuthType.values().length);
    private static StringContainer language;

    public static void init(FileConfiguration conf) {
        Configuration.conf = Objects.requireNonNull(conf);
        enabledTypes.clear();

        for (String name : conf.getStringList("enabled_types")) {
            AuthType type = AuthType.valueOf(name.toUpperCase());
            type.description = conf.getString("descriptions." + name);
            enabledTypes.add(type);
        }

        String lang = conf.getString("lang");

        loadLangContainer(lang);

//        language = Internationalization.getContainerForLang(conf.getString("lang"));
    }

    public static long getTimeoutSeconds() {
        return conf.getLong("auth.auth_timeout");
    }

    public static List<AuthType> getAuthTypes() {
        return Collections.unmodifiableList(enabledTypes);
    }

    public static long getBanTimeOnFail() {
        return conf.getLong("auth.ban_time_on_fail");
    }

    public static int getMaxAttemptFailure() {
        return conf.getInt("auth.max_failed_attempts");
    }

    private static void loadLangContainer(String lang) {
        Class<?> context = Vanguard.getPlugin(Vanguard.class).getClass();
        String filename = "/" + lang + ".properties";
        try (InputStream in = context.getResourceAsStream(filename)) {

            Properties strings = new Properties();
            try {
                strings.load(in);
            } catch (ZipException e) {
                strings.load(new ZipInputStream(in));
            }

//            System.out.println(strings);
            StringContainer llanguage = new MapStringContainer(lang, strings);
            Configuration.language = llanguage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getString(String name) {
        return language.getString(name);
    }
}
