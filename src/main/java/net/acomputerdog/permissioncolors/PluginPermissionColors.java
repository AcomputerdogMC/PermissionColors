package net.acomputerdog.permissioncolors;

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

/**
 * Plugin main class
 */
public class PluginPermissionColors extends JavaPlugin implements Listener {
    /**
     * Map of currently logged on players to their color
     */
    private Map<Player, String> playerColors;

    //don't reset in onEnable or onDisable
    private boolean reloading = false;

    @Override
    public void onEnable(){
        playerColors = new HashMap<>();

        // update colors for all online players
        getServer().getOnlinePlayers().forEach(this::changeColors);

        // register events only on first load
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
        // check for cached color string
        String colorStr = playerColors.get(player);

        // if not found, then generate
        if (colorStr == null) {
            ChatColor color = getColorByPermission(player);
            ChatColor format = getFormatByPermission(player);

            colorStr = "";
            if (color != null) {
                colorStr += color.toString();
            }
            if (format != null) {
                colorStr += format.toString();
            }
            playerColors.put(player, colorStr);
        }
        return colorStr;
    }

    private ChatColor getColorByPermission(Player player) {
        if (player.hasPermission("permissioncolors.color.black")) {
            return ChatColor.BLACK;
        } else if (player.hasPermission("permissioncolors.color.dark_blue")) {
            return ChatColor.DARK_BLUE;
        } else if (player.hasPermission("permissioncolors.color.dark_green")) {
            return ChatColor.DARK_GREEN;
        } else if (player.hasPermission("permissioncolors.color.dark_aqua")) {
            return ChatColor.DARK_AQUA;
        } else if (player.hasPermission("permissioncolors.color.dark_red")) {
            return ChatColor.DARK_RED;
        } else if (player.hasPermission("permissioncolors.color.dark_purple")) {
            return ChatColor.DARK_PURPLE;
        } else if (player.hasPermission("permissioncolors.color.gold")) {
            return ChatColor.GOLD;
        } else if (player.hasPermission("permissioncolors.color.grey")) {
            return ChatColor.GRAY;
        } else if (player.hasPermission("permissioncolors.color.dark_grey")) {
            return ChatColor.DARK_GRAY;
        } else if (player.hasPermission("permissioncolors.color.blue")) {
            return ChatColor.BLUE;
        } else if (player.hasPermission("permissioncolors.color.green")) {
            return ChatColor.GREEN;
        } else if (player.hasPermission("permissioncolors.color.aqua")) {
            return ChatColor.AQUA;
        } else if (player.hasPermission("permissioncolors.color.red")) {
            return ChatColor.RED;
        } else if (player.hasPermission("permissioncolors.color.light_purple")) {
            return ChatColor.LIGHT_PURPLE;
        } else if (player.hasPermission("permissioncolors.color.yellow")) {
            return ChatColor.YELLOW;
        } else {
            return null;
        }
    }

    private ChatColor getFormatByPermission(Player player) {
        if (player.hasPermission("permissioncolors.format.scrambled")) {
            return ChatColor.MAGIC;
        } else if (player.hasPermission("permissioncolors.format.bold")) {
            return ChatColor.BOLD;
        } else if (player.hasPermission("permissioncolors.format.strikethrough")) {
            return ChatColor.STRIKETHROUGH;
        } else if (player.hasPermission("permissioncolors.format.underline")) {
            return ChatColor.UNDERLINE;
        } else if (player.hasPermission("permissioncolors.format.italic")) {
            return ChatColor.ITALIC;
        } else {
            return null;
        }
    }

    private void changeColors(Player player) {
        String color = getColorForPlayer(player);
        String listName = color + player.getName();
        String displayName = listName + ChatColor.WHITE.toString() + ChatColor.RESET.toString();

        player.setDisplayName(displayName);

        if (listName.length() <= 16) {
            player.setPlayerListName(listName);
        } else {
            getLogger().warning("Player \"" + player.getName() + "\"'s name is too long to apply their player list color.");
        }
    }
}
