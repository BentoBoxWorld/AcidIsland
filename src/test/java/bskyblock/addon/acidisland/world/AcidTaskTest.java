package bskyblock.addon.acidisland.world;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bskyblock.addon.acidisland.AISettings;
import bskyblock.addon.acidisland.AcidIsland;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
public class AcidTaskTest {

    private BukkitScheduler scheduler;
    private AISettings settings;
    private AcidIsland addon;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Bukkit.class);
        scheduler = mock(BukkitScheduler.class);
        when(Bukkit.getScheduler()).thenReturn(scheduler);
        addon = mock(AcidIsland.class);
        settings = mock(AISettings.class);
        when(settings.getAcidDestroyItemTime()).thenReturn(0L);
        when(addon.getSettings()).thenReturn(settings);
    }

    @Test
    public void testAcidTaskDoNotDestroyItems() {
        new AcidTask(addon);
        Mockito.verify(scheduler).scheduleSyncRepeatingTask(Mockito.any(), Mockito.any(Runnable.class), Mockito.eq(0L), Mockito.eq(20L));
    }

    @Test
    public void testAcidTaskDestroyItems() {
        when(settings.getAcidDestroyItemTime()).thenReturn(5L);
        new AcidTask(addon);
        Mockito.verify(scheduler).scheduleSyncRepeatingTask(Mockito.any(), Mockito.any(Runnable.class), Mockito.eq(0L), Mockito.eq(20L));
        Mockito.verify(scheduler).scheduleSyncRepeatingTask(Mockito.any(), Mockito.any(Runnable.class), Mockito.eq(100L), Mockito.eq(100L));
    }

    @Test
    public void testAcidTaskCancelTasks() {
        AcidTask task = new AcidTask(addon);
        task.cancelTasks();
        Mockito.verify(scheduler).cancelTask(Mockito.anyInt());
    }

    @Test
    public void testAcidTaskCancelBothTasks() {
        when(settings.getAcidDestroyItemTime()).thenReturn(5L);
        AcidTask task = new AcidTask(addon);
        task.cancelTasks();
        Mockito.verify(scheduler, Mockito.times(2)).cancelTask(Mockito.anyInt());
    }


}
