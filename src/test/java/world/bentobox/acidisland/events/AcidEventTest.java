package world.bentobox.acidisland.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class AcidEventTest {

    @Mock
    private Player player;
    private AcidEvent e;

    @Before
    public void setUp() {
        List<PotionEffectType> effects = Arrays.asList(PotionEffectType.values());
        e = new AcidEvent(player, 10, 5, effects);
    }

    @Test
    public void testAcidEvent() {
        assertNotNull(e);
    }

    @Test
    public void testGetPlayer() {
        assertEquals(player, e.getPlayer());
    }

    @Test
    public void testSetPlayer() {
        Player player2 = mock(Player.class);
        e.setPlayer(player2);
        assertEquals(player2, e.getPlayer());
    }

    @Test
    public void testGetTotalDamage() {
        assertEquals(10D, e.getTotalDamage(), 0D);
    }

    @Test
    public void testGetProtection() {
        assertEquals(5D, e.getProtection(), 0D);
    }

    @Test
    public void testSetTotalDamage() {
        e.setTotalDamage(50);
        assertEquals(50D, e.getTotalDamage(), 0D);
    }

    @Test
    public void testGetPotionEffects() {
        Assert.assertArrayEquals(PotionEffectType.values(), e.getPotionEffects().toArray());
    }

    @Test
    public void testSetPotionEffects() {
        e.setPotionEffects(new ArrayList<>());
        assertTrue(e.getPotionEffects().isEmpty());
    }

    @Test
    public void testIsCancelled() {
        assertFalse(e.isCancelled());
    }

    @Test
    public void testSetCancelled() {
        e.setCancelled(true);
        assertTrue(e.isCancelled());
        e.setCancelled(false);
        assertFalse(e.isCancelled());
    }

}
