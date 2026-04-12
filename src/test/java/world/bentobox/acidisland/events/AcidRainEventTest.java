package world.bentobox.acidisland.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AcidRainEventTest {

    @Mock
    private Player player;
    private AcidRainEvent e;

    @BeforeEach
    public void setUp() {
        List<PotionEffectType> effects = List.of();
        e = new AcidRainEvent(player, 10, 5, effects);
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
    public void testGetPotionEffects() {
        assertEquals(0, e.getPotionEffects().toArray().length);
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
