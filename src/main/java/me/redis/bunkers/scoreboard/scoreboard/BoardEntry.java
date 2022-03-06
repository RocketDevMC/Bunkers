package me.redis.bunkers.scoreboard.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Accessors(chain = true)
public class BoardEntry {
    
    @Getter private Board board;
    @Getter @Setter private String text;
    @Getter private String originalText;
    @Getter private String key;
    @Getter private Team team;

    public BoardEntry(Board board, String text) {
        this.board = board;
        this.text = text;
        this.originalText = text;
        this.key = board.getNewKey(this);

        setup();
    }

    public BoardEntry setup() {
        Scoreboard scoreboard = board.getScoreboard();

        text = ChatColor.translateAlternateColorCodes('&', text);

        String teamName = key;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        if (scoreboard.getTeam(teamName) != null) {
            team = scoreboard.getTeam(teamName);
        } else {
            team = scoreboard.registerNewTeam(teamName);
        }

        if (!(team.getEntries().contains(key))) {
            team.addEntry(key);
        }

        if (!(board.getEntries().contains(this))) {
            board.getEntries().add(this);
        }

        return this;
    }

    public BoardEntry send(int position) {
        Objective objective = board.getObjective();

        if (text.length() <= 16) {
            team.setPrefix(text);
            team.setSuffix("");
        } else {
            String left = text.substring(0, 16), right = "";
            if (left.endsWith("\u00a7")) {
                left = left.substring(0, left.length() - 1);
                right = "\u00a7";
            }
            right = StringUtils.left(ChatColor.getLastColors(left) + right + text.substring(16), 16);
            team.setPrefix(left);
            team.setSuffix(right);
        }
        Score score = objective.getScore(key);
        score.setScore(position);

        return this;
    }

    public void remove() {
        board.getKeys().remove(key);
        board.getScoreboard().resetScores(key);
    }

}
