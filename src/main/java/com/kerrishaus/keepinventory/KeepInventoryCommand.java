package com.kerrishaus.keepinventory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KeepInventoryCommand implements CommandExecutor
{
    private final KeepInventory plugin;

    KeepInventoryCommand(KeepInventory plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        // TODO: allow using command from console if a player is specified

        if (!(sender instanceof Player))
        {
            plugin.getLogger().warning("Only Players can use this command.");
            return true;
        }

        if (args.length == 0)
        {
            sender.sendMessage("Keep inventory is turned " + (plugin.keepInventoryUsers.contains(((Player) sender).getUniqueId()) ? "on" : "off") + ".");
            return true;
        }

        UUID playerId = ((Player) sender).getUniqueId();

        plugin.toggleUserKeepInventory(playerId);

        if (plugin.keepInventoryUsers.contains(playerId))
            sender.sendMessage("KeepInventory is now on.");
        else
            sender.sendMessage("KeepInventory is now off.");

        return true;
    }
}