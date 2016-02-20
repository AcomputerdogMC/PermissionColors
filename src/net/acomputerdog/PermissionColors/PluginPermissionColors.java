package net.acomputerdog.PermissionColors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PluginPermissionColors extends JavaPlugin implements Listener {
    private Map<Player, String> playerColors;

    //don't reset in onEnable or onDisable
    private boolean reloading = false;

    @Override
    public void onEnable(){
        playerColors = new HashMap<>();
        getServer().getOnlinePlayers().forEach(this::changeColors);
        if (!reloading) {
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    @Override
    public void onDisable() {
        playerColors = null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        changeColors(p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogout(PlayerQuitEvent e) {
        //make sure we don't keep reference to a player who already left
        playerColors.remove(e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("permissioncolors.command.reload")) {
                sender.sendMessage(ChatColor.YELLOW + "Reloading, please wait...");
                getLogger().info("Reloading...");
                reloading = true;
                onDisable();
                onEnable();
                reloading = false;
                getLogger().info("Done.");
                sender.sendMessage(ChatColor.YELLOW + "Done.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid usage, use /permissioncolors <function>.  Current supported functions: reload");
        }
        return true;
    }

    private String getColorForPlayer(Player player) {
        String colorStr = playerColors.get(player);
        if (colorStr == null) {
            ChatColor color = null;
            ChatColor format = null;
            if (player.hasPermission("permissioncolors.color.black")) {
                color = ChatColor.BLACK;
            } else if (player.hasPermission("permissioncolors.color.dark_blue")) {
                color = ChatColor.DARK_BLUE;
            } else if (player.hasPermission("permissioncolors.color.dark_green")) {
                color = ChatColor.DARK_GREEN;
            } else if (player.hasPermission("permissioncolors.color.dark_aqua")) {
                color = ChatColor.DARK_AQUA;
            } else if (player.hasPermission("permissioncolors.color.dark_red")) {
                color = ChatColor.DARK_RED;
            } else if (player.hasPermission("permissioncolors.color.dark_purple")) {
                color = ChatColor.DARK_PURPLE;
            } else if (player.hasPermission("permissioncolors.color.gold")) {
                color = ChatColor.GOLD;
            } else if (player.hasPermission("permissioncolors.color.grey")) {
                color = ChatColor.GRAY;
            } else if (player.hasPermission("permissioncolors.color.dark_grey")) {
                color = ChatColor.DARK_GRAY;
            } else if (player.hasPermission("permissioncolors.color.blue")) {
                color = ChatColor.BLUE;
            } else if (player.hasPermission("permissioncolors.color.green")) {
                color = ChatColor.GREEN;
            } else if (player.hasPermission("permissioncolors.color.aqua")) {
                color = ChatColor.AQUA;
            } else if (player.hasPermission("permissioncolors.color.red")) {
                color = ChatColor.RED;
            } else if (player.hasPermission("permissioncolors.color.light_purple")) {
                color = ChatColor.LIGHT_PURPLE;
            } else if (player.hasPermission("permissioncolors.color.yellow")) {
                color = ChatColor.YELLOW;
            }

            if (player.hasPermission("permissioncolors.format.scrambled")) {
                format = ChatColor.MAGIC;
            } else if (player.hasPermission("permissioncolors.format.bold")) {
                format = ChatColor.BOLD;
            } else if (player.hasPermission("permissioncolors.format.strikethrough")) {
                format = ChatColor.STRIKETHROUGH;
            } else if (player.hasPermission("permissioncolors.format.underline")) {
                format = ChatColor.UNDERLINE;
            } else if (player.hasPermission("permissioncolors.format.italic")) {
                format = ChatColor.ITALIC;
            }

            colorStr =  (color == null ? "" : color.toString()) + (format == null ? "" : format.toString());
            playerColors.put(player, colorStr);
        }
        return colorStr;
    }

    private void changeColors(Player player) {
        String oldName = player.getDisplayName();
        String color = getColorForPlayer(player);
        String newName = color + oldName + ChatColor.WHITE.toString() + ChatColor.RESET.toString();
        player.setDisplayName(newName);
        player.setPlayerListName(newName);
        /*
        if (newName.length() <= 16) {
            player.setPlayerListName(newName);
        }
        */
        player.setCustomNameVisible(true);
        player.setCustomName(newName);
    }
}
