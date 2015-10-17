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
package com.psygate.vanguard.googleauth;

import com.google.common.io.BaseEncoding;
import com.sun.crypto.provider.HmacSHA1;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class GoogleAuth {

    private int PASS_CODE_LENGTH = 6;
    private int INTERVAL = 30;
//    private int ADJACENT_INTERVALS = 1;

    private int PIN_MODULO = (int) Math.pow(10, PASS_CODE_LENGTH);
    private int SECRET_SIZE = 80;
    private byte[] secret = new byte[SECRET_SIZE / 8];
    private long unixTimeCreation = System.currentTimeMillis() / 1000;

    public void generateSecret() {
        final SecureRandom rand = new SecureRandom();
        secret = new byte[SECRET_SIZE / 8];

        rand.nextBytes(secret);

//        for (int i = 0; i < SECRET_SIZE / 8; i++) {
//            secret[i] = (byte) i;
//        }
    }

    public byte[] getSecret() {
        return secret;
    }

    public int getSECRET_SIZE() {
        return SECRET_SIZE;
    }

    public void setSECRET_SIZE(int SECRET_SIZE) {
        this.SECRET_SIZE = SECRET_SIZE;
    }

    public long getUnixTimeCreation() {
        return unixTimeCreation;
    }

    public void setUnixTimeCreation(long unixTimeCreation) {
        this.unixTimeCreation = unixTimeCreation;
    }

    public String getSecretString() {
        return BaseEncoding.base32().encode(secret);
    }

    public String generateToken() {
        Objects.requireNonNull(secret);
        long message = ((System.currentTimeMillis() / 1000) / INTERVAL);
        MessageDigest mc;
        try {
            final String algoname = "HMACSHA1";
            Mac mac = Mac.getInstance(algoname);
            mac.init(new SecretKeySpec(secret, ""));
//            SecretKeySpec signingKey = new SecretKeySpec(secret, algoname);
//            mac.init(signingKey);
            byte[] digest = mac.doFinal(ByteBuffer.allocate(8).putLong(message).array());
//            mc.update(secret);
//            mc.update(longAsByteArray(message));
//            byte[] digest = mc.digest();
//            byte[] digest = mac.doFinal();

            int offset = digest[digest.length - 1] & 0xF;
            int truncatedHash = hashToInt(digest, offset) & 0x7FFFFFFF;
            int pin = truncatedHash % PIN_MODULO;

            return padPin(pin, PASS_CODE_LENGTH);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new RuntimeException(ex);
        }

    }

    private static String padPin(int pin, int length) {
        String out = Integer.toString(pin);

        while (out.length() < length) {
            out = "0" + out;
        }

        return out;
    }

    private static int hashToInt(byte[] bytes, int start) {
        DataInput input = new DataInputStream(
                new ByteArrayInputStream(bytes, start, bytes.length - start));
        int val;
        try {
            val = input.readInt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return val;
    }
}
