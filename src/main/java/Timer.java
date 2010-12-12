public class Timer {

    private long lastTime = System.currentTimeMillis();

    public void reset() {
        lastTime = System.currentTimeMillis();
    }

    public long elapsed() {
        return System.currentTimeMillis() - lastTime;
    }

}
