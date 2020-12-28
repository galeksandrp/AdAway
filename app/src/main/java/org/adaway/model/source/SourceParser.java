package org.adaway.model.source;

import org.adaway.db.entity.HostListItem;
import org.adaway.db.entity.HostsSource;
import org.adaway.db.entity.ListType;
import org.adaway.util.Log;
import org.adaway.util.RegexUtils;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.adaway.db.entity.ListType.BLOCKED;
import static org.adaway.db.entity.ListType.REDIRECTED;
import static org.adaway.util.Constants.BOGUS_IPv4;
import static org.adaway.util.Constants.LOCALHOST_HOSTNAME;
import static org.adaway.util.Constants.LOCALHOST_IPv4;
import static org.adaway.util.Constants.LOCALHOST_IPv6;

/**
 * This class is an {@link org.adaway.db.entity.HostsSource} parser.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
class SourceParser {
    private static final String TAG = "SourceParser";
    private static final String HOSTS_PARSER = "^\\s*([^#\\s]+)\\s+([^#\\s]+)\\s*(?:#.*)*$";
    static final Pattern HOSTS_PARSER_PATTERN = Pattern.compile(HOSTS_PARSER);

    private final int sourceId;
    private final boolean parseRedirectedHosts;
    private final Stream<HostListItem> itemStream;

    SourceParser(HostsSource hostsSource, Reader reader) {
        this.sourceId = hostsSource.getId();
        this.parseRedirectedHosts = hostsSource.isRedirectEnabled();
        this.itemStream = buildItemStream(reader);
    }

    private Stream<HostListItem> buildItemStream(Reader reader) {
        return new BufferedReader(reader)
                .lines()
//                .unordered()
                .parallel()
                .map(this::parseHostListItem)
                .filter(Objects::nonNull)
                .filter(this::isRedirectionValid)
                .filter(this::isHostValid);
    }

    public Stream<HostListItem> getItemStream() {
        return this.itemStream;
    }

    private HostListItem parseHostListItem(String line) {
        Matcher matcher = HOSTS_PARSER_PATTERN.matcher(line);
        if (!matcher.matches()) {
            Log.d(TAG, "Does not match: " + line);
            return null;
        }
        // Check IP address validity or while list entry (if allowed)
        String ip = matcher.group(1);
        String hostname = matcher.group(2);
        // Skip localhost name
        if (LOCALHOST_HOSTNAME.equals(hostname)) {
            return null;
        }
        // check if ip is 127.0.0.1 or 0.0.0.0
        ListType type;
        if (LOCALHOST_IPv4.equals(ip)
                || BOGUS_IPv4.equals(ip)
                || LOCALHOST_IPv6.equals(ip)) {
            type = BLOCKED;
        } else if (this.parseRedirectedHosts) {
            type = REDIRECTED;
        } else {
            return null;
        }
        HostListItem item = new HostListItem();
        item.setType(type);
        item.setHost(hostname);
        item.setEnabled(true);
        if (type == REDIRECTED) {
            item.setRedirection(ip);
        }
        item.setSourceId(this.sourceId);
        return item;
    }

    private boolean isRedirectionValid(HostListItem item) {
        return item.getType() != REDIRECTED || RegexUtils.isValidIP(item.getRedirection());
    }

    private boolean isHostValid(HostListItem item) {
        return RegexUtils.isValidWildcardHostname(item.getHost());
    }
}
