package org.yakumo.mcHelper.config;

import org.json.JSONObject;
import org.yakumo.mcHelper.Plugin;
import java.io.*;

/**
 * 用来加载和读取配置文件的
 */
public class Config {

    // 服务器类型
    public final static  String JAVA_TYPE = "java";
    public final static String BDS_TYPE = "bds";

    private String path;
    private JSONObject json;

    public Config(String path, Plugin INSTANCE){
        this.path = path;
        if(!ispath())
            INSTANCE.getLogger().info("无法创建config.json文件, 请手动创建并重启该插件");
        else
            readConfig();
    }

    // config文件初始化, 即第一次加载时生成默认内容
    private void configInit(){
        JSONObject info = new JSONObject(); //单个服务器信息
        info.put("host", "");
        info.put("port", "");
        info.put("type", "");
        info.put("password", "");

        JSONObject server = new JSONObject(); // 服务器名称
        server.put("name", info);

        json = new JSONObject(); // 服务器列表
        json.put("servers", server);


    }

    // 写入配置文件夹
    private void writeConfig(){
        try {
            FileWriter out = new FileWriter(path);
            out.write(json.toString());
            out.close();
        }catch (IOException e){

        }
    }

    // 读入配置文件夹
    private void readConfig(){
        try{
            FileReader in = new FileReader(path);
            String text = "";
            char[] buf = new char[1024];
            int len;
            while((len=in.read(buf))!=-1) text += new String(buf);
            json = new JSONObject(text);
        }catch (IOException e){

        }
    }

    // 查看文件是否存在
    private boolean ispath(){
        File file = new File(path);
        if(file.isFile()) return true;
        try{
            file.createNewFile();
            configInit();
            writeConfig();
        }catch (IOException e){
            return  false;
        }
        return true;
    }

    /**
     *  获取一个包含服务器ip,port等之类的服务器信息
     * @param key 配置文件中的对于服务器信息的key值
     * @return 若存在返回json对象,若不存在返回null
     */
    public JSONObject getServerInfo(String key){
        JSONObject servers = json.getJSONObject("servers");
        if(key=="servers") return servers;
        return servers.has(key)?servers.getJSONObject(key):null;
    }


}
