package com.kasukusakura.gateway.enhancement.high;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

public class PropertyUtil {
    public static ProfileProperty getValue(PlayerProfile profile, String key) {
        var properties = profile.getProperties();
        return properties.stream()
                .filter(it -> key.equals(it.getName()))
                .findFirst().orElse(null);
    }
}
