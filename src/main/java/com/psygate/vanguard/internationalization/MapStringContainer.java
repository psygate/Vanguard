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
package com.psygate.vanguard.internationalization;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Color;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class MapStringContainer implements StringContainer {

    private final String lang;
    private final Map<String, String> strings;

    public MapStringContainer(final String lang, final Map<String, String> strings) {
        this.lang = Objects.requireNonNull(lang, "Lang cannot be null.");
        this.strings = new HashMap<>(Objects.requireNonNull(strings, "Strings cannot be null."));

        initColors();
    }

    public MapStringContainer(final String lang, final Properties strings) {
        this.lang = Objects.requireNonNull(lang, "Lang cannot be null.");
        Objects.requireNonNull(strings, "Strings cannot be null.");
        this.strings = new HashMap<>();

        for (Object okey : strings.keySet()) {
            if (!(okey instanceof String)) {
                throw new IllegalArgumentException("Language string keys must be string.");
            } else {
                String val = strings.getProperty((String) okey);
                this.strings.put((String) okey, val);
            }
        }

        initColors();
    }

    private void initColors() {
        for (String key : strings.keySet()) {
            String langstring = strings.get(key);

            for (ChatColor color : ChatColor.values()) {
                langstring = langstring.replace("$" + color.name() + "$", color.toString());
            }

            strings.put(key, langstring);

        }
    }

    @Override
    public String getString(String name) {
        return Objects.requireNonNull(strings.get(name), "String " + name + " not found.");
    }
}
