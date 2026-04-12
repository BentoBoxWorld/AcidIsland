package world.bentobox.acidisland.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bukkit.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ItemDestroyByAcidEventTest {

    @Mock
    private Item item;
    private ItemDestroyByAcidEvent event;

    @BeforeEach
    public void setUp() {
        event = new ItemDestroyByAcidEvent(item);
    }

    @Test
    public void testConstructor() {
        assertNotNull(event);
    }

    @Test
    public void testGetItem() {
        assertEquals(item, event.getItem());
    }

    @Test
    public void testGetHandlers() {
        assertNotNull(event.getHandlers());
    }

    @Test
    public void testGetHandlerList() {
        assertNotNull(ItemDestroyByAcidEvent.getHandlerList());
    }
}
