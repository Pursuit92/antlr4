/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LuaParserDescriptor {
    public static class LuaParser extends BaseParserTestDescriptor {
        public String output = null;
        public String errors = null;
        public String startRule = "chunk";
        public String grammarName = "Lua";

        /** Look for input as resource */
        @Override
        public String getInput() {
            String input = null;

            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final URL stuff = loader.getResource("org/antlr/v4/test/runtime/LuaInput.lua");
            try {
                input = new String(Files.readAllBytes(Paths.get(stuff.toURI())));
            }
            catch (Exception e) {
                System.err.println("Cannot find input org/antlr/v4/test/runtime/LuaInput.lua");
            }

            return input;
        }

        /** Look for grammar as resource */
        @Override
        public Pair<String, String> getGrammar() {
            String grammar = null;

            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final URL stuff = loader.getResource("org/antlr/v4/test/runtime/LuaST.g4");
            try {
                grammar = new String(Files.readAllBytes(Paths.get(stuff.toURI())));
            }
            catch (Exception e) {
                System.err.println("Cannot find grammar org/antlr/v4/test/runtime/LuaST.g4");
            }

            return new Pair<>(grammarName, grammar);
        }
    }
}
