package bentobox.addon.acidisland.commands;

import java.util.List;

import bentobox.addon.acidisland.AcidIsland;
import world.bentobox.bbox.api.addons.Addon;
import world.bentobox.bbox.api.commands.CompositeCommand;
import world.bentobox.bbox.api.user.User;
import world.bentobox.bbox.commands.admin.AdminClearResetsAllCommand;
import world.bentobox.bbox.commands.admin.AdminClearResetsCommand;
import world.bentobox.bbox.commands.admin.AdminGetRankCommand;
import world.bentobox.bbox.commands.admin.AdminInfoCommand;
import world.bentobox.bbox.commands.admin.AdminRegisterCommand;
import world.bentobox.bbox.commands.admin.AdminReloadCommand;
import world.bentobox.bbox.commands.admin.AdminSchemCommand;
import world.bentobox.bbox.commands.admin.AdminSetRankCommand;
import world.bentobox.bbox.commands.admin.AdminTeleportCommand;
import world.bentobox.bbox.commands.admin.AdminUnregisterCommand;
import world.bentobox.bbox.commands.admin.AdminVersionCommand;
import world.bentobox.bbox.commands.admin.range.AdminRangeCommand;
import world.bentobox.bbox.commands.admin.team.AdminTeamAddCommand;
import world.bentobox.bbox.commands.admin.team.AdminTeamDisbandCommand;
import world.bentobox.bbox.commands.admin.team.AdminTeamKickCommand;
import world.bentobox.bbox.commands.admin.team.AdminTeamMakeLeaderCommand;

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
