package ru.firesquare.executein;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.dev.ChainCommand;
import redempt.redlib.dev.StructureTool;
import redempt.redlib.misc.EventListener;
import redempt.redlib.region.SpheroidRegion;
import redempt.redlib.region.events.RegionEnterEvent;
import ru.firesquare.executein.commands.ExecuteCommand;
import ru.firesquare.executein.config.Config;
import ru.firesquare.executein.config.Messages;
import ru.firesquare.executein.sql.ExecuteRegion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecutePlugin extends JavaPlugin {
    @Override
    public void onEnable () {
        // Load config
        ConfigManager.create(this).target(Config.class).saveDefaults().load();
        ConfigManager.create(this, "lang.yml").target(Messages.class).saveDefaults().load();

        // Load messages
        reloadFileConfig();

        // Set static instance
        ExecutePlugin.instance = this;

        // Load and init SQL
        initSQL();

        // Load regions
        spheroidRegionsList = new ArrayList<>();
        reloadEvents();

        // Register the commands
        ChainCommand chain = new ChainCommand();
        new CommandParser(this.getResource("command.rdcml"))
                .parse()
                .register("execute", new ExecuteCommand(), StructureTool.enable(), chain);

        // Setup Vault
        setupPermissions();
    }

    private Dao<ExecuteRegion, String> regionDao;

    public void initSQL() {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(Config.database);

            // instantiate the dao's
            regionDao = DaoManager.createDao(connectionSource, ExecuteRegion.class);

            // create tables
            TableUtils.createTableIfNotExists(connectionSource, ExecuteRegion.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reloadFileConfig() {
        ConfigManager.create(this, "lang.yml").target(Messages.class).saveDefaults().reload();
        ConfigManager.create(this).target(Config.class).saveDefaults().reload();
    }

    private static Permission perms = null;

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        assert rsp != null;
        perms = rsp.getProvider();
    }

    public static Permission getPermissions() {
        return perms;
    }

    private static ExecutePlugin instance;

    public static ExecutePlugin getInstance () {
        return ExecutePlugin.instance;
    }

    public Dao<ExecuteRegion, String> getRegionDao() {
        return regionDao;
    }

    private List<SpheroidRegion> spheroidRegionsList;

    public void reloadEvents() {
        for (SpheroidRegion region : spheroidRegionsList) {
            region.disableEvents();
        }
        spheroidRegionsList = new ArrayList<>();

        try {
            for (ExecuteRegion executeRegion : getRegionDao().queryForAll()) {
                SpheroidRegion region = new SpheroidRegion(executeRegion.getLocation(), executeRegion.getRadius());
                spheroidRegionsList.add(region);
                region.enableEvents();
                new EventListener<>(ExecutePlugin.getInstance(), RegionEnterEvent.class,
                        e -> executeRegion.executeCommands(e.getPlayer()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}