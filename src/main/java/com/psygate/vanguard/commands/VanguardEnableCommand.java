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
package com.psygate.vanguard.commands;

import com.psygate.vanguard.Configuration;
import com.psygate.vanguard.Vanguard;
import com.psygate.vanguard.data.AuthType;
import com.psygate.vanguard.data.PlayerSettings;
import com.psygate.vanguard.googleauth.GoogleAuth;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class VanguardEnableCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (!(arg0 instanceof Player)) {
            arg0.sendMessage(Configuration.getString("onlyplayers"));
            return true;
        } else if (arg3.length != 1) {
            arg0.sendMessage(Configuration.getString("methodmissing").replace("$usage$", arg1.getUsage()));
            return true;
        } else {
            Player p = (Player) arg0;

            if (Vanguard.getPlayerSettings(p.getUniqueId()).isEnabled()) {
                p.sendMessage(Configuration
                        .getString("authalreadyenabled"));
                return true;
            } else {
                AuthType type;
                try {
                    type = AuthType.valueOf(arg3[0].toUpperCase());
                    type.initHandler.init(p);
                } catch (IllegalArgumentException e) {
                    p.sendMessage(
                            Configuration
                            .getString("nosuchauth")
                            .replace("$auth$", arg3[0])
                            .split("\n"));
                }
            }
//            try {
//                AuthType type = AuthType.valueOf(arg3[0].toUpperCase());
//                if (type == AuthType.GOOGLE_TOKEN) {
//                    PlayerSettings settings = Vanguard.getPlayerSettings(p.getUniqueId());
//
//                    if (settings.isEnabled()) {
//                        arg0.sendMessage(ChatColor.RED + "Vanguard is already enabled for this account.");
//                        return true;
//                    }
//
//                    settings.setType(type);
//                    settings.setEnabled(true);
//                    settings.setGoogleAuth(new GoogleAuth());
//                    settings.getGoogleAuth().generateSecret();
//                    final UUID uuid = p.getUniqueId();
//                    p.sendMessage(new String[]{
//                        "Do not under any circumstances share this secret or lose your authenticator.",
//                        "Vanguard has been initialized for your account is now active upon login.",
//                        "You will be kicked in 30 seconds and asked to provide the authenticator code upon login.",
//                        "Your UUID is " + uuid.toString() + ", save this, ops will require this to reset your vanguard in case of emergency.",
//                        "- " + ChatColor.GREEN + ChatColor.BOLD + "Your secret is: " + settings.getGoogleAuth().getSecretString()});
//                    if (Vanguard.getPlugin(Vanguard.class).getServer().getScheduler().scheduleSyncDelayedTask(Vanguard.getPlugin(Vanguard.class), new Runnable() {
//
//                        @Override
//                        public void run() {
//                            Player player = Vanguard.getPlugin(Vanguard.class).getServer().getPlayer(uuid);
//                            if (player != null) {
//                                player.kickPlayer("Vanguard initialized.");
//                            }
//                        }
//                    }, 30 * 20) == -1) {
//                        p.sendMessage(ChatColor.RED + "Authentication initialization failed. Please retry.");
//                        return true;
//                    }
//                    Vanguard.savePlayerSettings(p.getUniqueId(), settings);
//                }
//            } catch (IllegalArgumentException e) {
//                arg0.sendMessage(ChatColor.RED + "Unknown auth method: " + arg3[0]);
//            }
//        }
        }
        return true;
    }

}
