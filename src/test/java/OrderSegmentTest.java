import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OrderSegmentTest {

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
        final OrderSegment segment = new OrderSegment(Planet.get(0), 20, 3, 0);
        assertEquals(Planet.get(0), segment.getSource());
        assertEquals(20, segment.getShipCount());
        assertEquals(3, segment.getDistance());
        assertEquals(0, segment.getTurnDelta());
    }

    @Test
    public void equals_and_hashCode() {
        final OrderSegment segment = new OrderSegment(Planet.get(0), 20, 3, 0);
        final OrderSegment same = new OrderSegment(Planet.get(0), 20, 3, 0);
        final OrderSegment other = new OrderSegment(Planet.get(1), 30, 3, 1);
        assertEquals(same, segment);
        assertEquals(same.hashCode(), segment.hashCode());
        assertFalse(other.equals(segment));
        assertFalse(other.hashCode() == segment.hashCode());
    }

    @Test
    public void string() {
        final OrderSegment segment = new OrderSegment(Planet.get(0), 20, 3, 0);
        assertEquals("<S " + Planet.get(0) + " #20 in (0+3)>", segment.toString());
    }

    @After
    public void tearDown() {
        Planet.reset();
    }

}
