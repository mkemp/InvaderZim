import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FleetTest {

    @BeforeClass
    public static void classSetUp() {
        Planet.reset();
    }

    @Before
    public void setUp() {
        new Planet(null, 0, 5, 1.0, 7.0);
        new Planet(null, 1, 5, 4.0, 4.0);
        new Planet(null, 2, 5, 2.0, 5.0);
    }

    @Test
    public void attributes() {
        final Fleet fleet = new Fleet("1", "20", "0", "2", "3", "2");
        assertEquals(Player.ME, fleet.getOwner());
        assertEquals(20, fleet.getShipCount());
        assertEquals(Planet.get(0), fleet.getSource());
        assertEquals(Planet.get(2), fleet.getDestination());
        assertEquals(3, fleet.getTotalTurns());
        assertEquals(2, fleet.getTurnsRemaining());
    }

    @Test
    public void equals_and_hashCode() {
        final Planet source = Planet.get(0);
        final Planet destination = Planet.get(2);
        final int distance = source.distance(destination);
        final Fleet fleet = new Fleet(Player.ME, 20, source, destination, distance, distance - 1);
        final Fleet same = new Fleet(Player.ME, 20, source, destination, distance, distance - 1);
        final Fleet other = new Fleet(Player.ME, 30, source, destination, distance, distance - 3);
        assertEquals(same, fleet);
        assertEquals(same.hashCode(), fleet.hashCode());
        assertFalse(other.equals(fleet));
        assertFalse(other.hashCode() == fleet.hashCode());
    }

    @Test
    public void string() {
        final Planet source = Planet.get(0);
        final Planet destination = Planet.get(2);
        final int distance = source.distance(destination);
        final Fleet fleet = new Fleet(Player.ME, 20, source, destination, distance, distance - 1);
        assertEquals("<F(" + Player.ME + ") #20 " + source + " -> " + destination + " in 2>", fleet.toString());
    }

    @After
    public void tearDown() {
        Planet.reset();
    }

}
