package com.kerrishaus.keepinventory;

import org.apache.commons.lang.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class KeepInventory extends JavaPlugin implements Listener
{
    public List<UUID> keepInventoryUsers = new ArrayList<>();

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
                this.keepInventoryUsers.add(UUID.fromString(user));
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
        this.saveConfig();

        this.getLogger().info("KeepInventory disabled.");
    }

    public void toggleUserKeepInventory(final UUID player)
    {
        if (keepInventoryUsers.contains(player))
            keepInventoryUsers.remove(player);
        else
            keepInventoryUsers.add(player);

        this.saveKeepInventoryList();
    }

    public void saveKeepInventoryList()
    {
        for (UUID id : this.keepInventoryUsers)
            this.getConfig().set(id.toString(), true);

        saveConfig();
    }

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event)
    {
        if (keepInventoryUsers.contains(event.getEntity().getUniqueId()))
        {
            event.setKeepInventory(true);
            event.getDrops().clear();

            this.getLogger().fine("Kept " + event.getEntity().getName() + "'s inventory.");
        }
    }
}
