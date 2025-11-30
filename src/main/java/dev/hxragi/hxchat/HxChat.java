package dev.hxragi.hxchat;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class HxChat extends JavaPlugin {

    private HxChatConfig chatConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> new HxChatCommand(this).register(event.registrar()));

        getServer().getPluginManager().registerEvents(new HxChatListener(this), this);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.chatConfig = HxChatConfig.from(getConfig());
    }

    public HxChatConfig getChatConfig() {
        return chatConfig;
    }
}