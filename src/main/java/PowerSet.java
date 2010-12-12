/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Adapted from com.google.common.collect.Sets.PowerSet.
 *
 * Represents the set of all possible subsets of {@code iterable}. For example,
 * {@code PowerSet(Arrays.asList(1, 2))} returns the set {@code {{},
 * {1}, {2}, {1, 2}}}.
 *
 * <p>Elements appear in these subsets in the same iteration order as they
 * appeared in the input set. The order in which these subsets appear in the
 * outer set is undefined. Note that the power set of the empty set is not the
 * empty set, but a one-element set containing the empty set.
 *
 * <p>The returned set and its constituent sets use {@code equals} to decide
 * whether two elements are identical, even if the input set uses a different
 * concept of equivalence.
 *
 * <p><i>Performance notes:</i> while the power set of a set with size {@code
 * n} is of size {@code 2^n}, its memory usage is only {@code O(n)}. When the
 * power set is constructed, the input set is merely copied. Only as the
 * power set is iterated are the individual subsets created, and these subsets
 * themselves occupy only a few bytes of memory regardless of their size.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 * @author Chris Povirk
 * @see <a href="http://en.wikipedia.org/wiki/Power_set">Power set article at
 *      Wikipedia</a>
 * @since 4
 */
public class PowerSet<E> extends AbstractSet<Set<E>> {

    private final Set<E> set;
    private final List<E> items;
    private final int size;

    public PowerSet(final Iterable<E> iterable) {
        super();
        set = new HashSet<E>();
        items = new ArrayList<E>();
        for (final E e : iterable) {
            if (set.add(e)) {
                items.add(e);
            }
        }
        size = 1 << items.size();
    }

    @Override
    public boolean contains(final Object obj) {
        if (Set.class.isAssignableFrom(obj.getClass())) {
            final Set<?> set = (Set<?>) obj;
            return this.set.containsAll(set);
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object obj) {
        if (PowerSet.class.isAssignableFrom(obj.getClass())) {
            final PowerSet<?> that = (PowerSet<?>) obj;
            return set.equals(set);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
      return set.hashCode() << (set.size() - 1);
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<Set<E>> iterator() {
        return new AbstractIndexedListIterator<Set<E>>(size) {
            @Override
            protected Set<E> get(final int setBits) {
                return new AbstractSet<E>() {
                    @Override
                    public int size() {
                        return Integer.bitCount(setBits);
                    }

                    @Override
                    public Iterator<E> iterator() {
                        return new BitFilteredSetIterator<E>(items, setBits);
                    }
                };
            }
        };
    }

    @Override
    public boolean remove(final Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return size;
    }

    private static final class BitFilteredSetIterator<E> extends UnmodifiableIterator<E> {
        
        final List<E> input;
        int remainingSetBits;

        BitFilteredSetIterator(final List<E> input, final int allSetBits) {
            this.input = input;
            this.remainingSetBits = allSetBits;
        }

        public boolean hasNext() {
            return remainingSetBits != 0;
        }

        public E next() {
            final int index = Integer.numberOfTrailingZeros(remainingSetBits);
            if (index == 32) {
                throw new NoSuchElementException();
            }
            final int currentElementMask = 1 << index;
            remainingSetBits &= ~currentElementMask;
            return input.get(index);
        }
    }
}
