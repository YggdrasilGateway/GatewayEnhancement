package com.kasukusakura.yggdrasil.gateway.enhancement.velocity.injection;

import com.velocitypowered.api.proxy.Player;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerOutgoingInjector {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerOutgoingInjector.class);

    public static void inject(Player player) {
        try {
            var connection = player.getClass().getMethod("getConnection").invoke(player);
            var channel = (Channel) connection.getClass().getMethod("getChannel").invoke(connection);
//            System.out.println(channel.pipeline().names());

            channel.pipeline().addAfter(
                    "minecraft-encoder",
                    "yggdrasil-enhancement-outgoing-hook",
                    new OutgoingHookAdapter(player)
            );
        } catch (Throwable throwable) {
            LOG.warn("Exception when injecting player", throwable);
        }
    }
}
