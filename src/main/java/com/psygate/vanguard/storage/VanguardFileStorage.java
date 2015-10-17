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
package com.psygate.vanguard.storage;

import com.google.common.base.Ascii;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.psygate.vanguard.data.AuthType;
import com.psygate.vanguard.data.PlayerSettings;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class VanguardFileStorage implements VanguardStorage {

    private final Path basepath;
    private final GsonBuilder bgson = new GsonBuilder().setPrettyPrinting();

    public VanguardFileStorage(Path basepath) {
        this.basepath = requireFolder(basepath);
    }

    public VanguardFileStorage(File basepath) {
        this.basepath = requireFolder(basepath.toPath());
    }

    private Path requireFolder(Path toPath) {
        Objects.requireNonNull(toPath);

        try {
            if (!Files.exists(toPath)) {
                Files.createDirectories(toPath);
            } else if (!Files.isWritable(toPath) || !Files.isDirectory(toPath)) {
                throw new IllegalStateException("Base path unusable. " + toPath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return toPath;
    }

    @Override
    public Path getBasePath() {
        return basepath;
    }

    @Override
    public PlayerSettings getPlayerSettings(UUID playerid) {
        if (!Files.exists(getUserFile(playerid))) {
            throw new IllegalArgumentException();
        }

        try (JsonReader writer = new JsonReader(new FileReader(getUserFile(playerid).toFile()))) {
            return bgson.create().fromJson(writer, PlayerSettings.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void savePlayerSettings(UUID playerid, PlayerSettings settings) {
        try (JsonWriter writer = new JsonWriter(new FileWriter(getUserFile(playerid).toFile()))) {
            bgson.create().toJson(settings, PlayerSettings.class, writer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deletePlayerSettings(UUID playerid) {
        try {
            Files.deleteIfExists(getUserFile(playerid));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PlayerSettings getOrCreatePlayerSettings(UUID playerid) {
        if (!Files.exists(getUserFile(playerid))) {
            return new PlayerSettings(playerid);
        } else {
            return getPlayerSettings(playerid);
        }
    }

    private Path getUserFile(UUID playerid) {
        return basepath.resolve(playerid.toString() + ".vanguard.dat");
    }
}
