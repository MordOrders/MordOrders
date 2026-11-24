package me.mord.orders;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class ShardsManager {
    private final File file;
    private final YamlConfiguration data;
    public ShardsManager(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "shards.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { throw new IllegalStateException("Could not create shards.yml", e); }
        data = YamlConfiguration.loadConfiguration(file);
    }
    public double getBalance(UUID id) { return data.getDouble(id.toString(), 0.0); }
    public void add(UUID id, double amount) { if (valid(amount)) { data.set(id.toString(), getBalance(id) + amount); save(); } }
    public boolean take(UUID id, double amount) {
        if (!valid(amount) || getBalance(id) < amount) return false;
        data.set(id.toString(), getBalance(id) - amount); save(); return true;
    }
    public void set(UUID id, double amount) { if (Double.isFinite(amount) && amount >= 0) { data.set(id.toString(), amount); save(); } }
    private boolean valid(double amount) { return Double.isFinite(amount) && amount > 0; }
    private void save() { try { data.save(file); } catch (IOException e) { throw new IllegalStateException("Could not save shards.yml", e); } }
}
