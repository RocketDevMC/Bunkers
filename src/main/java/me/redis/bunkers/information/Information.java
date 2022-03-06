package me.redis.bunkers.information;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.utils.Cuboid;
import me.redis.bunkers.utils.LocationUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Location;

@Getter
public class Information {
    @Setter private String lobbyLocation, kothFirstLocation, kothSecondLocation;
    @Setter private String serverName;
    @Setter private int minPlayers = 5;
    @Setter private String address = "none";

    public Information() {
        load();
    }


    public void load() {
        Document document = (Document) Bunkers.getPlugin().getInformationCollection().find(Filters.eq("_id", "Information")).first();

        if (document == null) return;

        lobbyLocation = document.getString("lobbyLocation");
        serverName = document.getString("serverName");
        kothFirstLocation = document.getString("kothFirstLocation");
        kothSecondLocation = document.getString("kothSecondLocation");
        minPlayers = document.getInteger("minPlayers");
        address = document.getString("address");
    }

    public void save() {
        Document document = new Document("_id", "Information");

        document.put("lobbyLocation", lobbyLocation);
        document.put("serverName", serverName);
        document.put("kothFirstLocation", kothFirstLocation);
        document.put("kothSecondLocation", kothSecondLocation);
        document.put("minPlayers", minPlayers);
        document.put("address", address);

        Bson filter = Filters.eq("_id", "Information");
        FindIterable iterable = Bunkers.getPlugin().getInformationCollection().find(filter);

        if (iterable.first() == null) {
            Bunkers.getPlugin().getInformationCollection().insertOne(document);
        } else {
            Bunkers.getPlugin().getInformationCollection().replaceOne(filter, document);
        }

        Bunkers.getPlugin().getInformationManager().setInformation(this);
    }

    public Cuboid getKothCuboid() {
        return new Cuboid(LocationUtils.getLocation(kothFirstLocation), LocationUtils.getLocation(kothSecondLocation));
    }

    public Location getKothCenter() {
        return getKothCuboid().getCenter();
    }
}
