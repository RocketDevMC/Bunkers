package me.redis.bunkers.entity.types;

import lombok.Getter;
import lombok.Setter;
import me.redis.bunkers.entity.ShopType;
import me.redis.bunkers.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public class CombatEntity {
    private Team team;
    private Location spawnLocation;
    @Setter private ShopType shopType;

    public CombatEntity(Team team, Location spawnLocation) {
        this.team = team;
        this.spawnLocation = spawnLocation;

        Villager villager = (Villager) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
        villager.setFallDistance(0);
        villager.setRemoveWhenFarAway(false);
        villager.setAdult();
        villager.setProfession(Villager.Profession.BLACKSMITH);
        villager.setCanPickupItems(false);
        villager.setMaxHealth(40);
        villager.setHealth(40);
        villager.setCustomName(team.getColor() + "Combat Shop");
        villager.setCustomNameVisible(true);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, -6, true), true);
        setShopType(ShopType.COMBAT);
    }
}
