import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FuturePlanetTest {

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
        final FuturePlanet futurePlanet = Planet.get(0).now();
        assertEquals(Planet.get(0), futurePlanet.getPlanet());
        assertEquals(0, futurePlanet.getTurnDelta());
        assertEquals(0, futurePlanet.getTurn());
        assertEquals(Player.NEUTRAL, futurePlanet.getOwner());
        assertEquals(0, futurePlanet.getShipCount());
    }

    @Test
    public void turn() {
        final FuturePlanet futurePlanet = Planet.get(1).now();
        assertEquals(0, futurePlanet.getTurnDelta());
        assertEquals(0, futurePlanet.getTurn());
        futurePlanet.setTurn(3);
        assertEquals(0, futurePlanet.getTurnDelta());
        assertEquals(3, futurePlanet.getTurn());
        final FuturePlanet next = Planet.create(futurePlanet);
        assertEquals(1, next.getTurnDelta());
        assertEquals(4, next.getTurn());
    }

    @Test
    public void owner() {
        final FuturePlanet futurePlanet = Planet.get(1).now();
        futurePlanet.setOwner(Player.ENEMY);
        assertEquals(Player.ENEMY, futurePlanet.getOwner());
    }

    @Test
    public void shipCount() {
        final FuturePlanet futurePlanet = Planet.get(1).now();
        futurePlanet.setShipCount(50);
        futurePlanet.setRequiredShipCount(20);
        assertEquals(50, futurePlanet.getShipCount());
        assertEquals(20, futurePlanet.getRequiredShipCount());
        assertEquals(30, futurePlanet.getAvailableShipCount());
    }

    @Test
    public void resolve_now() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        final FuturePlanet futurePlanet = planet.now();
        futurePlanet.setShipCount(50);
        futurePlanet.resolve(null);
        assertEquals(Player.NEUTRAL, futurePlanet.getOwner());
        assertEquals(50, futurePlanet.getShipCount());
    }

    @Test
    public void resolve_neutralNoGrowth() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        planet.now().setShipCount(50);
        FuturePlanet futurePlanet = planet.now();
        for (int i = 0; i < 20; i++) {
            futurePlanet = Planet.create(futurePlanet);
            futurePlanet.resolve(planet.inFuture(i));
        }
        for (int i = 0; i <= 20; i++) {
            futurePlanet = planet.inFuture(i);
            assertEquals(Player.NEUTRAL, futurePlanet.getOwner());
            assertEquals(50, futurePlanet.getShipCount());
        }
    }

    @Test
    public void resolve_neutralWithoutTakeover() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ME, 30, null, planet, 5, 1));
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        planet.now().setShipCount(50);
        futurePlanet.resolve(planet.now());
        assertEquals(Player.NEUTRAL, futurePlanet.getOwner());
        assertEquals(20, futurePlanet.getShipCount());
    }

    @Test
    public void resolve_neutralWithTiedTakeover() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ME, 51, null, planet, 5, 1));
        universe.fleets.add(new Fleet(Player.ENEMY, 51, null, planet, 4, 1));
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        planet.now().setShipCount(50);
        futurePlanet.resolve(planet.now());
        assertEquals(Player.NEUTRAL, futurePlanet.getOwner());
        assertEquals(0, futurePlanet.getShipCount());
    }

    @Test
    public void resolve_neutralWithEnemyTakeover() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ENEMY, 51, null, planet, 4, 1));
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        planet.now().setShipCount(50);
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ENEMY, futurePlanet.getOwner());
        assertEquals(1, futurePlanet.getShipCount());
    }

    @Test
    public void resolve_neutralWithMyTakeover() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ME, 51, null, planet, 5, 1));
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        planet.now().setShipCount(50);
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ME, futurePlanet.getOwner());
        assertEquals(1, futurePlanet.getShipCount());
    }

    @Test
    public void resolve_neutralWithEnemySnipe() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ME, 51, null, planet, 5, 1));
        universe.fleets.add(new Fleet(Player.ENEMY, 7, null, planet, 4, 2));
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        planet.now().setShipCount(50);
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ME, futurePlanet.getOwner());
        assertEquals(1, futurePlanet.getShipCount());
        final FuturePlanet next = Planet.create(futurePlanet);
        next.resolve(futurePlanet);
        assertEquals(Player.ENEMY, next.getOwner());
        assertEquals(1, next.getShipCount());
    }

    @Test
    public void resolve_neutralWithMySnipe() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ENEMY, 51, null, planet, 4, 1));
        universe.fleets.add(new Fleet(Player.ME, 7, null, planet, 5, 2));
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        planet.now().setShipCount(50);
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ENEMY, futurePlanet.getOwner());
        assertEquals(1, futurePlanet.getShipCount());
        final FuturePlanet next = Planet.create(futurePlanet);
        next.resolve(futurePlanet);
        assertEquals(Player.ME, next.getOwner());
        assertEquals(1, next.getShipCount());
    }

    @Test
    public void resolve_enemyDefense() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ENEMY, 5, null, planet, 4, 2));
        universe.fleets.add(new Fleet(Player.ME, 45, null, planet, 5, 1));
        universe.fleets.add(new Fleet(Player.ME, 20, null, planet, 5, 2));
        planet.now().setOwner(Player.ENEMY);
        planet.now().setShipCount(50);
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ENEMY, futurePlanet.getOwner());
        assertEquals(10, futurePlanet.getShipCount());
        final FuturePlanet next = Planet.create(futurePlanet);
        next.resolve(futurePlanet);
        assertEquals(Player.ENEMY, next.getOwner());
        assertEquals(0, next.getShipCount());
    }

    @Test
    public void resolve_enemyDefenseWithTakeover() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ENEMY, 5, null, planet, 4, 2));
        universe.fleets.add(new Fleet(Player.ME, 45, null, planet, 5, 1));
        universe.fleets.add(new Fleet(Player.ME, 25, null, planet, 5, 2));
        planet.now().setOwner(Player.ENEMY);
        planet.now().setShipCount(50);
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ENEMY, futurePlanet.getOwner());
        assertEquals(10, futurePlanet.getShipCount());
        final FuturePlanet next = Planet.create(futurePlanet);
        next.resolve(futurePlanet);
        assertEquals(Player.ME, next.getOwner());
        assertEquals(5, next.getShipCount());
    }

    @Test
    public void resolve_enemyGrowth() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        planet.now().setOwner(Player.ENEMY);
        planet.now().setShipCount(50);
        FuturePlanet futurePlanet = planet.now();
        for (int i = 0; i < 20; i++) {
            futurePlanet = Planet.create(futurePlanet);
            futurePlanet.resolve(planet.inFuture(i));
        }
        for (int i = 0; i <= 20; i++) {
            futurePlanet = planet.inFuture(i);
            assertEquals(Player.ENEMY, futurePlanet.getOwner());
            assertEquals(50 + i * planet.getGrowthRate(), futurePlanet.getShipCount());
        }
    }

    @Test
    public void resolve_myDefense() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ME, 5, null, planet, 5, 2));
        universe.fleets.add(new Fleet(Player.ENEMY, 45, null, planet, 4, 1));
        universe.fleets.add(new Fleet(Player.ENEMY, 20, null, planet, 4, 2));
        planet.now().setOwner(Player.ME);
        planet.now().setShipCount(50);
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ME, futurePlanet.getOwner());
        assertEquals(10, futurePlanet.getShipCount());
        final FuturePlanet next = Planet.create(futurePlanet);
        next.resolve(futurePlanet);
        assertEquals(Player.ME, next.getOwner());
        assertEquals(0, next.getShipCount());
    }

    @Test
    public void resolve_myDefenseWithTakeover() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        universe.fleets.add(new Fleet(Player.ME, 5, null, planet, 5, 2));
        universe.fleets.add(new Fleet(Player.ENEMY, 45, null, planet, 4, 1));
        universe.fleets.add(new Fleet(Player.ENEMY, 25, null, planet, 4, 2));
        planet.now().setOwner(Player.ME);
        planet.now().setShipCount(50);
        final FuturePlanet futurePlanet = Planet.create(planet.now());
        futurePlanet.resolve(planet.now());
        assertEquals(Player.ME, futurePlanet.getOwner());
        assertEquals(10, futurePlanet.getShipCount());
        final FuturePlanet next = Planet.create(futurePlanet);
        next.resolve(futurePlanet);
        assertEquals(Player.ENEMY, next.getOwner());
        assertEquals(5, next.getShipCount());
    }

    @Test
    public void resolve_myGrowth() {
        final MockUniverse universe = new MockUniverse();
        final Planet planet = new Planet(universe, 0, 5, 7.0, 8.0);
        planet.now().setOwner(Player.ME);
        planet.now().setShipCount(50);
        FuturePlanet futurePlanet = planet.now();
        for (int i = 0; i < 20; i++) {
            futurePlanet = Planet.create(futurePlanet);
            futurePlanet.resolve(planet.inFuture(i));
        }
        for (int i = 0; i <= 20; i++) {
            futurePlanet = planet.inFuture(i);
            assertEquals(Player.ME, futurePlanet.getOwner());
            assertEquals(50 + i * planet.getGrowthRate(), futurePlanet.getShipCount());
        }
    }

    @Test
    public void string() {
        final FuturePlanet futurePlanet = Planet.get(2).now();
        futurePlanet.setShipCount(50);
        assertEquals("<FP(N) 2 (50,0,50) in 0)", futurePlanet.toString()); 
    }

    @After
    public void tearDown() {
        Planet.reset();
    }

    static class MockUniverse extends UniverseSpy {

        private final Fleets fleets;

        public MockUniverse() {
            super();
            this.fleets = new Fleets();
        }

        @Override
        public Fleets getFleets() {
            return fleets;
        }
    }
}
