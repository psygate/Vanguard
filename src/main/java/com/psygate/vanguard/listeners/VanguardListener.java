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
package com.psygate.vanguard.listeners;

import com.psygate.vanguard.Configuration;
import com.psygate.vanguard.Vanguard;
import com.psygate.vanguard.data.AuthType;
import com.psygate.vanguard.data.LoginRequest;
import com.psygate.vanguard.data.PlayerSettings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class VanguardListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void login(PlayerJoinEvent ev) {
        PlayerSettings settings = Vanguard.getPlayerSettings(ev.getPlayer().getUniqueId());

        if (settings.isEnabled()) {
//            ev.getPlayer().sendMessage(new String[]{vLoginMsg, vCheckMsg, vLockoutMsg});
//            locked.add(ev.getPlayer().getUniqueId());

            settings.getType().loginHandler.login(ev.getPlayer().getUniqueId(), settings);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preAuth(AsyncPlayerPreLoginEvent ev) {
        PlayerSettings settings = Vanguard.getPlayerSettings(ev.getUniqueId());
        if (settings.getFailedAttempts() >= Configuration.getMaxAttemptFailure()) {
            ev.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            ev.setKickMessage(Configuration.getString("toomanyfailedattempts"));
        } else if (settings.getBanned() >= System.currentTimeMillis()) {
            ev.setKickMessage(Configuration.getString("vanguardauthfailure")
                    .replace("$timeout$",
                            Long.toString(Configuration.getBanTimeOnFail())));

            ev.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent ev) {
        if (Vanguard.isLocked(ev.getPlayer().getUniqueId())) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void chat(AsyncPlayerChatEvent ev) {
//        Bukkit.broadcastMessage("Chat event.");
        if (Vanguard.isLocked(ev.getPlayer().getUniqueId())) {
            ev.getPlayer().sendMessage(Configuration.getString("googletokenlogin")
                    .replace("$timeout$", Long.toString(Configuration.getTimeoutSeconds()))
                    .split("\n"));
            ev.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void move(PlayerAnimationEvent ev) {
        if (Vanguard.isLocked(ev.getPlayer().getUniqueId())) {
//            ev.getPlayer().sendMessage(Configuration.getStringContainer()
//                    .getString("googletokenlogin")
//                    .replace("$timeout$", Long.toString(Configuration.getTimeoutSeconds()))
//                    .split("\n"));
            ev.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void move(PlayerPickupItemEvent ev) {
        if (Vanguard.isLocked(ev.getPlayer().getUniqueId())) {
//            ev.getPlayer().sendMessage(Configuration.getStringContainer()
//                    .getString("googletokenlogin")
//                    .replace("$timeout$", Long.toString(Configuration.getTimeoutSeconds()))
//                    .split("\n"));
            ev.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void move(PlayerMoveEvent ev) {
        if (Vanguard.isLocked(ev.getPlayer().getUniqueId())) {
//            ev.getPlayer().sendMessage(Configuration.getStringContainer()
//                    .getString("googletokenlogin")
//                    .replace("$timeout$", Long.toString(Configuration.getTimeoutSeconds()))
//                    .split("\n"));
            ev.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent ev) {
        if (ev.getEntity() instanceof Player) {
            Player p = (Player) ev.getEntity();
            if (Vanguard.isLocked(p.getUniqueId())) {
//            ev.getPlayer().sendMessage(Configuration.getStringContainer()
//                    .getString("googletokenlogin")
//                    .replace("$timeout$", Long.toString(Configuration.getTimeoutSeconds()))
//                    .split("\n"));
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void damage(PlayerCommandPreprocessEvent ev) {
        if (ev.getPlayer() != null) {
            Player p = ev.getPlayer();

            if (Vanguard.isLocked(p.getUniqueId())) {
                if (!ev.getMessage().contains("vanguard")) {
                    Vanguard.getPlugin(Vanguard.class).getLogger().log(Level.INFO, "Stopped: {0}: {1}", new Object[]{p.getName(), ev.getMessage()});
                    ev.setCancelled(true);
                }
                p.sendMessage("You cannot issue this command.");
            }
        }
    }
}
