package dev.hxragi.hxchat;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public record HxChatConfig(
        int localRadius,
        boolean shouldShowNoOneHeard,
        String globalFormat,
        String localFormat,
        String reloadMessage,
        String noOneHeardMessage
) {
    public static HxChatConfig from(@NotNull FileConfiguration config) {
        return new HxChatConfig(
                config.getInt("chat.radius", 75),
                config.getBoolean("chat.show-no-one-heard", true),
                config.getString("chat.global-format", "<yellow>G <player>: <message>"),
                config.getString("chat.local-format", "<blue>L <player>: <message>"),
                config.getString("messages.reload", "<green>Reloaded!"),
                config.getString("messages.no-one-heard", "<gray>No one heard you...")
        );
    }
}
