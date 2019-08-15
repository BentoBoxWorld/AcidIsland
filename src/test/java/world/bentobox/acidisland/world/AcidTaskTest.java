package world.bentobox.acidisland.world;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
public class AcidTaskTest {

    @Mock
    private BukkitScheduler scheduler;
    @Mock
    private AISettings settings;
    @Mock
    private AcidIsland addon;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getScheduler()).thenReturn(scheduler);
        when(settings.getAcidDestroyItemTime()).thenReturn(0L);
        when(addon.getSettings()).thenReturn(settings);
    }

    @Test
    public void testAcidTaskDoNotDestroyItems() {
        new AcidTask(addon);
        verify(scheduler).scheduleSyncRepeatingTask(any(), any(Runnable.class), eq(0L), eq(20L));
    }

    @Test
    public void testAcidTaskDestroyItems() {
        when(settings.getAcidDestroyItemTime()).thenReturn(5L);
        new AcidTask(addon);
        verify(scheduler).scheduleSyncRepeatingTask(any(), any(Runnable.class), eq(0L), eq(20L));
        verify(scheduler).scheduleSyncRepeatingTask(any(), any(Runnable.class), eq(100L), eq(100L));
    }

    @Test
    public void testAcidTaskCancelTasks() {
        AcidTask task = new AcidTask(addon);
        task.cancelTasks();
        verify(scheduler).cancelTask(anyInt());
    }

    @Test
    public void testAcidTaskCancelBothTasks() {
        when(settings.getAcidDestroyItemTime()).thenReturn(5L);
        AcidTask task = new AcidTask(addon);
        task.cancelTasks();
        verify(scheduler, times(2)).cancelTask(anyInt());
    }


}
