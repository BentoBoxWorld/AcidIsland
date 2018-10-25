package world.bentobox.acidisland.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

public class AcidRainEventTest {

    private Player player;
    private AcidRainEvent e;

    @Before
    public void setUp() throws Exception {
        player = mock(Player.class);
        e = new AcidRainEvent(player, 10, 5);
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
        assertTrue(e.getRainDamage() == 10D);
    }

    @Test
    public void testGetProtection() {
        assertTrue(e.getProtection() == 5D);
    }

    @Test
    public void testSetTotalDamage() {
        e.setRainDamage(50);
        assertTrue(e.getRainDamage() == 50D);
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
