package com.kasukusakura.gateway.enhancement.high;

import com.kasukusakura.gateway.enhancement.GatewayEnhancement;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class HighCommonPoint implements Listener {
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, GatewayEnhancement.INSTANCE);
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        var playerProfile = event.getPlayer().getPlayerProfile();
        var rejection = PropertyUtil.getValue(playerProfile, "rejection.reason");
        if (rejection != null) {
            event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    Component.text(rejection.getValue())
            );
        }
    }
}
