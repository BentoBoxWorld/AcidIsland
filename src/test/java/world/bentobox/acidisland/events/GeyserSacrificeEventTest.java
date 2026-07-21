package world.bentobox.acidisland.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
public class GeyserSacrificeEventTest {

    @Mock
    private Item item;
    @Mock
    private Location vent;
    private GeyserSacrificeEvent event;

    @BeforeEach
    void setUp() {
        event = new GeyserSacrificeEvent(item, vent, "gems");
    }

    @Test
    void testGetItem() {
        assertEquals(item, event.getItem());
    }

    @Test
    void testGetVent() {
        assertEquals(vent, event.getVent());
    }

    @Test
    void testGetChannel() {
        assertEquals("gems", event.getChannel());
        assertNull(new GeyserSacrificeEvent(item, vent, null).getChannel());
    }

    @Test
    void testCancellation() {
        assertFalse(event.isCancelled());
        event.setCancelled(true);
        assertTrue(event.isCancelled());
    }

    @Test
    void testGetHandlers() {
        assertNotNull(event.getHandlers());
    }

    @Test
    void testGetHandlerList() {
        assertNotNull(GeyserSacrificeEvent.getHandlerList());
    }
}
