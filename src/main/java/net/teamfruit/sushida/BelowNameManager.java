package net.teamfruit.sushida;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BelowNameManager {
    public static final NamespacedKey NAME_TAG_ENTITY_KEY = new NamespacedKey(Sushida.plugin, "sushida_tag");

    private final Set<Entity> managed = new HashSet<>();

    private Entity spawnLineTemplate(Player player) {
        return player.getWorld().spawn(player.getLocation(), ArmorStand.class, e -> {
            e.setGravity(false);
            e.setVisible(false);
            e.setInvulnerable(true);
            e.customName(Component.text().content("").color(TextColor.color(255, 255, 255)).build());
            e.setCustomNameVisible(true);
            e.setSmall(true);
            e.setMarker(true);
            e.getPersistentDataContainer().set(NAME_TAG_ENTITY_KEY, PersistentDataType.BYTE, (byte) 1);
            managed.add(e);
        });
    }

    private <T extends LivingEntity> T spawnGlueTemplate(Player player, Class<T> entityClass) {
        return player.getWorld().spawn(player.getLocation(), entityClass, e -> {
            e.setPersistent(true);
            e.setCollidable(false);
            e.setRotation(0, 0);
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
        // デスポーン
        new BukkitRunnable() {
            @Override
            public void run() {
                despawn(playerData);
            }
        }.runTask(Sushida.plugin);
        if (!playerData.hasSession())
            return;
        Player player = playerData.player;
        Entity glue0 = spawnGlueTemplate(player, Bee.class);
        Entity glue1 = spawnGlueTemplate(player, Salmon.class);
        Entity glue2 = spawnGlueTemplate(player, Salmon.class);
        Entity line0 = spawnLineTemplate(player);
        Entity line1 = spawnLineTemplate(player);
        player.addPassenger(glue0);
        glue0.addPassenger(glue1);
        glue1.addPassenger(line0);
        line0.addPassenger(glue2);
        glue2.addPassenger(line1);
        playerData.entity.reference = Arrays.asList(line0, line1, /*line2, */glue0, glue1, glue2/*, glue3*/);
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
        if (entity.getPersistentDataContainer().has(NAME_TAG_ENTITY_KEY, PersistentDataType.BYTE)) {
            entity.getPassengers().forEach(this::checkAndRemove);
            entity.remove();
        }
    }

    public void clearManaged() {
        managed.forEach(Entity::remove);
        managed.clear();
    }

    public static class NameTagReference {
        public List<Entity> reference = null;

        public void setTypingText(String text) {
            List<Entity> entities = this.reference;
            if (entities != null) {
                entities.get(0).customName(Component.text().content(text).color(TextColor.color(255, 255, 255)).build());
            }
        }

        public void setScoreText(String text) {
            List<Entity> entities = this.reference;
            if (entities != null) {
                entities.get(1).customName(Component.text().content(text).color(TextColor.color(255, 255, 255)).build());
            }
        }
    }
}
