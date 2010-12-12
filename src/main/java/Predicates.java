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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Adapted from com.google.common.base.Predicates.
 *
 * Static utility methods pertaining to {@code Predicate} instances.
 *
 * <p>All methods returns serializable predicates as long as they're given
 * serializable parameters.
 *
 * @author Kevin Bourrillion
 * @since 2 (imported from Google Collections Library)
 */
public class Predicates {

    private Predicates() {
    }

    public static <T> Predicate<T> alwaysTrue() {
        return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }

    public static <T> Predicate<T> alwaysFalse() {
        return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
    }

    public static <T> Predicate<T> isNull() {
        return ObjectPredicate.IS_NULL.withNarrowedType();
    }

    public static <T> Predicate<T> notNull() {
        return ObjectPredicate.NOT_NULL.withNarrowedType();
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return new NotPredicate<T>(predicate);
    }

    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components) {
        return new AndPredicate<T>(defensiveCopy(components));
    }

    public static <T> Predicate<T> and(Predicate<? super T>... components) {
        return new AndPredicate<T>(defensiveCopy(components));
    }

    public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<? super T> second) {
        return new AndPredicate<T>(Predicates.<T>asList(first, second));
    }

    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components) {
        return new OrPredicate<T>(defensiveCopy(components));
    }

    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        return new OrPredicate<T>(defensiveCopy(components));
    }

    public static <T> Predicate<T> or(Predicate<? super T> first, Predicate<? super T> second) {
        return new OrPredicate<T>(Predicates.<T>asList(first, second));
    }

    public static <T> Predicate<Iterable<T>> all(Predicate<? super T> predicate) {
        return new AllPredicate<T>(predicate);
    }

    public static <T> Predicate<Iterable<T>> any(Predicate<? super T> predicate) {
        return new AnyPredicate<T>(predicate);
    }

    public static <T> Predicate<T> equalTo(T target) {
        return (target == null)
                ? Predicates.<T>isNull()
                : new IsEqualToPredicate<T>(target);
    }

    public static Predicate<Object> instanceOf(Class<?> clazz) {
        return new InstanceOfPredicate(clazz);
    }

    public static <T> Predicate<T> in(Collection<? extends T> target) {
        return new InPredicate<T>(target);
    }

    public static Predicate<CharSequence> containsPattern(String pattern) {
        return new ContainsPatternPredicate(pattern);
    }

    public static Predicate<CharSequence> contains(Pattern pattern) {
        return new ContainsPatternPredicate(pattern);
    }

    // End public API, begin private implementation classes.
    private enum ObjectPredicate implements Predicate<Object> {
        ALWAYS_TRUE {
            public boolean apply(Object o) {
                return true;
            }
        },
        ALWAYS_FALSE {
            public boolean apply(Object o) {
                return false;
            }
        },
        IS_NULL {
            public boolean apply(Object o) {
                return o == null;
            }
        },
        NOT_NULL {
            public boolean apply(Object o) {
                return o != null;
            }
        };

        @SuppressWarnings("unchecked")
        // these Object predicates work for any T
        <T> Predicate<T> withNarrowedType() {
            return (Predicate<T>) this;
        }
    }

    private static class NotPredicate<T> implements Predicate<T> {
        private final Predicate<T> predicate;

        NotPredicate(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        public boolean apply(T t) {
            return !predicate.apply(t);
        }

        @Override
        public int hashCode() {
            return ~predicate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof NotPredicate) {
                NotPredicate<?> that = (NotPredicate<?>) obj;
                return predicate.equals(that.predicate);
            }
            return false;
        }
    }

    private static class AllPredicate<T> implements Predicate<Iterable<T>> {
        private final  Predicate<? super T> predicate;

        AllPredicate(final Predicate<? super T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(final Iterable<T> iterable) {
            for (final T obj : iterable) {
                if (!predicate.apply(obj)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj != null && obj instanceof AllPredicate) {
                AllPredicate that = (AllPredicate) obj;
                return predicate.equals(that.predicate);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return predicate.hashCode();
        }

    }

    private static class AnyPredicate<T> implements Predicate<Iterable<T>> {
        private final  Predicate<? super T> predicate;

        AnyPredicate(final Predicate<? super T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(final Iterable<T> iterable) {
            for (final T obj : iterable) {
                if (predicate.apply(obj)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj != null && obj instanceof AnyPredicate) {
                AnyPredicate that = (AnyPredicate) obj;
                return predicate.equals(that.predicate);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return predicate.hashCode();
        }

    }

    private static class AndPredicate<T> implements Predicate<T> {
        private final Iterable<? extends Predicate<? super T>> components;

        private AndPredicate(Iterable<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        public boolean apply(T t) {
            for (Predicate<? super T> predicate : components) {
                if (!predicate.apply(t)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = -1; /* Start with all bits on. */
            for (Predicate<? super T> predicate : components) {
                result &= predicate.hashCode();
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof AndPredicate) {
                AndPredicate<?> that = (AndPredicate<?>) obj;
                return iterableElementsEqual(components, that.components);
            }
            return false;
        }
    }

    private static class OrPredicate<T> implements Predicate<T> {
        private final Iterable<? extends Predicate<? super T>> components;

        private OrPredicate(Iterable<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        public boolean apply(T t) {
            for (Predicate<? super T> predicate : components) {
                if (predicate.apply(t)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 0; /* Start with all bits off. */
            for (Predicate<? super T> predicate : components) {
                result |= predicate.hashCode();
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof OrPredicate) {
                OrPredicate<?> that = (OrPredicate<?>) obj;
                return iterableElementsEqual(components, that.components);
            }
            return false;
        }
    }

    private static class IsEqualToPredicate<T> implements Predicate<T> {
        private final T target;

        private IsEqualToPredicate(T target) {
            this.target = target;
        }

        public boolean apply(T t) {
            return target.equals(t);
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof IsEqualToPredicate) {
                IsEqualToPredicate<?> that = (IsEqualToPredicate<?>) obj;
                return target.equals(that.target);
            }
            return false;
        }
    }

    private static class InstanceOfPredicate implements Predicate<Object> {
        private final Class<?> clazz;

        private InstanceOfPredicate(final Class<?> clazz) {
            this.clazz = clazz;
        }

        public boolean apply(Object o) {
            return clazz.isInstance(o);
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof InstanceOfPredicate) {
                InstanceOfPredicate that = (InstanceOfPredicate) obj;
                return clazz == that.clazz;
            }
            return false;
        }
    }

    private static class InPredicate<T> implements Predicate<T> {
        private final Collection<?> target;

        private InPredicate(Collection<?> target) {
            this.target = target;
        }

        public boolean apply(T t) {
            try {
                return target.contains(t);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof InPredicate) {
                InPredicate<?> that = (InPredicate<?>) obj;
                return target.equals(that.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }
    }

    private static class ContainsPatternPredicate implements Predicate<CharSequence> {
        final Pattern pattern;

        ContainsPatternPredicate(Pattern pattern) {
            this.pattern = pattern;
        }

        ContainsPatternPredicate(String patternStr) {
            this(Pattern.compile(patternStr));
        }

        public boolean apply(CharSequence t) {
            return pattern.matcher(t).find();
        }

        @Override
        public int hashCode() {
            // Pattern uses Object.hashCode, so we have to reach
            // inside to build a hashCode consistent with equals.

            return pattern.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof ContainsPatternPredicate) {
                ContainsPatternPredicate that = (ContainsPatternPredicate) obj;

                // Pattern uses Object (identity) equality, so we have to reach
                // inside to compare individual fields.
                return pattern.pattern().equals(that.pattern.pattern())
                        && pattern.flags() == that.pattern.flags();
            }
            return false;
        }
    }

    private static boolean iterableElementsEqual(Iterable<?> iterable1, Iterable<?> iterable2) {
        final Iterator<?> iterator1 = iterable1.iterator();
        final Iterator<?> iterator2 = iterable2.iterator();
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            if (!iterator1.next().equals(iterator2.next())) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Predicate<? super T>> asList(Predicate<? super T> first, Predicate<? super T> second) {
        return Arrays.<Predicate<? super T>>asList(first, second);
    }

    private static <T> List<T> defensiveCopy(T... array) {
        return defensiveCopy(Arrays.asList(array));
    }

    private static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        final ArrayList<T> list = new ArrayList<T>();
        for (T element : iterable) {
            list.add(element);
        }
        return list;
    }
}
