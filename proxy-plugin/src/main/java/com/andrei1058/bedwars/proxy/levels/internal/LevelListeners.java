package com.andrei1058.bedwars.proxy.levels.internal;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import it.dbruni.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;

public class LevelListeners implements Listener {



    public static LevelListeners instance;

    public LevelListeners() {
        instance = this;
    }

    //create new level data on player join
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        PluginManager pm = Bukkit.getPluginManager();
        if (pm.isPluginEnabled("Lobby")) {
            Lobby.getMinigamesItems(e.getPlayer());
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + "BedwarsProxy" + "]" + ChatColor.RED + " WARN: Dependency Lobby (by dbruni) not found, please consider to put it in the plugin folder.");
        }
        final UUID u = e.getPlayer().getUniqueId();
        // create empty level first
        new PlayerLevel(u);
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            //if (PlayerLevel.getLevelByPlayer(e.getPlayer().getUniqueId()) != null) return;
            Object[] levelData = BedWarsProxy.getRemoteDatabase().getLevelData(u);
            if (levelData.length == 0) return;
            PlayerLevel.getLevelByPlayer(u).lazyLoad((Integer) levelData[0], (Integer) levelData[1], (String) levelData[2], (Integer)levelData[3]);
            //new PlayerLevel(e.getPlayer().getUniqueId(), (Integer)levelData[0], (Integer)levelData[1]);
            //Bukkit.broadcastMessage("LAZY LOAD");
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        final UUID u = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            PlayerLevel pl = PlayerLevel.getLevelByPlayer(u);
            pl.destroy();
        });
    }
}
