import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractGameTest {

    @Test
    public void run_oneTurn() {
        final MockBot bot = new MockBot();
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream("test line\ngo\n".getBytes());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final MockGame game = new MockGame(bot, universe, in, new PrintStream(out));
        game.run();
        assertTrue(bot.doTurnCalled);
        assertTrue(game.turnStartCalled);
        assertTrue(game.turnDoneCalled);
        assertEquals(Arrays.asList("test line"), universe.getLines());
        assertArrayEquals(new byte[] {}, out.toByteArray());
    }

    @Test
    public void run_multipleTurns() {
        final MockBot bot = new MockBot();
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream("test line 1\ngo\ntest line 2\ngo\n".getBytes());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final MockGame game = new MockGame(bot, universe, in, new PrintStream(out));
        game.run();
        assertTrue(bot.doTurnCalled);
        assertTrue(game.turnStartCalled);
        assertTrue(game.turnDoneCalled);
        assertEquals(Arrays.asList("test line 1", "test line 2"), universe.getLines());
        assertArrayEquals(new byte[] {}, out.toByteArray());
    }

    @Test
    public void run_incompleteTurn() {
        final MockBot bot = new MockBot();
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream("test line\n".getBytes());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final MockGame game = new MockGame(bot, universe, in, new PrintStream(out));
        game.run();
        assertFalse(bot.doTurnCalled);
        assertFalse(game.turnStartCalled);
        assertFalse(game.turnDoneCalled);
        assertEquals(Arrays.asList("test line"), universe.getLines());
        assertArrayEquals(new byte[] {}, out.toByteArray());
    }

    @Test
    public void run_incompleteLine() {
        final MockBot bot = new MockBot();
        final UniverseSpy universe = new UniverseSpy();
        final ByteArrayInputStream in = new ByteArrayInputStream("test line".getBytes());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final MockGame game = new MockGame(bot, universe, in, new PrintStream(out));
        game.run();
        assertFalse(bot.doTurnCalled);
        assertFalse(game.turnStartCalled);
        assertFalse(game.turnDoneCalled);
        assertEquals(Collections.<String>emptyList(), universe.getLines());
        assertArrayEquals(new byte[] {}, out.toByteArray());
    }

    static class MockBot implements Bot {

        private boolean doTurnCalled;

        @Override
        public void doTurn(final Universe universe, final Context context) {
            doTurnCalled = true;
        }
    }

    static class MockGame extends AbstractGame {

        private boolean turnStartCalled;
        private boolean turnDoneCalled;

        public MockGame(final Bot bot, final Universe universe, final InputStream in, final PrintStream out) {
            super(bot, universe, in, out);
        }

        @Override
        public void turnDone(final Context context) {
            turnDoneCalled = true;
        }

        @Override
        public void turnStart(final Context context) {
            turnStartCalled = true;
        }
    }
}
