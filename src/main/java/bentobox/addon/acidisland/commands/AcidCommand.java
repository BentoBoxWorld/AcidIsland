package bentobox.addon.acidisland.commands;

import java.util.List;

import bentobox.addon.acidisland.AcidIsland;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.commands.admin.AdminClearResetsAllCommand;
import world.bentobox.bentobox.commands.admin.AdminClearResetsCommand;
import world.bentobox.bentobox.commands.admin.AdminGetRankCommand;
import world.bentobox.bentobox.commands.admin.AdminInfoCommand;
import world.bentobox.bentobox.commands.admin.AdminRegisterCommand;
import world.bentobox.bentobox.commands.admin.AdminReloadCommand;
import world.bentobox.bentobox.commands.admin.AdminSchemCommand;
import world.bentobox.bentobox.commands.admin.AdminSetRankCommand;
import world.bentobox.bentobox.commands.admin.AdminTeleportCommand;
import world.bentobox.bentobox.commands.admin.AdminUnregisterCommand;
import world.bentobox.bentobox.commands.admin.AdminVersionCommand;
import world.bentobox.bentobox.commands.admin.range.AdminRangeCommand;
import world.bentobox.bentobox.commands.admin.team.AdminTeamAddCommand;
import world.bentobox.bentobox.commands.admin.team.AdminTeamDisbandCommand;
import world.bentobox.bentobox.commands.admin.team.AdminTeamKickCommand;
import world.bentobox.bentobox.commands.admin.team.AdminTeamMakeLeaderCommand;

public class AcidCommand extends CompositeCommand {

    public AcidCommand(Addon addon) {
        super(addon, "acid");
    }

    @Override
    public void setup() {
        setPermissionPrefix("acidisland");
        setPermission("acidisland.admin.*");
        setOnlyPlayer(false);
        setParameters("commands.admin.help.parameters");
        setDescription("commands.admin.help.description");
        setWorld(((AcidIsland)getAddon()).getIslandWorld());

        // Team commands
        new AdminTeamAddCommand(this);
        new AdminTeamKickCommand(this);
        new AdminTeamDisbandCommand(this);
        new AdminTeamMakeLeaderCommand(this);
        // Other
        new AdminGetRankCommand(this);
        new AdminInfoCommand(this);
        new AdminRegisterCommand(this);
        new AdminReloadCommand(this);
        new AdminSchemCommand(this);
        new AdminSetRankCommand(this);
        new AdminTeleportCommand(this, "tp");
        new AdminTeleportCommand(this, "tpnether");
        new AdminTeleportCommand(this, "tpend");
        new AdminUnregisterCommand(this);
        new AdminVersionCommand(this);
        // Range
        new AdminRangeCommand(this);
        // Resets
        new AdminClearResetsCommand(this);
        new AdminClearResetsAllCommand(this);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (!args.isEmpty()) {
            user.sendMessage("general.errors.unknown-command", "[label]", "acid");
            return false;
        }
        // By default run the attached help command, if it exists (it should)
        return showHelp(this, user);
    }


}
