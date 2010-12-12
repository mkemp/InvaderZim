import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MapperTest {

    private static final Function<Object,String> function = Functions.toStringFunction();
    
    @Test
    public void iterator() {
        final Mapper<Object,String> mapper = new Mapper<Object,String>(Collections.emptySet(), function);
        assertSame(mapper, mapper.iterator());
    }

    @Test
    public void hasNext_iterableWithResults() {
        final Mapper<Object,String> mapper = new Mapper<Object,String>(Collections.<Object>singleton(1), function);
        assertTrue(mapper.hasNext());
    }

    @Test
    public void hasNext_emptyIterable() {
        final Mapper<Object,String> mapper = new Mapper<Object,String>(Collections.emptySet(), function);
        assertFalse(mapper.hasNext());
    }

    @Test
    public void next_iterableWithResults() {
        final Mapper<Object,String> mapper = new Mapper<Object,String>(Collections.<Object>singleton(1), function);
        assertEquals("1", mapper.next());
        assertFalse(mapper.hasNext());
    }

    @Test
    public void next_emptyIterable() {
        final Mapper<Object,String> mapper = new Mapper<Object,String>(Collections.emptySet(), function);
        try {
            mapper.next();
            fail();
        } catch (NoSuchElementException e) {
            // no-op
        }
    }

    @Test
    public void mapper_list() {
        final List<String> result = Mapper.map(Arrays.<Object>asList(1, 2, 3), function);
        assertEquals(Arrays.asList("1", "2", "3"), result);
    }

    @Test
    public void mapper_set() {
        final Set<String> result = Mapper.map(new HashSet<Object>(Arrays.<Object>asList(1, 2, 3)), function);
        assertEquals(new HashSet<String>(Arrays.asList("1", "2", "3")), result);
    }

    @Test
    public void mapper_sortedSet() {
        final SortedSet<String> result = Mapper.map(new TreeSet<Object>(Arrays.<Object>asList(1, 2, 3)), function);
        assertEquals(new TreeSet<String>(Arrays.asList("1", "2", "3")), result);
    }
}
