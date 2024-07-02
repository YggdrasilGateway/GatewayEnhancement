package com.kasukusakura.yggdrasil.gateway.enhancement.velocity;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;

import java.util.List;

@Plugin(id = "gateway-enhancement")
public class GatewayVelocity {
    @Inject
    private ProxyServer proxy;

    public static ProxyServer PROXY;

    public static String getPlayerDeclaredTree(Player playerMe) {
        var propertyValue = getPropertyValue(playerMe.getGameProfileProperties(), "yggdrasil.gateway.source");
        if (propertyValue != null) return propertyValue.getValue();
        return null;
    }

    @Subscribe
    private void onProxyInitialize(ProxyInitializeEvent event) throws Throwable {
        PROXY = proxy;
    }

    @Subscribe
    private void onPlayerLogin(LoginEvent event) {
        if (!event.getResult().isAllowed()) return;
        var properties = event.getPlayer().getGameProfile().getProperties();
        properties.stream().filter(it -> "rejection.reason".equals(it.getName())).findFirst().ifPresent(rejection -> {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(rejection.getValue())));
        });
    }

    @Subscribe
    private void onPlayerChat(PlayerChatEvent event) {
//        ChatSession
    }


    public static GameProfile.Property getPropertyValue(List<GameProfile.Property> properties, String key) {
        if (properties == null) return null;
        return properties.stream()
                .filter(it -> key.equals(it.getName()))
                .findFirst()
                .orElse(null);
    }
}
