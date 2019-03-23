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
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class, User.class })
public class AiCommandTest {

    private User user;
    private IslandsManager im;
    private Island island;

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

        // Island World Manager
        IslandWorldManager iwm = mock(IslandWorldManager.class);
        when(plugin.getIWM()).thenReturn(iwm);


        // Player has island to begin with
        im = mock(IslandsManager.class);
        island = mock(Island.class);
        when(im.getIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(island);
        when(plugin.getIslands()).thenReturn(im);

        // Locales
        // Return the reference (USE THIS IN THE FUTURE)
        when(user.getTranslation(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> invocation.getArgumentAt(0, String.class));

    }


    /**
     * Test method for {@link world.bentobox.acidisland.commands.AiCommand#AiCommand(world.bentobox.bentobox.api.addons.Addon, java.lang.String)}.
     */
    @Test
    public void testAiCommand() {
        Addon addon = mock(Addon.class);
        AiCommand cmd = new AiCommand(addon, "ai");
        assertEquals("ai", cmd.getLabel());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AiCommand#setup()}.
     */
    @Test
    public void testSetup() {
        Addon addon = mock(Addon.class);
        when(addon.getPermissionPrefix()).thenReturn("acidisland.");
        AiCommand cmd = new AiCommand(addon, "ai");
        assertEquals("acidisland.island", cmd.getPermission());
        assertTrue(cmd.isOnlyPlayer());
        assertEquals("commands.ai.parameters", cmd.getParameters());
        assertEquals("commands.island.help.description", cmd.getDescription());
        // Number of commands = sub commands + help
        assertEquals("Number of sub commands registered", 17, cmd.getSubCommands().values().size());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AiCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringNullUsers() {
        Addon addon = mock(Addon.class);
        AiCommand cmd = new AiCommand(addon, "ai");
        assertFalse(cmd.execute(null, "ai", Collections.emptyList()));

    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AiCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringUnknownCommand() {
        Addon addon = mock(Addon.class);
        AiCommand cmd = new AiCommand(addon, "ai");
        assertFalse(cmd.execute(user, "ai", Collections.singletonList("unknown")));
        Mockito.verify(user).sendMessage("general.errors.unknown-command", TextVariables.LABEL, "ai");
    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AiCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringNoArgs() {
        Addon addon = mock(Addon.class);
        AiCommand cmd = new AiCommand(addon, "ai");
        assertTrue(cmd.execute(user, "ai", Collections.emptyList()));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.commands.AiCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringNoArgsNoIsland() {
        island = null;
        Addon addon = mock(Addon.class);
        AiCommand cmd = new AiCommand(addon, "ai");
        assertTrue(cmd.execute(user, "ai", Collections.emptyList()));
    }

}
