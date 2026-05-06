package nowplaying.utility;

/*  Code sourced from (https://bugs.openjdk.org/browse/JDK-8233878). */
class HiddenUtility {
	
	/*
	 * Copyright (c) 1994, 2019, Oracle and/or its affiliates. All rights reserved.
	 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
	 *
	 * This code is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License version 2 only, as
	 * published by the Free Software Foundation.  Oracle designates this
	 * particular file as subject to the "Classpath" exception as provided
	 * by Oracle in the LICENSE file that accompanied this code.
	 *
	 * This code is distributed in the hope that it will be useful, but WITHOUT
	 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
	 * version 2 for more details (a copy is included in the LICENSE file that
	 * accompanied this code).
	 *
	 * You should have received a copy of the GNU General Public License version
	 * 2 along with this work; if not, write to the Free Software Foundation,
	 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
	 *
	 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
	 * or visit www.oracle.com if you need additional information or have any
	 * questions.
	 */
	
	/**
     * Returns a string whose value is this string, with escape sequences
     * inserted so as to create a string literal whose value is this string.
     * <p>
     * Escape sequences are inserted as follows;
     * <table class="striped">
     *   <caption style="display:none">Escape sequences</caption>
     *   <thead>
     *   <tr>
     *     <th scope="col">Character</th>
     *     <th scope="col">Name</th>
     *     <th scope="col">Escape</th>
     *   </tr>
     *   </thead>
     *   <tbody>
     *   <tr>
     *     <th scope="row">{@code U+0008}</td>
     *     <td>backspace</td>
     *     <td>{@code \u005Cb}</th>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code U+0009}</td>
     *     <td>horizontal tab</td>
     *     <td>{@code \u005Ct}</th>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code U+000A}</td>
     *     <td>line feed</td>
     *     <td>{@code \u005Cn}</th>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code U+000C}</td>
     *     <td>form feed</td>
     *     <td>{@code \u005Cf}</th>
     *   </tr>
     *   <t<th scope="row">    *     <td>{@code U+000D}</td>
     *     <td>{@code \u005Cr}</th>
     *     <td>carriage return</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code U+0022}</td>
     *     <td>double quote</td>
     *     <td>{@code \u005C"}</th>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code U+0027}</td>
     *     <td>single quote</td>
     *     <td>{@code \u005C'}</th>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code U+005C}</td>
     *     <td>backslash</td>
     *     <td>{@code \u005C\u005C}</th>
     *   </tr>
     *   <tr>
     *     <th scope="row">(all other characters)</td>
     *     <td>regular character</td>
     *     <td>(unchanged)</th>
     *   </tr>
     *   </tbody>
     * </table>
     *
     * @implNote
     * This method does <em>not</em> insert Unicode escapes such as "{@code \u005cu2022}".
     * Unicode escapes are translated by the Java compiler when reading input characters and
     * are not part of the string literal specification.
     *
     * @return String with escape sequences inserted.
     *
     * @see String#translateEscapes
     *
     * @jls 3.10.7 Escape Sequences
     *
     * @since 13
     */
    public static String insertEscapes(String string) {
        if (string.isEmpty()) {
            return "";
        }
        char[] chars = string.toCharArray();
        // The next three variables may change if chars is reallocated:
        int firstCharToTranslate = 0;
        int from = chars.length;
        int to = from;
        while (from > firstCharToTranslate) {
            // invariant:  chars[--to] is a valid element
            char ch = chars[--from];
            switch (ch) {
            case '\b':
                ch = 'b';
                break;
            case '\f':
                ch = 'f';
                break;
            case '\n':
                ch = 'n';
                break;
            case '\r':
                ch = 'r';
                break;
            case '\t':
                ch = 't';
                break;
            case '\'':
            case '\"':
            case '\\':
                // as is
                break;
            default:
                chars[--to] = ch;
                continue;
            }
            if (to < 2) {
                // oops, we need to expand chars
                assert(to <= from);
                int grow = Math.max(10, (from - to) * 2);
                int gotSoFar = chars.length;
                int newLen = chars.length + grow;
                char[] newChars = new char[newLen];
                System.arraycopy(chars, 0, newChars, newLen - gotSoFar, gotSoFar);
                to += grow;
                from += grow;
                firstCharToTranslate += grow;
            }
            chars[--to] = '\\';
            chars[--to] = ch;
        }

        if (from == to)  return string;  // no length change => no change

        return new String(chars, to, chars.length);
    }

}
