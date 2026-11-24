package me.mord.orders;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ShardsCommand implements CommandExecutor {
    private final ShardsManager shards;
    public ShardsCommand(ShardsManager shards) { this.shards = shards; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
            player.sendMessage(ChatColor.AQUA + "Your Shards: " + ChatColor.WHITE + format(shards.getBalance(player.getUniqueId())));
            return true;
        }
        if (!sender.hasPermission("mordorders.admin")) { sender.sendMessage(ChatColor.RED + "No permission."); return true; }
        if (args.length != 3) { sender.sendMessage(ChatColor.YELLOW + "/shards <give|take|set> <player> <amount>"); return true; }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) { sender.sendMessage(ChatColor.RED + "That player must be online."); return true; }
        double amount;
        try { amount = Double.parseDouble(args[2]); } catch (NumberFormatException ex) { sender.sendMessage(ChatColor.RED + "Invalid amount."); return true; }
        if (!Double.isFinite(amount) || amount <= 0) { sender.sendMessage(ChatColor.RED + "Amount must be greater than zero."); return true; }
        switch (args[0].toLowerCase()) {
            case "give" -> shards.add(target.getUniqueId(), amount);
            case "take" -> { if (!shards.take(target.getUniqueId(), amount)) { sender.sendMessage(ChatColor.RED + "Player does not have enough Shards."); return true; } }
            case "set" -> shards.set(target.getUniqueId(), amount);
            default -> { sender.sendMessage(ChatColor.YELLOW + "Use give, take, or set."); return true; }
        }
        sender.sendMessage(ChatColor.GREEN + "Updated " + target.getName() + "'s Shards balance to " + format(shards.getBalance(target.getUniqueId())) + ".");
        target.sendMessage(ChatColor.AQUA + "Your Shards balance: " + ChatColor.WHITE + format(shards.getBalance(target.getUniqueId())));
        return true;
    }
    private String format(double n) { return n == Math.floor(n) ? String.valueOf((long)n) : String.valueOf(n); }
}
