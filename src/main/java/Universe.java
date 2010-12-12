public interface Universe extends GameUpdateHandler {
    Fleets findFleets(Player owner);

    Fleets findFleets(Player owner, Planet destination);

    Fleets findFleets(Player owner, Planet destination, Predicate<Fleet> predicate);

    Fleets findFleets(Player owner, Predicate<Fleet> predicate);

    Fleets findFleets(Planet destination);

    Fleets findFleets(Planet destination, Predicate<Fleet> predicate);

    Fleets findFleets(Predicate<Fleet> predicate);

    Fleets findFleets(Player owner, Planet source, Planet destination, Predicate<Fleet> predicate);

    Planets findPlanets(Player owner);

    Planets findPlanets(Predicate<Planet> predicate);

    Planets findPlanets(Player owner, Integer growthRate, Predicate<Planet> predicate);

    Fleets getFleets();

    Fleets getMyFleets();

    Fleets getEnemyFleets();

    Planets getPlanets();

    Planets getMyPlanets();

    Planets getEnemyPlanets();

    Planets getNeutralPlanets();

    Planets getNotMyPlanets();

    int getTurn();

    FutureUniverse inFuture();

    FutureUniverse inFuture(int turns);

    FutureUniverse now();
}
