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
package com.psygate.vanguard.data;

import com.psygate.vanguard.auth.token.GoogleLoginHandler;
import com.psygate.vanguard.auth.token.InitHandler;
import com.psygate.vanguard.auth.token.GoogleTokenInitHandler;
import com.psygate.vanguard.auth.token.GoogleTokenSubmissionHandler;
import com.psygate.vanguard.auth.token.LoginHandler;
import com.psygate.vanguard.auth.token.TokenSubmissionHandler;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public enum AuthType {

    GOOGLE_TOKEN(new GoogleTokenInitHandler(),
            new GoogleLoginHandler(),
            new GoogleTokenSubmissionHandler());

    public final InitHandler initHandler;
    public final LoginHandler loginHandler;
    public final TokenSubmissionHandler tokenHandler;
    public boolean isEnabled = true;
    public String description = "Unavailable";

    private AuthType(InitHandler inithandler, LoginHandler loginhandler, TokenSubmissionHandler tokenhandler) {
        this.initHandler = inithandler;
        this.loginHandler = loginhandler;
        this.tokenHandler = tokenhandler;
    }
}
