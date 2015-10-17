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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.encoder.QRCode;
import com.psygate.vanguard.Configuration;
import com.psygate.vanguard.Vanguard;
import com.psygate.vanguard.data.AuthType;
import com.psygate.vanguard.data.PlayerSettings;
import com.psygate.vanguard.googleauth.GoogleAuth;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class GoogleTokenInitHandler implements InitHandler {

    @Override
    public boolean init(Player p) {
        PlayerSettings settings = Vanguard.getPlayerSettings(p.getUniqueId());
        if (settings.isEnabled()) {
            return false;
        }

        settings.setType(AuthType.GOOGLE_TOKEN);
        settings.setGoogleAuth(new GoogleAuth());
        settings.getGoogleAuth().generateSecret();

        final String secret = settings.getGoogleAuth().getSecretString();
        p.sendMessage(Configuration.getString("googletokeninit")
                .replace("$uuid$", p.getUniqueId().toString())
                .replace("$secret$", secret));
        settings.setEnabled(true);
        Vanguard.savePlayerSettings(p.getUniqueId(), settings);
        String uri = "otpauth://totp/CivExMC:" + p.getName() + "?secret=" + secret + "&issuer=Vanguard_Plugin";
//
//        QRCode qr = new QRCode();
//        qr.setECLevel(ErrorCorrectionLevel.L);
//        qr.setMode(Mode.ALPHANUMERIC);
        QRCodeWriter w = new QRCodeWriter();
        try {
            BitMatrix matrix = w.encode(uri, BarcodeFormat.QR_CODE, 5, 5);
            for (int y = 0; y < 50; y++) {
                for (int x = 0; x < matrix.getWidth(); x++) {
                    for (int z = 0; z < matrix.getHeight(); z++) {

                        Material m = Material.AIR;
                        if (y == 49) {
                            m = matrix.get(x, z) ? Material.OBSIDIAN : Material.WOOL;// Material.GLOWSTONE;
                        }

                        Location loc = p.getLocation().clone().add(x - matrix.getWidth() / 2, y, z - matrix.getHeight() / 2);
                        p.sendBlockChange(loc, m, (byte) 0);
                    }
                }
            }
        } catch (WriterException ex) {
            throw new RuntimeException(ex);
        }

        return true;
    }

}
