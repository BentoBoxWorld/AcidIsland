/**
 *
 */
package world.bentobox.acidisland.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.CommandsManager;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class, User.class })
public class AcidCommandTest {

    private static final int NUM_COMMANDS = 28;
    private User user;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Set up plugin
        BentoBox plugin = mock(BentoBox.class);
        Whitebox.setInternalState(BentoBox.class, "instance", plugin);

        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // Player
        Player p = mock(Player.class);
        // Sometimes use Mockito.withSettings().verboseLogging()
        user = mock(User.class);
        when(user.isOp()).thenReturn(false);
        UUID uuid = UUID.randomUUID();
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(p);
        when(user.getName()).thenReturn("tastybento");
        User.setPlugin(plugin);

        // Locales
        // Return the reference (USE THIS IN THE FUTURE)
        when(user.getTranslation(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> invocation.getArgumentAt(0, String.class));

    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AcidCommand#AcidCommand(world.bentobox.bentobox.api.addons.Addon, java.lang.String)}.
     */
    @Test
    public void testAcidCommand() {
        Addon addon = mock(Addon.class);
        AcidCommand cmd = new AcidCommand(addon, "acid");
        assertEquals("acid", cmd.getLabel());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AcidCommand#setup()}.
     */
    @Test
    public void testSetup() {
        Addon addon = mock(Addon.class);
        when(addon.getPermissionPrefix()).thenReturn("acidisland.");
        AcidCommand cmd = new AcidCommand(addon, "acid");
        assertEquals("acidisland.admin", cmd.getPermission());
        assertFalse(cmd.isOnlyPlayer());
        assertEquals("commands.admin.help.parameters", cmd.getParameters());
        assertEquals("commands.admin.help.description", cmd.getDescription());
        // Number of commands = sub commands + help
        assertEquals("Number of sub commands registered", NUM_COMMANDS, cmd.getSubCommands().values().size());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AcidCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringUnknownCommand() {
        Addon addon = mock(Addon.class);
        AcidCommand cmd = new AcidCommand(addon, "acid");
        assertFalse(cmd.execute(user, "acid", Collections.singletonList("unknown")));
        Mockito.verify(user).sendMessage("general.errors.unknown-command", TextVariables.LABEL, "acid");
    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AcidCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringNoCommand() {
        Addon addon = mock(Addon.class);
        AcidCommand cmd = new AcidCommand(addon, "acid");
        assertTrue(cmd.execute(user, "acid", Collections.emptyList()));
        // Show help
        Mockito.verify(user).sendMessage("commands.help.header", TextVariables.LABEL, "commands.help.console");
    }

}
