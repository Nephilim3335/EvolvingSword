package com.example.evolvingsword;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class EvolvingSwordPlugin extends JavaPlugin implements CommandExecutor {

    private NamespacedKey killsKey;
    private NamespacedKey stageKey;

    @Override
    public void onEnable() {
        killsKey = new NamespacedKey(this, "kills");
        stageKey = new NamespacedKey(this, "stage");

        Bukkit.getPluginManager().registerEvents(new SwordListener(this), this);
        this.getCommand("giveevolvingsword").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs !");
            return true;
        }

        Player player = (Player) sender;
        ItemStack sword = createEvolvingSword();
        player.getInventory().addItem(sword);
        player.sendMessage("§aTu as reçu ton épée évolutive !");
        return true;
    }

    public ItemStack createEvolvingSword() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§bÉpée évolutive I");
            meta.setCustomModelData(101);

            AttributeModifier damageModifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "generic.attackDamage",
                    6.0,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);

            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(killsKey, PersistentDataType.INTEGER, 0);
            data.set(stageKey, PersistentDataType.INTEGER, 1);

            item.setItemMeta(meta);
        }
        return item;
    }

    public NamespacedKey getKillsKey() {
        return killsKey;
    }

    public NamespacedKey getStageKey() {
        return stageKey;
    }
}
