package newsbot.engine;

public class Session {
    private boolean started;

    public boolean isStarted() { return started; }
    public void markStarted() { started = true; }
    public void unmarkStarted() { started = false; }
}
