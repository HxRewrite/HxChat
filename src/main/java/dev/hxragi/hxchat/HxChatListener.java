package dev.hxragi.hxchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class HxChatListener implements Listener {

    private final HxChat plugin;
    private final MiniMessage miniMessage;

    public HxChatListener(HxChat plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        var originalMessage = event.message();
        var config = plugin.getChatConfig();

        var plainText = PlainTextComponentSerializer.plainText().serialize(originalMessage);
        boolean isGlobal = plainText.startsWith("!");

        var finalMessageContent = isGlobal
                ? Component.text(plainText.substring(1).trim())
                : originalMessage;

        if (isGlobal && plainText.length() == 1) {
            event.setCancelled(true);
            return;
        }

        event.message(finalMessageContent);

        boolean nobodyHeard = false;

        if (!isGlobal) {
            var world = player.getWorld();
            var senderLoc = player.getLocation();
            double radiusSquared = Math.pow(config.localRadius(), 2);

            event.viewers().retainAll(
                    event.viewers().stream()
                            .filter(audience -> {
                                if (audience instanceof Player receiver) {
                                    if (!receiver.getWorld().equals(world)) return false;
                                    return receiver.getLocation().distanceSquared(senderLoc) <= radiusSquared;
                                }
                                return audience == org.bukkit.Bukkit.getConsoleSender();
                            })
                            .toList()
            );

            long playerCount = event.viewers().stream()
                    .filter(a -> a instanceof Player)
                    .count();

            nobodyHeard = (playerCount <= 1) && config.shouldShowNoOneHeard();
        }

        final boolean showNobodyHeard = nobodyHeard;
        final String formatString = isGlobal ? config.globalFormat() : config.localFormat();

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            var formattedMessage = miniMessage.deserialize(formatString,
                    Placeholder.component("player", sourceDisplayName),
                    Placeholder.component("message", message)
            );

            if (showNobodyHeard && viewer == source) {
                return formattedMessage
                        .append(Component.newline())
                        .append(miniMessage.deserialize(config.noOneHeardMessage()));
            }

            return formattedMessage;
        });
    }
}