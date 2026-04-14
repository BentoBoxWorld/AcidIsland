package world.bentobox.acidisland.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import world.bentobox.acidisland.events.EntityDamageByAcidEvent.Acid;

@ExtendWith(MockitoExtension.class)
public class EntityDamageByAcidEventTest {

    @Mock
    private Entity entity;
    private EntityDamageByAcidEvent waterEvent;
    private EntityDamageByAcidEvent rainEvent;

    @BeforeEach
    void setUp() {
        waterEvent = new EntityDamageByAcidEvent(entity, 10.0, Acid.WATER);
        rainEvent = new EntityDamageByAcidEvent(entity, 5.0, Acid.RAIN);
    }

    @Test
    void testConstructor() {
        assertNotNull(waterEvent);
        assertNotNull(rainEvent);
    }

    @Test
    void testGetEntity() {
        assertEquals(entity, waterEvent.getEntity());
    }

    @Test
    void testGetDamage() {
        assertEquals(10.0, waterEvent.getDamage(), 0D);
        assertEquals(5.0, rainEvent.getDamage(), 0D);
    }

    @Test
    void testSetDamage() {
        waterEvent.setDamage(25.0);
        assertEquals(25.0, waterEvent.getDamage(), 0D);
    }

    @Test
    void testGetCauseWater() {
        assertEquals(Acid.WATER, waterEvent.getCause());
    }

    @Test
    void testGetCauseRain() {
        assertEquals(Acid.RAIN, rainEvent.getCause());
    }

    @Test
    void testIsCancelled() {
        assertFalse(waterEvent.isCancelled());
    }

    @Test
    void testSetCancelled() {
        waterEvent.setCancelled(true);
        assertTrue(waterEvent.isCancelled());
        waterEvent.setCancelled(false);
        assertFalse(waterEvent.isCancelled());
    }

    @Test
    void testGetHandlers() {
        assertNotNull(waterEvent.getHandlers());
    }

    @Test
    void testGetHandlerList() {
        assertNotNull(EntityDamageByAcidEvent.getHandlerList());
    }
}
