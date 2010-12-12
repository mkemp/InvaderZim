import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GameTest {

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
    public void turnStart_oneTurn() {
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {});
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Game game = new Game(null, universe, in, new PrintStream(out));
        assertEquals(0, game.getTurn());
        assertEquals(0, universe.getTurn());
        game.turnStart(null);
        assertEquals(1, game.getTurn());
        assertEquals(1, universe.getTurn());
        assertEquals(Collections.<String>emptyList(), universe.getLines());
        assertArrayEquals(new byte[] {}, out.toByteArray());
    }

    @Test
    public void turnStart_multipleTurns() {
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {});
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Game game = new Game(null, universe, in, new PrintStream(out));
        assertEquals(0, game.getTurn());
        assertEquals(0, universe.getTurn());
        game.turnStart(null);
        game.turnStart(null);
        assertEquals(2, game.getTurn());
        assertEquals(2, universe.getTurn());
        assertEquals(Collections.<String>emptyList(), universe.getLines());
        assertArrayEquals(new byte[] {}, out.toByteArray());
    }

    @Test
    public void turnDone_noOrders() {
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {});
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Game game = new Game(null, universe, in, new PrintStream(out));
        final Context context = new Context();
        game.turnDone(context);
        assertEquals(Collections.<String>emptyList(), universe.getLines());
        assertArrayEquals("go\n".getBytes(), out.toByteArray());
    }

    @Test
    public void turnDone_oneOrder() {
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {});
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Game game = new Game(null, universe, in, new PrintStream(out));
        final Context context = new Context();
        context.getOrders().add(Planet.get(0), Planet.get(2), 20);
        game.turnDone(context);
        assertEquals(Collections.<String>emptyList(), universe.getLines());
        assertArrayEquals("0 2 20\ngo\n".getBytes(), out.toByteArray());
    }

    @Test
    public void turnDone_multipleOrders() {
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {});
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Game game = new Game(null, universe, in, new PrintStream(out));
        final Context context = new Context();
        context.getOrders().add(Planet.get(0), Planet.get(2), 20);
        context.getOrders().add(Planet.get(1), Planet.get(2), 30);
        game.turnDone(context);
        assertEquals(Collections.<String>emptyList(), universe.getLines());
        assertArrayEquals("0 2 20\n1 2 30\ngo\n".getBytes(), out.toByteArray());
    }

    @After
    public void tearDown() {
        Planet.reset();
    }

}
