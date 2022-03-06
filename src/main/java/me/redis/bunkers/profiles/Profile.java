package me.redis.bunkers.profiles;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.profiles.status.PlayerStatus;
import me.redis.bunkers.protocol.ClaimPillar;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;

@Getter
public class Profile {
    private UUID uniqueId;
    @Setter private String name;

    @Setter private int gamesWon, gamesPlayed, kills, deaths, matchKills, balance;
    @Setter private PlayerStatus status = PlayerStatus.LOBBY;

    @Setter private ClaimPillar firstPillar; 
    @Setter private ClaimPillar secondPillar;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;
        load();
    }

    public void load() {
        Document document = (Document) Bunkers.getPlugin().getProfilesCollection().find(Filters.eq("_id", uniqueId)).first();

        if (document == null) return;

        name = document.getString("name");
        gamesPlayed = document.getInteger("gamesPlayed");
        gamesWon = document.getInteger("gamesWon");
        kills = document.getInteger("kills");
        deaths = document.getInteger("deaths");
    }

    public void save() {
        Document document = new Document("_id", uniqueId);

        document.put("name", name);
        document.put("gamesPlayed", gamesPlayed);
        document.put("gamesWon", gamesWon);
        document.put("kills", kills);
        document.put("deaths", deaths);

        Bson filter = Filters.eq("_id", uniqueId);
        FindIterable iterable = Bunkers.getPlugin().getProfilesCollection().find(filter);

        if (iterable.first() == null) {
            Bunkers.getPlugin().getProfilesCollection().insertOne(document);
        } else {
            Bunkers.getPlugin().getProfilesCollection().replaceOne(filter, document);
        }
    }
}
