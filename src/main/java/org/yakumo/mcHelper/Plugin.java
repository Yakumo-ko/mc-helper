package org.yakumo.mcHelper;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import org.yakumo.mcHelper.command.Echo;
import org.yakumo.mcHelper.command.McCompositeCommand;
import org.yakumo.mcHelper.config.Config;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    public Config config;

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("org.yakumo.mcHelper.plugin", "0.1")
                .name("mcHelper")
                .author("Yakumo_ko")
                .build());
    }

    @Override
    public void onEnable() {

        getLogger().info("Plugin mcHelper loaded!");

        String path = INSTANCE.getConfigFolder().getPath() + "/config.json";
        config = new Config(path, INSTANCE);

        CommandManager.INSTANCE.registerCommand(Echo.INSTANCE, false);
        CommandManager.INSTANCE.registerCommand(McCompositeCommand.INSTANCE, false);
    }
}
