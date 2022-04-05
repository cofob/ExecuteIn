package ru.firesquare.executein.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import redempt.redlib.commandmanager.CommandHook;
import ru.firesquare.executein.config.Messages;
import ru.firesquare.executein.ExecutePlugin;
import ru.firesquare.executein.sql.ExecuteRegion;
import ru.firesquare.executein.utils.ChatUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExecuteCommand {
    @CommandHook("add")
    public void add(CommandSender sender, String name) {
        ExecuteRegion region = new ExecuteRegion();
        List<String> commands = new ArrayList<>();
        region.setName(name);
        region.setCommands(commands);
        region.setOpCommands(commands);
        region.setX(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation().getBlockX());
        region.setY(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation().getBlockY());
        region.setZ(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation().getBlockZ());
        region.setRadius(5);
        region.setShape(0);
        region.setWorld(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getWorld().getName());
        try {
            ExecutePlugin.getInstance().getRegionDao().create(region);
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(ChatUtils.translate(Messages.error));
            return;
        }
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.success));
    }

    @CommandHook("rm")
    public void rm(CommandSender sender, String name) {
        try {
            ExecutePlugin.getInstance().getRegionDao().deleteById(name);
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(ChatUtils.translate(Messages.error));
            return;
        }
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.success));
    }

    @CommandHook("set_commands")
    public void setCommands(CommandSender sender, String name, String commands) {
        ExecuteRegion region = null;
        try {
            region = ExecutePlugin.getInstance().getRegionDao().queryForId(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert region != null;
        region.setCommands(Arrays.asList(commands.split("%n")));
        try {
            ExecutePlugin.getInstance().getRegionDao().update(region);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.success));
    }

    @CommandHook("set_op_commands")
    public void setOpCommands(CommandSender sender, String name, String commands) {
        ExecuteRegion region = null;
        try {
            region = ExecutePlugin.getInstance().getRegionDao().queryForId(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert region != null;
        region.setOpCommands(Arrays.asList(commands.split("%n")));
        try {
            ExecutePlugin.getInstance().getRegionDao().update(region);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.success));
    }

    @CommandHook("set_coords")
    public void setCoords(CommandSender sender, String name, int x, int y, int z) {
        ExecuteRegion region = null;
        try {
            region = ExecutePlugin.getInstance().getRegionDao().queryForId(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert region != null;
        region.setX(x);
        region.setY(y);
        region.setZ(z);
        try {
            ExecutePlugin.getInstance().getRegionDao().update(region);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.success));
    }

    @CommandHook("set_radius")
    public void setRadius(CommandSender sender, String name, int radius) {
        ExecuteRegion region = null;
        try {
            region = ExecutePlugin.getInstance().getRegionDao().queryForId(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert region != null;
        region.setRadius(radius);
        try {
            ExecutePlugin.getInstance().getRegionDao().update(region);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.success));
    }

    @CommandHook("reload")
    public void reloadConfig(CommandSender sender) {
        ExecutePlugin.getInstance().reloadFileConfig();
        ExecutePlugin.getInstance().reloadEvents();
        sender.sendMessage(ChatUtils.translate(Messages.reload));
    }
}
