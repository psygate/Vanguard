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
package com.psygate.vanguard.auth.token;

import com.google.common.base.Objects;
import com.psygate.vanguard.Configuration;
import com.psygate.vanguard.Vanguard;
import com.psygate.vanguard.data.PlayerSettings;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class GoogleTokenSubmissionHandler implements TokenSubmissionHandler {

    @Override
    public void handleToken(UUID uuid, String token, PlayerSettings settings) {
        String stoken = settings.getGoogleAuth().generateToken();
        Player p = Vanguard.getPlugin(Vanguard.class).getServer().getPlayer(uuid);

        if (p == null) {
            return;
        }

        if (!Objects.equal(token, stoken)) {
            p.kickPlayer(ChatColor.RED + "Invalid token.");
            Vanguard.getPlugin(Vanguard.class).getLogger().log(Level.INFO, "Failed attempt for {0} Token: {1} Provided: {2}", new Object[]{uuid, stoken, token});
            settings.setBanned(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Configuration.getBanTimeOnFail()));
            settings.setFailedAttempts(settings.getFailedAttempts() + 1);
            Vanguard.savePlayerSettings(uuid, settings);
        } else {
            Vanguard.unlock(uuid);
            p.sendMessage(ChatColor.GREEN + "Token accepted.");

            if (settings.getFailedAttempts() > 0) {
                p.sendMessage(ChatColor.RED + "There have been " + settings.getFailedAttempts() + " failed attempts to log in.");
                settings.setFailedAttempts(0);
                Vanguard.savePlayerSettings(uuid, settings);
            }
        }

    }

}
