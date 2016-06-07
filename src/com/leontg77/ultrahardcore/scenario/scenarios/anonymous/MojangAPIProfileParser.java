package com.leontg77.ultrahardcore.scenario.scenarios.anonymous;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

/**
 * Fetches profiles from the Mojang API. This API is rate limited so care should be taken when using this.
 * 
 * @author ghowden, converted to from kotlin to java.
 * @see https://github.com/Eluinhost/anonymous
 */
public class MojangAPIProfileParser implements ProfileParser {
    private static final String URL_FORMAT = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    protected Gson parser = new Gson();

    protected String getUrlForUuid(UUID uuid) {
        return String.format(URL_FORMAT, uuid.toString().replace("-", ""));
    }

    @Override
    public ParsedProfile getForUuid(UUID uuid) throws IOException {
        return fromJson(Resources.toString(new URL(getUrlForUuid(uuid)), Charsets.UTF_8));
    }

    protected ParsedProfile fromJson(String json) {
        ParsedProfile profile = parser.fromJson(json, ParsedProfile.class);

        if (!profile.isValid()) {
            throw new IllegalArgumentException("Invalid response from Mojang server");
        }

        return profile;
    }
}