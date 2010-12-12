import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ListenerSupportTest {

    @Test
    public void add_iterate_remove() {
        final Set<Object> myListeners = new HashSet<Object>();
        myListeners.add(new Object());
        myListeners.add(new Object());
        myListeners.add(new Object());
        final ListenerSupport<Object> myListenerSupport = new ListenerSupport<Object>();
        for (final Object myListener : myListeners) {
            myListenerSupport.add(myListener);
        }
        final Set<Object> myCopy = new HashSet<Object>(myListeners);
        for (final Object myObj : myListenerSupport) {
            assertTrue(myCopy.remove(myObj));
        }
        for (final Object myListener : myListeners) {
            myListenerSupport.remove(myListener);
        }
        for (final Object myObj : myListenerSupport) {
            fail("Found listener, when no listeners should be present!");
        }
    }

    @Test
    public void add_remove() {
        final Object myListener = new Object();
        final ListenerSupport<Object> myListenerSupport = new ListenerSupport<Object>();
        myListenerSupport.add(myListener);
        myListenerSupport.remove(myListener);
        for (final Object myObj : myListenerSupport) {
            fail("Found listener, when no listeners should be present!");
        }
    }

    @Test
    public void iterate_empty() {
        final ListenerSupport<Object> myListenerSupport = new ListenerSupport<Object>();
        for (final Object myObj : myListenerSupport) {
            fail("Found listener, when no listeners should be present!");
        }
    }
}
