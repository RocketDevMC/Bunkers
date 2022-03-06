package me.redis.bunkers.utils;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

	private final ItemStack itemStack;

	public ItemBuilder(ItemStack itemStack) {
		this.itemStack = Preconditions.checkNotNull(itemStack, "ItemStack cannot be null").clone();
	}

	public ItemBuilder(Material material) {
		this(material, 1);
	}

	public ItemBuilder(Material material, int amount) {
		this(material, amount, (short) 0);
	}

	public ItemBuilder(Material material, int amount, short damage) {
		this(new ItemStack(material, amount, damage));
	}

	public ItemBuilder setType(Material material) {
		itemStack.setType(material);
		return this;
	}

	public ItemBuilder setAmount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder setDurability(int durability) {
		itemStack.setDurability((short) durability);
		return this;
	}

	public ItemBuilder setData(int data) {
		itemStack.setData(new MaterialData(itemStack.getType(), (byte) data));
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment enchantment) {
		addEnchantment(enchantment, 1);
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
		itemStack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder removeEnchantment(Enchantment enchantment) {
		itemStack.removeEnchantment(enchantment);
		return this;
	}

	public ItemBuilder clearEnchantments() {
		for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			itemStack.removeEnchantment(enchantment);
		}
		return this;
	}

	public ItemBuilder setUnbreakable(boolean unbreakable) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.spigot().setUnbreakable(unbreakable);
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setDisplayName(String name) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder addLore(String lore) {
		ItemMeta itemMeta = itemStack.getItemMeta();

		List<String> lores = itemMeta.getLore();
		if (lores == null) {
			lores = new ArrayList<>();
		}
		lores.add(ChatColor.translateAlternateColorCodes('&', lore));

		itemMeta.setLore(lores);

		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setLore(String... lores) {
		clearLore();
		for (String lore : lores) {
			addLore(lore);
		}
		return this;
	}

	public ItemBuilder clearLore() {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(new ArrayList<>());
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setPotionEffect(PotionEffect effect) {
		PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
		potionMeta.setMainEffect(effect.getType());
		potionMeta.addCustomEffect(effect, false);
		itemStack.setItemMeta(potionMeta);
		return this;
	}

	public ItemBuilder setPotionEffects(PotionEffect... effects) {
		PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
		potionMeta.setMainEffect(effects[0].getType());
		for (PotionEffect effect : effects) {
			potionMeta.addCustomEffect(effect, false);
		}
		itemStack.setItemMeta(potionMeta);
		return this;
	}

	public ItemBuilder setSkullOwner(String owner) {
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		skullMeta.setOwner(owner);
		itemStack.setItemMeta(skullMeta);
		return this;
	}

	public ItemStack create() {
		return itemStack;
	}
}