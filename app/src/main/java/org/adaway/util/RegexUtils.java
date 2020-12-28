/*
 * Copyright (C) 2011-2012 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This file is part of AdAway.
 *
 * AdAway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdAway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdAway.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.adaway.util;

import com.google.common.net.InetAddresses;

import org.apache.commons.validator.routines.DomainValidator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

public class RegexUtils {
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("[*?]");
    private static final DomainValidator DOMAIN_VALIDATOR = DomainValidator.getInstance(true);

    /**
     * Check whether a hostname is valid.
     *
     * @param hostname The hostname to validate.
     * @return return {@code true} if hostname is valid, {@code false} otherwise.
     */
    public static boolean isValidHostname(String hostname) {
//        try {
////            ExecutorService executor = AppExecutors.getInstance().networkIO();
////            return executor.submit(() -> DOMAIN_VALIDATOR.isValid(hostname)).get();
//            return ForkJoinPool.commonPool().submit(() -> DOMAIN_VALIDATOR.isValid(hostname)).get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return false;
        return DOMAIN_VALIDATOR.isValid(hostname);
//        return InternetDomainName.isValid(hostname);
    }

    /**
     * Check whether a wildcard hostname is valid.
     * Wildcard hostname is an hostname with one of the following wildcard:
     * <ul>
     * <li>{@code *} for any character sequence,</li>
     * <li>{@code ?} for any character</li>
     * </ul>
     * <p/>
     * Wildcard validation is quite tricky, because wildcards can be placed anywhere and can match with
     * anything. To make sure we don't dismiss certain valid wildcard host names, we trim wildcards
     * or replace them with an alphanumeric character for further validation.<br/>
     * We only reject whitelist host names which cannot match against valid host names under any circumstances.
     *
     * @param hostname The wildcard hostname to validate.
     * @return return {@code true} if wildcard hostname is valid, {@code false} otherwise.
     */
    public static boolean isValidWildcardHostname(String hostname) {
        // Clear wildcards from host name then validate it
        String clearedHostname = WILDCARD_PATTERN.matcher(hostname).replaceAll("");
        // Replace wildcards from host name by an alphanumeric character
        String replacedHostname = WILDCARD_PATTERN.matcher(hostname).replaceAll("a"); // TODO Use only one matcher?
        // Check if any hostname is valid
        return isValidHostname(clearedHostname) || isValidHostname(replacedHostname);
    }

    /**
     * Check if an IP address is valid.
     *
     * @param ip The IP to validate.
     * @return {@code true} if the IP is valid, {@code false} otherwise.
     */
    public static boolean isValidIP(String ip) {
        try {
            InetAddresses.forString(ip);
            return true;
        } catch (IllegalArgumentException exception) {
            Log.d(Constants.TAG, "Invalid IP address: " + ip, exception);
            return false;
        }
    }

    /*
     * Transforms String with * and ? characters to regex String, convert "example*.*" to regex
     * "^example.*\\..*$", from http://www.rgagnon.com/javadetails/java-0515.html
     */
    public static String wildcardToRegex(String wildcard) {
        StringBuilder regex = new StringBuilder(wildcard.length());
        regex.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch (c) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append(".");
                    break;
                // escape special regex-characters
                case '(':
                case ')':
                case '[':
                case ']':
                case '$':
                case '^':
                case '.':
                case '{':
                case '}':
                case '|':
                case '\\':
                    regex.append("\\");
                    regex.append(c);
                    break;
                default:
                    regex.append(c);
                    break;
            }
        }
        regex.append('$');
        return regex.toString();
    }
}
