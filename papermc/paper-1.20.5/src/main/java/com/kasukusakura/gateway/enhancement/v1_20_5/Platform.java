package com.kasukusakura.gateway.enhancement.v1_20_5;

import com.kasukusakura.gateway.enhancement.GatewayEnhancement;
import com.kasukusakura.gateway.enhancement.UnsafeUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Platform implements Listener {
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, GatewayEnhancement.INSTANCE);
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) throws Throwable {
        injectHook((CraftPlayer) event.getPlayer());
    }

    private static void injectHook(CraftPlayer player) {
        var oldConnection = player.getHandle().connection;
        var newConnection = player.getHandle().connection = HookingConnection.hooking(player.getHandle().connection);

        var nettyConnection = player.getHandle().connection.connection;
        UnsafeUtils.scanningReplace(nettyConnection, oldConnection, newConnection);
    }

}
