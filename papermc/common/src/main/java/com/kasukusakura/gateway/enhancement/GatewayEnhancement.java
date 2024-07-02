package com.kasukusakura.gateway.enhancement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GatewayEnhancement extends JavaPlugin {
    public static GatewayEnhancement INSTANCE;

    public GatewayEnhancement() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        String bkVersion = Bukkit.getBukkitVersion();

        String subpoint = null;

        if (bkVersion.startsWith("1.20.5") || bkVersion.startsWith("1.20.6")) {
            subpoint = "v1_20_5";
        } else if (bkVersion.startsWith("1.20.") || bkVersion.startsWith("1.20-")) {
            subpoint = "v1_20";
        } else if (bkVersion.startsWith("1.19.") || bkVersion.startsWith("1.19-")) {
            subpoint = "v1_19";
        } else {
            getLogger().warning("Version " + bkVersion + " is not supported for advanced features...");
        }

        if (subpoint != null) {
            try {
                Class<?> forred = Class.forName("com.kasukusakura.gateway.enhancement." + subpoint + ".Platform");
                forred.getMethod("onEnable").invoke(forred.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        {
            Class<?> klassHighPoint = null;
            try {
                klassHighPoint = Class.forName("com.kasukusakura.gateway.enhancement.high.HighCommonPoint");
            } catch (Throwable ignored) {
            }
            if (klassHighPoint != null) {
                try {
                    klassHighPoint.getMethod("onEnable").invoke(klassHighPoint.newInstance());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        }
    }
}
