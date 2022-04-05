package ru.firesquare.executein.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.firesquare.executein.utils.ChatUtils;

import java.util.Arrays;
import java.util.List;

@DatabaseTable(tableName = "executein_players")
public class ExecuteRegion {
    @DatabaseField(canBeNull = false, id = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private String world;

    @DatabaseField(canBeNull = false)
    private String commands;
    @DatabaseField(canBeNull = false)
    private String op_commands;

    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int y;
    @DatabaseField(canBeNull = false)
    private int z;

    @DatabaseField(canBeNull = false)
    private int radius;

    @DatabaseField(canBeNull = false)
    private int shape;

    // ORMLite boilerplate
    public ExecuteRegion() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public List<String> getCommands() {
        return Arrays.asList(commands.split("%n"));
    }

    public List<String> getOpCommands() {
        return Arrays.asList(op_commands.split("%n"));
    }

    public void setCommands(List<String> commands) {
        this.commands = String.join("%n", commands);
    }

    public void setOpCommands(List<String> commands) {
        this.op_commands = String.join("%n", commands);
    }

    public static String formatCommand(String in, Player player) {
        return ChatUtils.translate(in.replaceAll("%player%", player.getName()));
    }

    public void executeCommands(Player player) {
        for (String command : this.getCommands()) {
            player.performCommand(formatCommand(command, player));
        }
        for (String command : this.getOpCommands()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), formatCommand(command, player));
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
    }
}
