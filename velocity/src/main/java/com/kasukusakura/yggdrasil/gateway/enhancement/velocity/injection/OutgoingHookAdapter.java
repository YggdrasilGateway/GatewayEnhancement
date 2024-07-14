package com.kasukusakura.yggdrasil.gateway.enhancement.velocity.injection;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

import static com.kasukusakura.yggdrasil.gateway.enhancement.velocity.GatewayVelocity.getPlayerDeclaredTree;

public class OutgoingHookAdapter extends ChannelOutboundHandlerAdapter {

    private static final Class<?> UPSERT_PLAYER_INFO_CLASS;
    private static final Class<?> UPSERT_PLAYER_INFO_ENTRY_CLASS;
    private static final MethodHandle MH_PLAYERINFO$GET_ENTRIES;
    private static final MethodHandle MH_ENTRY$GET_PROFILE;
    private static final MethodHandle MH_ENTRY$SET_PROFILE;

    static {
        try {
            UPSERT_PLAYER_INFO_CLASS = Class.forName("com.velocitypowered.proxy.protocol.packet.UpsertPlayerInfoPacket");
            UPSERT_PLAYER_INFO_ENTRY_CLASS = Class.forName("com.velocitypowered.proxy.protocol.packet.UpsertPlayerInfoPacket$Entry");

            var lookup = MethodHandles.lookup();

            MH_PLAYERINFO$GET_ENTRIES = lookup.findVirtual(UPSERT_PLAYER_INFO_CLASS, "getEntries", MethodType.methodType(List.class));
            MH_ENTRY$GET_PROFILE = lookup.findVirtual(UPSERT_PLAYER_INFO_ENTRY_CLASS, "getProfile", MethodType.methodType(GameProfile.class));
            MH_ENTRY$SET_PROFILE = lookup.findVirtual(UPSERT_PLAYER_INFO_ENTRY_CLASS, "setProfile", MethodType.methodType(void.class, GameProfile.class));
        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }


    private final Player player;
    private final String playerOrigin;

    public OutgoingHookAdapter(Player player) {
        this.player = player;
        playerOrigin = getPlayerDeclaredTree(player);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (UPSERT_PLAYER_INFO_CLASS.isInstance(msg)) {
            try {
                var entries = (List<?>) MH_PLAYERINFO$GET_ENTRIES.invoke(msg);
                if (entries != null) {
                    for (var entry : entries) {
                        var profile = (GameProfile) MH_ENTRY$GET_PROFILE.invoke(entry);
                        if (profile == null) continue;


                        MH_ENTRY$SET_PROFILE.invoke(entry, profile.withProperties(
                                profile.getProperties().stream()
                                        .filter(it -> !it.getName().startsWith("gateway"))
                                        .toList()
                        ));
                    }
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
        super.write(ctx, msg, promise);
    }
}
