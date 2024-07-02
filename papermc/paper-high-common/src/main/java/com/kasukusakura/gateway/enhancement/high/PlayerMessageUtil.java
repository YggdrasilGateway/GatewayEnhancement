package com.kasukusakura.gateway.enhancement.high;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class PlayerMessageUtil {
    public static boolean canSendTo(Player playerMe, UUID messageSender) {
        if (playerMe == null || messageSender == null) return false;
        if (playerMe.getUniqueId().equals(messageSender)) return true;

        var sender = Bukkit.getPlayer(messageSender);
        if (sender == null) return false;


        var senderFrom = PropertyUtil.getValue(sender.getPlayerProfile(), "gatewaySource");
        var meFrom = PropertyUtil.getValue(playerMe.getPlayerProfile(), "gatewaySource");

        if (senderFrom == null || meFrom == null) return false;

        return Objects.equals(senderFrom.getValue(), meFrom.getValue());
    }
}
