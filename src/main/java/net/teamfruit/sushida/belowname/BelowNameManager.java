package net.teamfruit.sushida.belowname;

import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BelowNameManager {
    public static final NamespacedKey NAME_TAG_ENTITY_KEY = new NamespacedKey(Sushida.plugin, "sushida_tag");

    private Set<Entity> managed = new HashSet<>();

    private ArmorStand spawnTemplate(Player player) {
        return player.getWorld().spawn(player.getLocation(), ArmorStand.class, e -> {
            e.setGravity(false);
            e.setVisible(false);
            e.setInvulnerable(true);
            e.setCustomName(ChatColor.WHITE + "");
            e.setCustomNameVisible(true);
            e.setSmall(true);
            e.getPersistentDataContainer().set(NAME_TAG_ENTITY_KEY, PersistentDataType.BYTE, (byte) 1);
            managed.add(e);
        });
    }

    private Slime spawnSlimeTemplate(Player player) {
        return player.getWorld().spawn(player.getLocation(), Slime.class, e -> {
            e.setPersistent(true);
            e.setCollidable(false);
            e.setRotation(0, 0);
            e.setSize(1);
            e.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            e.setInvulnerable(true);
            e.setSilent(true);
            e.setGravity(false);
            e.setAI(false);
            e.getPersistentDataContainer().set(NAME_TAG_ENTITY_KEY, PersistentDataType.BYTE, (byte) 1);
            managed.add(e);
        });
    }

    public void spawn(PlayerData playerData) {
        despawn(playerData);
        if (!playerData.hasSession())
            return;
        Player player = playerData.player;
        Slime glue0 = spawnSlimeTemplate(player);
        Slime glue1 = spawnSlimeTemplate(player);
        Slime glue2 = spawnSlimeTemplate(player);
        ArmorStand line0 = spawnTemplate(player);
        ArmorStand line1 = spawnTemplate(player);
        ArmorStand line2 = spawnTemplate(player);
        line0.setMarker(true);
        line1.setMarker(true);
        line2.setMarker(true);
        line2.setCustomName(playerData.player.getName());
        player.addPassenger(glue0);
        glue0.addPassenger(line0);
        line0.addPassenger(glue1);
        glue1.addPassenger(line1);
        line1.addPassenger(glue2);
        glue2.addPassenger(line2);
        playerData.entity.reference = Arrays.asList(line0, line1, line2, glue0, glue1, glue2);
    }

    public void despawn(PlayerData playerData) {
        List<Entity> entities = playerData.entity.reference;
        if (entities != null) {
            for (Entity entity : entities) {
                managed.remove(entity);
                entity.remove();
            }
            playerData.entity.reference = null;
        }
        Player player = playerData.player;
        player.getPassengers().forEach(this::checkAndRemove);
    }

    public void checkAndRemove(Entity entity) {
        if (entity.getPersistentDataContainer().has(NAME_TAG_ENTITY_KEY, PersistentDataType.BYTE))
            entity.remove();
    }

    public void clearManaged() {
        managed.forEach(Entity::remove);
        managed.clear();
    }

    public static class NameTagReference {
        public List<Entity> reference = null;

        public void setTypingText(String text) {
            List<Entity> entities = this.reference;
            if (entities != null)
                entities.get(0).setCustomName(ChatColor.WHITE + text);
        }

        public void setScoreText(String text) {
            List<Entity> entities = this.reference;
            if (entities != null)
                entities.get(1).setCustomName(ChatColor.WHITE + text);
        }
    }
}
