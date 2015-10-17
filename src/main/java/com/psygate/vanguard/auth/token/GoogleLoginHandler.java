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

import com.psygate.vanguard.Configuration;
import com.psygate.vanguard.Vanguard;
import com.psygate.vanguard.data.PlayerSettings;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class GoogleLoginHandler implements LoginHandler {

    @Override
    public void login(UUID player, PlayerSettings settings) {
        Player p = Vanguard.getPlugin(Vanguard.class).getServer().getPlayer(player);

        if (p != null) {
            Vanguard.lock(player);

            p.sendMessage(Configuration
                    .getString("googletokenlogin")
                    .replace("$timeout$", Long.toString(Configuration.getTimeoutSeconds()))
                    .split("\n"));
            Vanguard.lock(player);
        }
    }

}
