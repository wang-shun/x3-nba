package com.ftkj.x3.client.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luch
 */
@Component
public class ClientConsole {
    @Autowired
    private ClientPropConsole prop;
    @Autowired
    private ClientPlayerConsole player;

    public void init() {
        prop.init();
        player.init();
    }
    public void afterReloadNbaPlayer(){
        player.afterReloadNbaPlayer();
    }
    public ClientPropConsole getProp() {
        return prop;
    }

    public ClientPlayerConsole getPlayer() {
        return player;
    }
}
