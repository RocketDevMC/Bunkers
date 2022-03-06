package me.redis.bunkers.menu.type;

import lombok.Getter;
import me.redis.bunkers.menu.IMenu;
import me.redis.bunkers.menu.InventoryUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

@Getter
public abstract class PaginatedMenu<E extends JavaPlugin> implements IMenu {

    protected E plugin;

    private int page;

    public final Inventory inventory;

    public PaginatedMenu(int size, int page) {
        loadPlugin();

        this.page = page;

        inventory = plugin.getServer().createInventory(this, size, getTitle().length() > 32 ? getTitle().substring(0, 32) : getTitle());

        updateContents();
    }

    public abstract int getTotalPages();

    public abstract void updateContents();

    private void loadPlugin() {
        Class<E> clazz = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

        Method method = null;
        try {
            method = clazz.getDeclaredMethod("getPlugin", null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            plugin = (E) method.invoke(null, method.getParameterTypes());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setPage(int page) {
        this.page = page;

        for (HumanEntity viewer : getInventory().getViewers()) {
            InventoryUtil.changeTitle((Player) viewer, getTitle());
        }

        updateContents();
    }

    @Override
    public void open(Player player) {
        player.openInventory(getInventory());

        setPage(getPage());
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.equals(inventory)) {
            event.setCancelled(true);
        }
    }
}