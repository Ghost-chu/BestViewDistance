package me.lxct.bestviewdistance.functions;

import me.lxct.bestviewdistance.functions.data.Variable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.lxct.bestviewdistance.functions.Get.getViewDistance;
import static me.lxct.bestviewdistance.functions.data.Variable.*;

public class Set {

    // MAKE SURE CALCULATED VIEW DISTANCE ISN'T OVER LIMITS
    private static int setViewDistanceLimit(int viewDistance) {
        if (viewDistance > Variable.max) {
            viewDistance = Variable.max;
        } else if (viewDistance < Variable.min) {
            viewDistance = Variable.min;
        }
        return viewDistance;
    }

    // A FUNCTION FOR CLIENT SIDE SETTING. DON'T GIVE MORE VIEW DISTANCE THAN REQUIRED.
    static int setClientSettingLimit(Player player, int viewDistance) {
        int clientSideViewDistance = getViewDistance(player); // Get Client Side View Distance
        if (viewDistance > clientSideViewDistance) { // If given view distance is more than client side view distance
            viewDistance = clientSideViewDistance;
        }
        return viewDistance;
    }

    // MAKE SURE SUPPORTED VIEW DISTANCE ISN'T OVER LIMITS
    private static void setSupportedViewDistanceLimit(String player) {
        int supportedViewDistance = playerViewDistance.get(player); // The ping supported view distance
        if (supportedViewDistance > Variable.max) { // If playerViewDistance is over maximum view distance
            playerViewDistance.put(player, Variable.max); // Set it to max
        } else if (supportedViewDistance < Variable.min) { // Same with min
            playerViewDistance.put(player, Variable.min);
        }
    }

    // CHECK AND USE PERMISSIONS
    public static int setPlayerPermissions(Player player, int viewDistance) {
        for (int i = 32; i >= 3; i--) { // Start at 32, to 3
            // 3 4 5 6 7 8 9 10 ... 30 31 32
            if (player.hasPermission("view.set." + i) && !player.hasPermission("*") && !player.hasPermission("*.*")) { // view.set.i is set
                return i; // If he has permission, then return the number "after" the permission.
            }
        }
        return viewDistance; // If he doesn't have permissions, then return viewDistance.
    }

    // MAKE SURE REDUCTION INDICE ISN'T OVER LIMITS
    public static void setServerLimits() {
        if (Variable.reductionIndice > Variable.maxindice) { // Make sure the reduction indice don't escape limits
            Variable.reductionIndice = Variable.maxindice;
        } else if (Variable.reductionIndice < 0) {
            Variable.reductionIndice = 0.0;
        }
    }

    // THE MAIN FUNCTION ! CALCULATE BEST PLAYER VIEW DISTANCE WITH REDUCTION INDICE
    public static void calculatePlayersBestViewDistance(double ReductionIndice) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int supportedViewDistance;
            int sendVD;
            if (playerViewDistance.containsKey(player.getName())) {
                supportedViewDistance = playerViewDistance.get(player.getName()); // View distance supported by player
            } else {
                supportedViewDistance = onloginview;
            }
            int ping = player.spigot().getPing(); // Ping of player
            if (ping < Variable.aping && ping >= safePing) {
                ++supportedViewDistance; // increase
            } // Low ping = More View Distance
            else if (ping >= Variable.rping) {
                --supportedViewDistance; // Decrease
            } // Big ping = Less View Distance
            playerViewDistance.put(player.getName(), supportedViewDistance); // Store in var
            setSupportedViewDistanceLimit(player.getName()); // Make sure supported view distance doesn't get over limits

            if (playerLiveViewDistance.containsKey(player.getName())) {
                if (getViewDistance(player) < playerViewDistance.get(player.getName())) { // ADDED ON 8.5
                    sendVD = getViewDistance(player) + moreThanSettings;                // MAKE SURE THAT THE REDUCTION INDICE DECREASE THE TRUE VIEW DISTANCE
                } else {
                    sendVD = playerViewDistance.get(player.getName());
                }
            } else {
                if (getViewDistance(player) < supportedViewDistance) { // ADDED ON 9.1
                    sendVD = getViewDistance(player) + moreThanSettings;                // MAKE SURE THAT THE REDUCTION INDICE DECREASE THE TRUE VIEW DISTANCE
                } else {
                    sendVD = supportedViewDistance;
                }
            }

            if(sendVD > getViewDistance(player)) {
                sendVD = getViewDistance(player);
            }

            int viewDistance = Math.round((int) (sendVD * (1 - ReductionIndice))); // Apply percentage
            // About the line under this comment. We set player view distance only if view distance doesn't get over limits
            // And respect player settings
            playerLiveViewDistance.put(player.getName(), setViewDistanceLimit(viewDistance)); // Store result of calculations
        }
    }

    //
    //
    //
    //
    //


}