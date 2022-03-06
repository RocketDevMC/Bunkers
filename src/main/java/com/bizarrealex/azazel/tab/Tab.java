package com.bizarrealex.azazel.tab;

import com.bizarrealex.azazel.Azazel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Tab {

    @Getter private Scoreboard scoreboard;
    @Getter @Setter private Team elevatedTeam;
    private Map<TabEntryPosition, String> entries;

    public Tab(Player player, boolean hook, Azazel azazel) {
        this.entries = new ConcurrentHashMap<>();

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (hook && !player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = player.getScoreboard();
        }

        elevatedTeam = scoreboard.registerNewTeam(getBlanks().get(getBlanks().size() - 1));

        for (Player other : Bukkit.getOnlinePlayers()) {

            getElevatedTeam(other, azazel).addEntry(other.getName());

            Tab tab = azazel.getTabByPlayer(other);
            if (tab != null) {
                tab.getElevatedTeam(player, azazel).addEntry(player.getName());
            }

            PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) other).getHandle());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        player.setScoreboard(scoreboard);

        initialize(player);
    }

    public Team getElevatedTeam(Player player, Azazel azazel) {
        if (player.hasMetadata("HydrogenPrefix")) {
            String prefix = ChatColor.getLastColors(player.getDisplayName().replace(ChatColor.RESET + "", ""));

            String name = getBlanks().get(getBlanks().size() - 1) + prefix;
            if (name.length() > 16) {
                name = name.substring(0, 15);
            }

            Team team = scoreboard.getTeam(name);

            if (team == null) {
                team = scoreboard.registerNewTeam(name);

                team.setPrefix(prefix);

            }

            return team;
        }

        return elevatedTeam;
    }

    public Set<TabEntryPosition> getPositions() {
        return entries.keySet();
    }

    public Team getByLocation(int x, int y) {
        for (TabEntryPosition position : entries.keySet()) {
            if (position.getX() == x && position.getY() == y) {
                return scoreboard.getTeam(position.getKey());
            }
        }
        return null;
    }

    private void initialize(Player player) {
        if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 20; y++) {
                    String key = getNextBlank();
                    TabEntryPosition position = new TabEntryPosition(x, y, key, scoreboard);

                    entries.put(position, key);

                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getPlayerPacket(entries.get(position)));

                    Team team = scoreboard.getTeam(position.getKey());

                    if (team == null) {
                        team = scoreboard.registerNewTeam(position.getKey());
                    }

                    team.addEntry(entries.get(position));
                }
            }
        } else {
            for (int i = 0; i < 60; i++) {
                int x = i % 3;
                int y = i / 3;

                String key = getNextBlank();
                TabEntryPosition position = new TabEntryPosition(x, y, key, scoreboard);
                entries.put(position, key);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getPlayerPacket(entries.get(position)));

                Team team = scoreboard.getTeam(position.getKey());

                if (team == null) {
                    team = scoreboard.registerNewTeam(position.getKey());
                }

                team.addEntry(entries.get(position));
            }
        }
    }

    private String getNextBlank() {
        outer:
        for (String blank : getBlanks()) {

            if (scoreboard.getTeam(blank) != null) {
                continue;
            }

            for (String identifier : entries.values()) {
                if (identifier.equals(blank)) {
                    continue outer;
                }
            }
            return blank;
        }
        return null;
    }

    public List<String> getBlanks() {
        List<String> toReturn = new ArrayList<>();

        for (ChatColor color : ChatColor.values()) {
            for (int i = 0; i < 4; i++) {

                String identifier = StringUtils.repeat(color + "", 4 - i) + ChatColor.RESET;
                toReturn.add(identifier);
            }
        }

        return toReturn;
    }


    /*
        There should be a better way to do this without reflection
     */
    private static Packet getPlayerPacket(String name) {
        return getPlayerPacket(name, 1);
    }

    private static Packet getPlayerPacket(String name, int ping) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        Field action;
        Field username;
        Field player;
        Field latency;
        try {
            action = PacketPlayOutPlayerInfo.class.getDeclaredField("action");
            username = PacketPlayOutPlayerInfo.class.getDeclaredField("username");
            player = PacketPlayOutPlayerInfo.class.getDeclaredField("player");
            latency = PacketPlayOutPlayerInfo.class.getDeclaredField("ping");

            action.setAccessible(true);
            username.setAccessible(true);
            player.setAccessible(true);
            latency.setAccessible(true);

            action.set(packet, 0);
            username.set(packet, name);
            latency.set(packet, ping);

            GameProfile profile = new GameProfile(UUID.randomUUID(), name);
            profile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=", "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="));

            player.set(packet, profile);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static class TabEntryPosition {
        @Getter private final int x, y;
        @Getter private final String key;

        public TabEntryPosition(int x, int y, String key, Scoreboard scoreboard) {
            this.x = x;
            this.y = y;
            this.key = key;
        }
    }
}
