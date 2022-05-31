package com.kerrishaus.keepinventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class KeepInventory extends JavaPlugin implements Listener
{
    public HashMap<UUID, Boolean> dontKeepInventoryUsers = new HashMap<>();

    @Override
    public void onEnable()
    {
        this.getCommand("keepinventory").setExecutor(new KeepInventoryCommand(this));
        this.getCommand("keepinventory").setTabCompleter(new KeepInventoryTabCompleter());
        this.getCommand("keepinventory").setPermission("keepinventory");
        this.getCommand("keepinventory").setPermissionMessage("You cannot change your keep inventory setting.");

        this.getServer().getPluginManager().registerEvents(this, this);

        this.saveDefaultConfig();

        try
        {
            Set<String> users = this.getConfig().getConfigurationSection("").getKeys(false);

            for (String user : users)
                this.dontKeepInventoryUsers.put(UUID.fromString(user), this.getConfig().getBoolean(user));
        }
        catch (NullPointerException exception)
        {
            this.getLogger().severe("Failed to load users with KeepInventory enabled.");
        }

        this.getLogger().info("KeepInventory enabled.");
    }

    @Override
    public void onDisable()
    {
        this.saveKeepInventoryList();

        this.getLogger().info("KeepInventory disabled.");
    }

    public void toggleUserKeepInventory(final UUID player)
    {
        if (!dontKeepInventoryUsers.containsKey(player))
            keepInventoryOff(player);
        else
            keepInventoryOn(player);

        this.saveKeepInventoryList();
    }

    public void keepInventoryOff(final UUID player)
    {
        this.dontKeepInventoryUsers.put(player, true);

        this.saveKeepInventoryList();
    }

    public void keepInventoryOn(final UUID player)
    {
        this.dontKeepInventoryUsers.put(player, false);

        this.saveKeepInventoryList();
    }

    public void saveKeepInventoryList()
    {
        for(Map.Entry<UUID, Boolean> entry : dontKeepInventoryUsers.entrySet())
        {
            this.getConfig().set(entry.getKey().toString(), entry.getValue());
            this.getLogger().info("Saved a user.");
        }

        this.saveConfig();
    }

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event)
    {
        // if the list contains the user at all
        if (this.dontKeepInventoryUsers.containsKey(event.getEntity().getUniqueId()))
        {
            // if the user is set to false
            if (!this.dontKeepInventoryUsers.get(event.getEntity().getUniqueId()))
            {
                event.setKeepInventory(true);
                event.getDrops().clear();

                this.getLogger().fine("Kept " + event.getEntity().getName() + "'s inventory.");
            }
        }
        else // if the user is not in the list
        {
            // let them keep their inventory
            // and then send them a message and let them know how to turn it off
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event)
    {
        if (!this.dontKeepInventoryUsers.containsKey(event.getPlayer().getUniqueId()))
        {
            event.getPlayer().sendMessage("Don't want to keep your inventory when you die?");
            event.getPlayer().sendMessage("Use /keepinventory off to disable keep inventory.");

            this.keepInventoryOn(event.getPlayer().getUniqueId());
        }
    }
}
