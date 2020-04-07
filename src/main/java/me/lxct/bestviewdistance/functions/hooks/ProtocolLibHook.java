package me.lxct.bestviewdistance.functions.hooks;

import com.comphenix.packetwrapper.WrapperPlayClientSettings;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.lxct.bestviewdistance.functions.BVDPlayer;
import org.bukkit.plugin.Plugin;

import static me.lxct.bestviewdistance.functions.data.Variable.onlinePlayers;

class ProtocolLibHook {
    static void protocolLibHook(Plugin plugin) {
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.SETTINGS) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                if (e.getPacketType() == PacketType.Play.Client.SETTINGS) {
                    PacketContainer packet = e.getPacket();
                    BVDPlayer player = onlinePlayers.get(e.getPlayer());
                    if(player == null){
                        player = new BVDPlayer(e.getPlayer());
                        onlinePlayers.put(e.getPlayer(), player);
                    }
                    WrapperPlayClientSettings settings = new WrapperPlayClientSettings(packet);
                    player.saveSettingsViewDistance(settings.getViewDistance());
                }
            }
        });
    }
}
