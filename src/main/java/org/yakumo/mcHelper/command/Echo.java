package org.yakumo.mcHelper.command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.yakumo.mcHelper.Plugin;


public class Echo extends JSimpleCommand {

    public static final Echo INSTANCE = new Echo();

    private Echo(){
        super(Plugin.INSTANCE, "echo");
        this.setDescription("复读消息");
    }

    /**
     *  发送mc command用的
     *  对于用对于mc command而言， 需要用“-“代替” “
     * @param sender
     * @param age
     */
    @Handler
    public void handle(CommandSender sender, String age){
        sender.sendMessage(age);


    }
}
