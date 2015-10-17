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

import com.psygate.vanguard.commands.VanguardEnableCommand;
import com.psygate.vanguard.commands.VanguardListCommand;
import com.psygate.vanguard.commands.VanguardTokenCommand;
import com.psygate.vanguard.data.AuthType;
import com.psygate.vanguard.data.LoginRequest;
import com.psygate.vanguard.data.PlayerSettings;
import com.psygate.vanguard.googleauth.GoogleAuth;
import com.psygate.vanguard.listeners.VanguardListener;
import com.psygate.vanguard.storage.VanguardFileStorage;
import com.psygate.vanguard.storage.VanguardStorage;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class Vanguard extends JavaPlugin {

    private static Vanguard instance;

    private VanguardStorage storage;
    private final Set<UUID> lockedPlayers = new HashSet<>();

    public static PlayerSettings getPlayerSettings(UUID uniqueId) {
        return instance.storage.getOrCreatePlayerSettings(uniqueId);
    }

    public static void savePlayerSettings(UUID uniqueId, PlayerSettings settings) {
        instance.storage.savePlayerSettings(uniqueId, settings);
    }

    @Override
    public void onEnable() {
        instance = this;
        storage = new VanguardFileStorage(getDataFolder().toPath().resolve("vanguard_player_data"));
        getLogger().log(Level.INFO, "{0}Vanguard enabled.", ChatColor.GREEN);
        Configuration.init(getConfig());
        registerEventHandlers(new VanguardListener());
        registerCommands();
    }

    @Override
    public void onDisable() {
        for (UUID uuid : lockedPlayers) {
            Player p = getServer().getPlayer(uuid);
            if (p != null) {
                p.kickPlayer(Configuration.getString("kickedbyreload"));
            }
        }
    }

    private void registerEventHandlers(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommands() {
        getServer().getPluginCommand("enable").setExecutor(new VanguardEnableCommand());
        getServer().getPluginCommand("available").setExecutor(new VanguardListCommand());
        getServer().getPluginCommand("token").setExecutor(new VanguardTokenCommand());

    }

    public static void lock(UUID player) {
        instance.lockedPlayers.add(Objects.requireNonNull(player));
    }

    public static boolean isLocked(UUID player) {
        return instance.lockedPlayers.contains(player);
    }

    public static void unlock(UUID uuid) {
        instance.lockedPlayers.remove(uuid);
    }

}
