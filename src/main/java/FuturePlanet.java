public class FuturePlanet {

    private static final Logger log = Logger.getLogger(FuturePlanet.class);

    private final Universe universe;
    private final Planet planet;
    private final int turnDelta;
    private int turn;
    private Player owner;
    private int shipCount;
    private int requiredShipCount;

    public FuturePlanet(final Planet planet) {
        this(planet, null);
    }

    public FuturePlanet(final FuturePlanet previous) {
        this(previous.getPlanet(), previous);
    }

    private FuturePlanet(final Planet planet, final FuturePlanet previous) {
        super();
        this.universe = planet.getUniverse();
        this.planet = planet;
        if (previous != null) {
            this.turnDelta = previous.turnDelta + 1;
            this.turn = previous.turn + 1;
            this.owner = previous.owner;
            this.shipCount = previous.shipCount;
        } else {
            this.turnDelta = 0;
            this.turn = 0;
            this.owner = Player.NEUTRAL;
        }
    }

    public int getAvailableShipCount() {
        return shipCount - requiredShipCount;
    }

    public Player getOwner() {
        return owner;
    }

    public Planet getPlanet() {
        return planet;
    }

    public int getRequiredShipCount() {
        return requiredShipCount;
    }

    public int getShipCount() {
        return shipCount;
    }

    public int getTurn() {
        return turn;
    }

    public int getTurnDelta() {
        return turnDelta;
    }

    public void resolve(final FuturePlanet previous) {
        if (previous != null) {
            shipCount = previous.shipCount;
            owner = previous.owner;
            final Fleets fleets = universe.findFleets(
                planet,
                new Predicate<Fleet>() {
                    public boolean apply(final Fleet obj) {
                        return obj.getTurnsRemaining() == turnDelta;
                    }
                }
            );
            final int myShipCount = fleets.getMyShipCount();
            final int enemyShipCount = fleets.getEnemyShipCount();
            if (!Player.NEUTRAL.equals(owner) && turnDelta > 0) {
                shipCount += planet.getGrowthRate();
            }
            switch (owner) {
                case NEUTRAL:
                    if (Math.max(myShipCount, enemyShipCount) > shipCount) {
                        if (myShipCount != enemyShipCount) {
                            owner = myShipCount > enemyShipCount ? Player.ME : Player.ENEMY;
                        }
                        shipCount = Math.max(Math.abs(myShipCount - enemyShipCount) - shipCount, 0);
                    } else {
                        shipCount -= Math.max(myShipCount, enemyShipCount);
                    }
                    break;
                case ME:
                    if (enemyShipCount > shipCount + myShipCount) {
                        owner = Player.ENEMY;
                        shipCount = enemyShipCount - (shipCount + myShipCount);
                    } else {
                        shipCount += myShipCount - enemyShipCount;
                    }
                    break;
                default:
                    if (myShipCount > shipCount + enemyShipCount) {
                        owner = Player.ME;
                        shipCount = myShipCount - (shipCount + enemyShipCount);
                    } else {
                        shipCount += enemyShipCount - myShipCount;
                    }
                    break;
            }
        }
    }

    public void setOwner(final Player owner) {
        this.owner = owner;
    }

    public void setRequiredShipCount(final int requiredShipCount) {
        this.requiredShipCount = requiredShipCount;
    }

    public void setShipCount(final int shipCount) {
        this.shipCount = shipCount;
    }

    public void setTurn(final int turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return "<FP(" + owner.name().charAt(0) + ") " + planet.getId() + " (" + shipCount + "," + requiredShipCount + "," + getAvailableShipCount() + ") in " + turnDelta + ")";
    }

}
