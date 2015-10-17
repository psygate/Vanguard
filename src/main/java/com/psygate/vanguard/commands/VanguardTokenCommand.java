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
import com.psygate.vanguard.data.PlayerSettings;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class VanguardTokenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(Configuration.getString("onlyplayers"));
        } else {
            Player p = (Player) cs;
            if (!Vanguard.isLocked(p.getUniqueId())) {
                p.sendMessage(Configuration.getString("notokennow"));
            } else {
                if (args.length != 1) {
                    p.sendMessage(cmnd.getUsage());
                } else {
                    try {
                        PlayerSettings settings = Vanguard.getPlayerSettings(p.getUniqueId());
                        if (!settings.isEnabled()) {
                            p.sendMessage("Vanguard not enabled.");
                        } else {
                            settings.getType().tokenHandler.handleToken(p.getUniqueId(), args[0], settings);
                        }
                    } catch (Exception e) {
                        p.kickPlayer("Internal error. Contact admins.");
                        Logger.getLogger(VanguardTokenCommand.class.getName()).log(Level.SEVERE, "Token input error.", e);
                        return true;
                    }

                }
            }
        }

        return true;
    }

}
