package com.leontg77.ultrahardcore.scenario.scenarios.anonymous;

import java.io.IOException;
import java.util.UUID;

/**
 * Used to fetch profiles from a source by their UUID
 * 
 * @author ghowden, converted to from kotlin to java.
 * @see https://github.com/Eluinhost/anonymous
 */
interface ProfileParser {

    /**
     * Fetch for the given UUID, any returned profile should have [ParsedProfile.isValid] true.
     *
     * @param uuid account uuid to fetch profile for
     * @throws java.io.IOException on potential IO problems
     */
    ParsedProfile getForUuid(UUID uuid) throws IOException;
}