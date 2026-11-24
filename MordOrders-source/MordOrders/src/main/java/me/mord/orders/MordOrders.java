package me.mord.orders;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MordOrders extends JavaPlugin {
    private Economy economy;
    private ShardsManager shards;

    @Override public void onEnable() {
        saveDefaultConfig();
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) economy = provider.getProvider();
        }
        shards = new ShardsManager(this);
        getCommand("orders").setExecutor(new OrdersGUI(this));
        getCommand("shards").setExecutor(new ShardsCommand(shards));
        getLogger().info("MORD Orders enabled. Economy available: " + (economy != null));
    }
    public Economy economy() { return economy; }
    public ShardsManager shards() { return shards; }
}
