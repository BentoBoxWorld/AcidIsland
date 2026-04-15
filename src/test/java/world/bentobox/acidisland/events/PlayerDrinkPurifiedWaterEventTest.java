package world.bentobox.acidisland.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import world.bentobox.bentobox.database.objects.Island;

/**
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
class PlayerDrinkPurifiedWaterEventTest {

    @Test
    void testGetPlayer() {
        Player player = mock(Player.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(null, player, 4.0);
        assertEquals(player, event.getPlayer());
    }

    @Test
    void testGetHealAmount() {
        Player player = mock(Player.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(null, player, 4.0);
        assertEquals(4.0, event.getHealAmount());
    }

    @Test
    void testSetHealAmount() {
        Player player = mock(Player.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(null, player, 4.0);
        event.setHealAmount(8.0);
        assertEquals(8.0, event.getHealAmount());
    }

    @Test
    void testNotCancelledByDefault() {
        Player player = mock(Player.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(null, player, 4.0);
        assertFalse(event.isCancelled());
    }

    @Test
    void testCancelEvent() {
        Player player = mock(Player.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(null, player, 4.0);
        event.setCancelled(true);
        assertTrue(event.isCancelled());
    }

    @Test
    void testUncancelEvent() {
        Player player = mock(Player.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(null, player, 4.0);
        event.setCancelled(true);
        event.setCancelled(false);
        assertFalse(event.isCancelled());
    }

    @Test
    void testWithIsland() {
        Player player = mock(Player.class);
        Island island = mock(Island.class);
        PlayerDrinkPurifiedWaterEvent event = new PlayerDrinkPurifiedWaterEvent(island, player, 4.0);
        assertEquals(player, event.getPlayer());
        assertEquals(4.0, event.getHealAmount());
        assertFalse(event.isCancelled());
    }

    @Test
    void testHandlerList() {
        assertFalse(PlayerDrinkPurifiedWaterEvent.getHandlerList() == null);
    }
}
