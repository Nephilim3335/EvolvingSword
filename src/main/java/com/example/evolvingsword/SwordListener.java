package com.example.evolvingsword;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SwordListener implements Listener {

    private final EvolvingSwordPlugin plugin;

    public SwordListener(EvolvingSwordPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        ItemStack item = event.getEntity().getKiller().getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        int kills = data.getOrDefault(plugin.getKillsKey(), PersistentDataType.INTEGER, 0) + 1;
        int stage = data.getOrDefault(plugin.getStageKey(), PersistentDataType.INTEGER, 1);

        data.set(plugin.getKillsKey(), PersistentDataType.INTEGER, kills);
        updateLore(meta, kills, stage);

        if (stage == 1 && kills >= 50) {
            upgradeSword(item, meta, 2, 8, "§bÉpée évolutive II", 102);
        } else if (stage == 2 && kills >= 150) {
            upgradeSword(item, meta, 3, 11, "§bÉpée évolutive III", 103);
        } else if (stage == 3 && kills >= 300) {
            upgradeSword(item, meta, 4, 15, "§bÉpée évolutive IV", 104);
        } else if (stage == 4 && kills >= 600) {
            upgradeSword(item, meta, 5, 20, "§bÉpée évolutive V", 105);
        }

        item.setItemMeta(meta);
    }

    private void upgradeSword(ItemStack item, ItemMeta meta, int newStage, double newDamage, String newName, int modelData) {
        meta.setDisplayName(newName);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(plugin.getStageKey(), PersistentDataType.INTEGER, newStage);

        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);

        AttributeModifier damageModifier = new AttributeModifier(
                UUID.randomUUID(),
                "generic.attackDamage",
                newDamage,
                AttributeModifier.Operation.ADD_NUMBER
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);

        meta.setCustomModelData(modelData);

        int kills = data.getOrDefault(plugin.getKillsKey(), PersistentDataType.INTEGER, 0);
        updateLore(meta, kills, newStage);

        Bukkit.broadcastMessage("§aTon épée a évolué en stade " + newStage + " !");
    }

    private void updateLore(ItemMeta meta, int kills, int stage) {
        List<String> lore = new ArrayList<>();
        int nextReq = 0;
        String nextStage = "";

        switch (stage) {
            case 1: nextReq = 50; nextStage = "II"; break;
            case 2: nextReq = 150; nextStage = "III"; break;
            case 3: nextReq = 300; nextStage = "IV"; break;
            case 4: nextReq = 600; nextStage = "V"; break;
            case 5: nextReq = -1; nextStage = "MAX"; break;
        }

        if (stage < 5) {
            lore.add("§7Kills : " + kills + " / " + nextReq);
            lore.add("§eProchaine évolution : Stade " + nextStage);
        } else {
            lore.add("§aÉvolution maximale atteinte !");
            lore.add("§7Kills totaux : " + kills);
        }

        meta.setLore(lore);
    }
}
