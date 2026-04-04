package world.bentobox.acidisland.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.mocks.ServerMocks;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.CommandsManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IslandAboutCommandTest {

    @Mock
    private CompositeCommand parentCommand;
    @Mock
    private User user;
    @Mock
    private AcidIsland addon;
    @Mock
    private BentoBox plugin;

    private MockedStatic<Bukkit> mockedBukkit;
    private IslandAboutCommand command;

    @BeforeAll
    public static void beforeAll() {
        ServerMocks.newServer();
    }

    @BeforeEach
    public void setUp() {
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);

        when(parentCommand.getAddon()).thenReturn(addon);
        when(parentCommand.getWorld()).thenReturn(mock(org.bukkit.World.class));
        when(parentCommand.getTopLabel()).thenReturn("ai");
        when(parentCommand.getPermissionPrefix()).thenReturn("acidisland.");

        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);
        when(addon.getPlugin()).thenReturn(plugin);

        AddonDescription desc = new AddonDescription.Builder("bentobox", "AcidIsland", "1.0")
                .description("test").authors("tastybento").build();
        when(addon.getDescription()).thenReturn(desc);

        command = new IslandAboutCommand(parentCommand);
    }

    @AfterEach
    public void tearDown() {
        mockedBukkit.close();
    }

    @Test
    public void testExecute() {
        boolean result = command.execute(user, "about", Collections.emptyList());
        assertTrue(result);
        verify(user, atLeastOnce()).sendRawMessage(anyString());
    }
}
