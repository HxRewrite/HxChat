package dev.hxragi.hxchat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public record HxChatCommand(HxChat plugin) {

    public void register(Commands commands) {
        commands.register(
                Commands.literal("hxchat")
                        .requires(src -> src.getSender().hasPermission("hxchat.admin"))
                        .then(Commands.literal("reload")
                                .executes(this::reload))
                        .build(),
                "Управление чатом",
                List.of("hchat")
        );
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        plugin.reloadPluginConfig();
        var msg = MiniMessage.miniMessage().deserialize(plugin.getChatConfig().reloadMessage());
        ctx.getSource().getSender().sendMessage(msg);
        return Command.SINGLE_SUCCESS;
    }
}