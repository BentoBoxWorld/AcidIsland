package bskyblock.addon.acidisland.commands;

import java.util.List;

import bskyblock.addon.acidisland.AcidIsland;
import us.tastybento.bskyblock.api.addons.Addon;
import us.tastybento.bskyblock.api.commands.CompositeCommand;
import us.tastybento.bskyblock.api.user.User;
import us.tastybento.bskyblock.commands.admin.AdminGetRankCommand;
import us.tastybento.bskyblock.commands.admin.AdminInfoCommand;
import us.tastybento.bskyblock.commands.admin.AdminRegisterCommand;
import us.tastybento.bskyblock.commands.admin.AdminReloadCommand;
import us.tastybento.bskyblock.commands.admin.AdminSchemCommand;
import us.tastybento.bskyblock.commands.admin.AdminSetRankCommand;
import us.tastybento.bskyblock.commands.admin.AdminTeleportCommand;
import us.tastybento.bskyblock.commands.admin.AdminUnregisterCommand;
import us.tastybento.bskyblock.commands.admin.AdminVersionCommand;
import us.tastybento.bskyblock.commands.admin.teams.AdminTeamAddCommand;
import us.tastybento.bskyblock.commands.admin.teams.AdminTeamDisbandCommand;
import us.tastybento.bskyblock.commands.admin.teams.AdminTeamKickCommand;
import us.tastybento.bskyblock.commands.admin.teams.AdminTeamMakeLeaderCommand;

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
    }

    @Override
    public boolean execute(User user, List<String> args) {
        if (!args.isEmpty()) {
            user.sendMessage("general.errors.unknown-command", "[label]", "acid");
            return false;
        }
        // By default run the attached help command, if it exists (it should)
        return showHelp(this, user);
    }


}
