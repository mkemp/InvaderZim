public class Fleet {

    private final Player owner;
    private final int shipCount;
    private final Planet source;
    private final Planet destination;
    private final int totalTurns;
    private int turnsRemaining;

    public Fleet(final String owner, final String shipCount, final String source, final String destination, final String totalTurns, final String turnsRemaining) {
        this(Integer.parseInt(owner), Integer.parseInt(shipCount), Integer.parseInt(source), Integer.parseInt(destination), Integer.parseInt(totalTurns), Integer.parseInt(turnsRemaining));
    }

    public Fleet(final int owner, final int shipCount, final int source, final int destination, final int totalTurns, final int turnsRemaining) {
        this(Player.get(owner), shipCount, Planet.get(source), Planet.get(destination), totalTurns, turnsRemaining);
    }

    public Fleet(final Player owner, final int shipCount, final Planet source, final Planet destination, final int totalTurns, final int turnsRemaining) {
        super();
        this.owner = owner;
        this.shipCount = shipCount;
        this.source = source;
        this.destination = destination;
        this.totalTurns = totalTurns;
        this.turnsRemaining = turnsRemaining;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Fleet fleet = (Fleet) o;

        if (shipCount != fleet.shipCount) return false;
        if (turnsRemaining != fleet.turnsRemaining) return false;
        if (destination != null ? !destination.equals(fleet.destination) : fleet.destination != null) return false;
        if (owner != null ? !owner.equals(fleet.owner) : fleet.owner != null) return false;
        if (source != null ? !source.equals(fleet.source) : fleet.source != null) return false;

        return true;
    }

    public Player getOwner() {
        return owner;
    }

    public int getShipCount() {
        return shipCount;
    }

    public Planet getSource() {
        return source;
    }

    public Planet getDestination() {
        return destination;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    @Override
    public int hashCode() {
        int result = owner != null ? owner.hashCode() : 0;
        result = 31 * result + shipCount;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + turnsRemaining;
        return result;
    }

    @Override
    public String toString() {
        return "<F(" + owner + ") #" + shipCount + " " + source + " -> " + destination + " in " + turnsRemaining + ">";
    }
}
