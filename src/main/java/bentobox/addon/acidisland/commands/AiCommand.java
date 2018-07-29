package bentobox.addon.acidisland.commands;

import java.util.ArrayList;
import java.util.List;

import bentobox.addon.acidisland.AcidIsland;
import world.bentobox.bbox.api.addons.Addon;
import world.bentobox.bbox.api.commands.CompositeCommand;
import world.bentobox.bbox.api.user.User;
import world.bentobox.bbox.commands.island.IslandAboutCommand;
import world.bentobox.bbox.commands.island.IslandBanCommand;
import world.bentobox.bbox.commands.island.IslandBanlistCommand;
import world.bentobox.bbox.commands.island.IslandCreateCommand;
import world.bentobox.bbox.commands.island.IslandGoCommand;
import world.bentobox.bbox.commands.island.IslandLanguageCommand;
import world.bentobox.bbox.commands.island.IslandResetCommand;
import world.bentobox.bbox.commands.island.IslandResetnameCommand;
import world.bentobox.bbox.commands.island.IslandSethomeCommand;
import world.bentobox.bbox.commands.island.IslandSetnameCommand;
import world.bentobox.bbox.commands.island.IslandSettingsCommand;
import world.bentobox.bbox.commands.island.IslandUnbanCommand;
import world.bentobox.bbox.commands.island.team.IslandTeamCommand;

public class AiCommand extends CompositeCommand {

    public AiCommand(Addon addon) {
        super(addon, "ai");
    }

    /* (non-Javadoc)
     * @see world.bentobox.bbox.api.commands.CompositeCommand#setup()
     */
    @Override
    public void setup() {
        setDescription("commands.island.help.description");
        setOnlyPlayer(true);
        // Permission
        setPermissionPrefix("acidisland");
        setPermission("island");
        // Set up world
        setWorld(((AcidIsland)getAddon()).getIslandWorld());
        // Set up subcommands
        new IslandAboutCommand(this);
        new IslandCreateCommand(this);
        new IslandGoCommand(this);
        new IslandResetCommand(this);
        new IslandSetnameCommand(this);
        new IslandResetnameCommand(this);
        new IslandSethomeCommand(this);
        new IslandSettingsCommand(this);
        new IslandLanguageCommand(this);
        new IslandBanCommand(this);
        new IslandUnbanCommand(this);
        new IslandBanlistCommand(this);
        // Team commands
        new IslandTeamCommand(this);
    }

    /* (non-Javadoc)
     * @see world.bentobox.bbox.api.commands.CommandArgument#execute(org.bukkit.command.CommandSender, java.lang.String[])
     */
    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (user == null) {
            return false;
        }
        if (args.isEmpty()) {
            // If in world, go
            if (getPlugin().getIslands().getIsland(getWorld(), user.getUniqueId()) != null) {
                return getSubCommand("go").map(goCmd -> goCmd.execute(user, label, new ArrayList<>())).orElse(false);
            }
            // No islands currently
            return getSubCommand("create").map(createCmd -> createCmd.execute(user, label, new ArrayList<>())).orElse(false);
        }
        user.sendMessage("general.errors.unknown-command", "[label]", getLabel());
        return false;

    }
}
