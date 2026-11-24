package me.mord.orders;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public final class OrdersGUI implements CommandExecutor, Listener {
    private final MordOrders plugin;
    private final NamespacedKey orderKey;
    public OrdersGUI(MordOrders plugin) {
        this.plugin = plugin;
        this.orderKey = new NamespacedKey(plugin, "order_id");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        open(player); return true;
    }
    private void open(Player player) {
        Inventory inv = Bukkit.createInventory(new OrderHolder(), 27, ChatColor.DARK_AQUA + "MORD SMP | Orders");
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("orders");
        if (section != null) {
            int slot = 10;
            for (String id : section.getKeys(false)) {
                if (slot > 16) break;
                ConfigurationSection order = section.getConfigurationSection(id);
                if (order == null) continue;
                Material material = Material.matchMaterial(order.getString("material", "STONE"));
                if (material == null || !material.isItem()) material = Material.STONE;
                int amount = Math.max(1, order.getInt("amount", 1));
                double reward = Math.max(0, order.getDouble("reward", 0));
                ItemStack icon = new ItemStack(material);
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(color(order.getString("display-name", id)));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Deliver: " + ChatColor.WHITE + amount + "x " + pretty(material));
                lore.add(ChatColor.GRAY + "Reward: " + ChatColor.GREEN + "$" + reward);
                lore.add(""); lore.add(ChatColor.YELLOW + "Click to submit items from your inventory");
                meta.setLore(lore); meta.getPersistentDataContainer().set(orderKey, PersistentDataType.STRING, id);
                icon.setItemMeta(meta); inv.setItem(slot++, icon);
            }
        }
        player.openInventory(inv);
    }
    @EventHandler public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof OrderHolder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || event.getClickedInventory() != event.getView().getTopInventory()) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        String id = clicked.getItemMeta().getPersistentDataContainer().get(orderKey, PersistentDataType.STRING);
        if (id == null) return;
        ConfigurationSection order = plugin.getConfig().getConfigurationSection("orders." + id);
        if (order == null) return;
        if (plugin.economy() == null) { player.sendMessage(ChatColor.RED + "Economy unavailable. Install Vault and an economy plugin."); return; }
        Material material = Material.matchMaterial(order.getString("material", "STONE"));
        int required = order.getInt("amount", 1);
        double reward = order.getDouble("reward", 0);
        if (material == null || !material.isItem() || required < 1 || reward <= 0) { player.sendMessage(ChatColor.RED + "This order is configured incorrectly."); return; }
        if (count(player, material) < required) { player.sendMessage(ChatColor.RED + "You need " + required + " " + pretty(material) + "."); return; }
        remove(player, material, required);
        plugin.economy().depositPlayer(player, reward);
        player.sendMessage(ChatColor.GREEN + "Order completed! You received $" + reward + ".");
        player.closeInventory();
    }
    private int count(Player p, Material m) { int total=0; for (ItemStack item : p.getInventory().getStorageContents()) if (item != null && item.getType()==m) total += item.getAmount(); return total; }
    private void remove(Player p, Material m, int amount) {
        int left=amount;
        for (int i=0; i<p.getInventory().getStorageContents().length && left>0; i++) {
            ItemStack item=p.getInventory().getItem(i);
            if (item==null || item.getType()!=m) continue;
            int take=Math.min(left,item.getAmount()); item.setAmount(item.getAmount()-take); left-=take;
            if (item.getAmount()<=0) p.getInventory().setItem(i,null);
        }
        p.updateInventory();
    }
    private String color(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
    private String pretty(Material m) { return m.name().toLowerCase().replace('_',' '); }
    private static final class OrderHolder implements InventoryHolder { @Override public Inventory getInventory() { return null; } }
}
