package org.yakumo.mcHelper.command;



import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.YakumoKo.libs.mc.Rcon.RconSeesion;
import org.YakumoKo.libs.mc.serverListPing.baseDataStruct.Players;
import org.YakumoKo.libs.mc.serverListPing.packet.TcpPacket;
import org.YakumoKo.libs.mc.serverListPing.packet.UdpPacket;
import org.YakumoKo.libs.mc.serverListPing.session.TcpSession;
import org.YakumoKo.libs.mc.serverListPing.session.UpdSession;
import org.json.JSONObject;
import org.yakumo.mcHelper.Plugin;
import org.yakumo.mcHelper.config.Config;

import java.io.IOException;

public final class McCompositeCommand extends JCompositeCommand {

    public static final McCompositeCommand INSTANCE = new McCompositeCommand();

    private JSONObject json;

    private  McCompositeCommand(){
        super(Plugin.INSTANCE, "mc2");
        json = Plugin.INSTANCE.config.getServerInfo("servers");
    }

    // 子指令, 发送MineCraft指令
    // mc send serverName mineCraftCommand
    @SubCommand
    public void send(CommandSender sender, String server_name, String command){
        if(!json.has(server_name)) {
            sender.sendMessage("该服务器不存在, 请使用mc list查看现有的服务器~");
            return;
        }

        JSONObject info = json.getJSONObject(server_name);
        if(!(info.getString("type")).equals(Config.JAVA_TYPE)){
            sender.sendMessage("基岩端不支持rcon协议,Minecraft命令无效QWQ");
            return;
        }

        RconSeesion rconSeesion = new RconSeesion (
                info.getString("host"),
                Integer.parseInt(info.getString("rcon")),
                info.getString("password"));
        String text = rconSeesion.sendCommand(command.replace('-', ' '));
        sender.sendMessage(text);

    }

    // 子指令, 获取一个服务器的信息
    // mc info server;
    @SubCommand
    public void info(CommandSender sender, String server_name){
        if(!json.has(server_name)) {
            sender.sendMessage("该服务器不存在, 请使用mc list查看现有的服务器~");
            return;
        }
        JSONObject info = json.getJSONObject(server_name);
        if((info.getString("type")).equals("java")){
            sender.sendMessage("获取java端服务器信息所需时间稍长...请耐心等待哈=w=");
            TcpPacket packet = new TcpPacket(0, -1,
                    info.getString("host"),
                    Integer.parseInt(info.getString("port")),
                    1);
            TcpSession seesion = new TcpSession(info.getString("host"), Integer.parseInt(info.getString("port")));
            try {
                seesion.run(packet);
            }catch (IOException e){
                sender.sendMessage("error: 无法发送请求或packet包处理错误");
                return;
            }
            Players players = seesion.getPlayers();
            String result = "服务器端版本: " + seesion.getVersion()
                    + "\n介绍: " + seesion.getMotd() +
                    "\n在线人数: " + players.getOnline()+"/"+players.getMAX();
            sender.sendMessage(result);
        }else{
            UpdSession seesion = new UpdSession(info.getString("host"), Integer.parseInt(info.getString("port")));
            try{
                seesion.getServerInfo(new UdpPacket());
            }catch (IOException e){
                sender.sendMessage("error: 无法发送请求...疑似服务器离线");
                return;
            }
            String result = "服务器端版本: 基岩端-" +seesion.getVersion() +
                    "\n介绍: " + seesion.getMotd() + "\n在线人数: "  + seesion.getPlayerNum() +
                    "\n 游戏模式: " + seesion.getGamemode();
            sender.sendMessage(result);

        }
    }

    // 子指令, 获取服务器列表
    // mc list
    @SubCommand
    public void list(CommandSender sender){
        String text = "服务器名: 类型\n -------------\n";
        for(String i: json.keySet()){
            String tmp = i + ": " + json.getJSONObject(i).getString("type") + "\n";
            text += tmp;
        }
        sender.sendMessage(text);
    }

    // 子指令, 帮助
    // mc help
    @SubCommand
    public void help(CommandSender sender){
        String text = "mc2 send serverName <mcCommand>\n" +
                "注: 向指定服务器发送mc指令\nmc指令中的空格需要用-代替 例如time-set-day\n" +
                "mc2 info servername\n" +
                "注: 获取指定服务器信息\n" +
                "mc2 list\n" +
                "注: 获取服务器列表\n" +
                "mc2 help: 帮助";
        sender.sendMessage(text);
    }
}
