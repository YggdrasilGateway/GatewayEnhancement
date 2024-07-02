package com.kasukusakura.gateway.enhancement.v1_20_5;

import com.kasukusakura.gateway.enhancement.UnsafeUtils;
import com.kasukusakura.gateway.enhancement.high.PlayerMessageUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class HookingConnection extends ServerGamePacketListenerImpl {
    public HookingConnection(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
        super(server, connection, player, clientData);
    }

    public static HookingConnection hooking(ServerGamePacketListenerImpl original) {
        if (original == null) throw new NullPointerException("original is null");
        if (original instanceof HookingConnection) return (HookingConnection) original;
        try {
            var unsafe = UnsafeUtils.getUnsafe();
            HookingConnection hook = (HookingConnection) unsafe.allocateInstance(HookingConnection.class);

            UnsafeUtils.deepCopying(original, hook, ServerGamePacketListenerImpl.class);

            return hook;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPlayerChatMessage(PlayerChatMessage message, ChatType.Bound params) {
        if (PlayerMessageUtil.canSendTo(getCraftPlayer(), message.link().sender())) {
            super.sendPlayerChatMessage(message, params);
        } else {
            this.sendDisguisedChatMessage(message.decoratedContent(), params);
        }
    }
}
