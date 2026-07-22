package world.bentobox.acidisland.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

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
class GeyserTransmuteEventTest {

    @Mock
    private Item item;
    @Mock
    private Location vent;
    private GeyserTransmuteEvent event;

    @BeforeEach
    void setUp() {
        event = new GeyserTransmuteEvent(vent, List.of(item), 5);
    }

    @Test
    void testGetVent() {
        assertEquals(vent, event.getVent());
    }

    @Test
    void testGetRewards() {
        assertEquals(List.of(item), event.getRewards());
    }

    @Test
    void testGetOfferings() {
        assertEquals(5, event.getOfferings());
    }

    @Test
    void testGetHandlers() {
        assertNotNull(event.getHandlers());
    }

    @Test
    void testGetHandlerList() {
        assertNotNull(GeyserTransmuteEvent.getHandlerList());
    }
}
