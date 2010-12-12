import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CandidateTest {

    private static Set<Integer> TURNS = new HashSet<Integer>(Arrays.asList(1, 2, 3));

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
        final Candidate candidate = new Candidate(Planet.get(0), Planet.get(2), 3, TURNS);
        assertEquals(Planet.get(0), candidate.getSource());
        assertEquals(Planet.get(2), candidate.getDestination());
        assertEquals(3, candidate.getDistance());
        assertEquals(TURNS, candidate.getTurns());
    }

    @Test
    public void equals_and_hashCode() {
        final Candidate candidate = new Candidate(Planet.get(0), Planet.get(2), 3, TURNS);
        final Candidate same = new Candidate(Planet.get(0), Planet.get(2), 3, TURNS);
        final Candidate other = new Candidate(Planet.get(1), Planet.get(2), 3, TURNS);
        assertEquals(same, candidate);
        assertEquals(same.hashCode(), candidate.hashCode());
        assertFalse(other.equals(candidate));
        assertFalse(other.hashCode() == candidate.hashCode());
    }

    @Test
    public void string() {
        final Candidate candidate = new Candidate(Planet.get(0), Planet.get(2), 3, TURNS);
        assertEquals("<C(3) " + Planet.get(0) + " -> " + Planet.get(2) + " on [1, 2, 3]>", candidate.toString());
    }

    @After
    public void tearDown() {
        Planet.reset();
    }

}
