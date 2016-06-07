package com.leontg77.ultrahardcore.scenario.scenarios.anonymous;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Maps;

/**
 * Caches for the given [ProfileParser] to the given [File].
 *
 * @param parser when cache doesn't have an entry or has timed out, delegates to this parser
 * @param cacheFile the file to write the profiles to as a backup
 * @param timeoutMins how long to wait before attempting to fetch a skin again
 * 
 * @author ghowden, converted to from kotlin to java.
 * @see https://github.com/Eluinhost/anonymous
 */
public class CachedProfileParser implements ProfileParser {
    private static final DateFormat CACHE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    protected final ProfileParser parser;
    protected final File cacheFile;
    protected final int timeoutMins;
    
    protected Map<UUID, Pair<Calendar, ParsedProfile>> cache = Maps.newHashMap();
    protected YamlConfiguration cacheConfig = new YamlConfiguration();
    
    public CachedProfileParser(ProfileParser parser, File cacheFile, int timeoutMins) throws IOException {
        this.parser = parser;
        this.cacheFile = cacheFile;
        this.timeoutMins = timeoutMins;
        
        if (!cacheFile.exists()) {
            cacheFile.createNewFile();
        }
        
        try {
            cacheConfig.load(cacheFile);
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().warning("Cannot load cache file for annonymous.");
        }

        // restore all from the config file into the live cache map
        for (String key : cacheConfig.getKeys(false)) {
            ConfigurationSection section = cacheConfig.getConfigurationSection(key);
            Calendar date = Calendar.getInstance();
            
            cache.put(
                    UUID.fromString(key), 
                    Pair.of(date, ParsedProfile.fromYaml(section.getConfigurationSection("data")))
            );
        }
    }

    /**
     * Fetch for the given UUID, any returned profile should have [ParsedProfile.isValid] true.
     *
     * If a [ParsedProfile] for [uuid] is still within [timeoutMins] it will be returned immediately, otherwise it is
     * fetched and cached before returning. If the [ProfileParser] fails to fetch a valid [ParsedProfile] then the last
     * cached version is used instead (if none exists the exception will be re-thrown)
     *
     * @param uuid account uuid to fetch profile for
     * @throws java.io.IOException on potential IO problems with no fallback cache
     */
    @Override
    public ParsedProfile getForUuid(UUID uuid) throws IOException {
        Pair<Calendar, ParsedProfile> fromCache = cache.get(uuid);
        
        Calendar timeoutBreakpoint = Calendar.getInstance();
        timeoutBreakpoint.add(Calendar.MINUTE, -timeoutMins);

        if (fromCache != null && fromCache.getLeft().after(timeoutBreakpoint)) {
            return fromCache.getRight();
        }
        
        try {
            ParsedProfile fetched = parser.getForUuid(uuid);

            // store in caches for later
            Calendar now = Calendar.getInstance();
            
            cache.put(
                    uuid, 
                    Pair.of(now, fetched)
            );

            cacheConfig.set(uuid.toString() + ".fetched", CACHE_DATE_FORMAT.format(now.getTime()));
            cacheConfig.set(uuid.toString() + ".data", fetched.toYaml());
            cacheConfig.save(cacheFile);

            return fetched;
        } catch (IOException ex) {
            // Rethrow the exception if we didn't have a cache to fall back to
            throw ex;
        }
    }
}