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

import java.util.Arrays;
import java.util.Map;

/**
 * Adapted from com.google.common.base.Functions.
 *
 * Static utility methods pertaining to {@code Function} instances.
 *
 * <p>All methods returns serializable functions as long as they're given serializable parameters.
 *
 * @author Mike Bostock
 * @author Jared Levy
 * @since 2 (imported from Google Collections Library)
 */
public final class Functions {

    private Functions() {
    }

    public static Function<Object, String> toStringFunction() {
        return ToStringFunction.INSTANCE;
    }

    // enum singleton pattern
    private enum ToStringFunction implements Function<Object, String> {
        INSTANCE;

        public String apply(final Object o) {
            return o.toString();
        }

        @Override
        public String toString() {
            return "toString";
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> Function<E,E> identity() {
        return Function.class.cast(IdentityFunction.INSTANCE);
    }

    // enum singleton pattern
    private enum IdentityFunction implements Function<Object, Object> {
        INSTANCE;

        public Object apply(final Object o) {
            return o;
        }

        @Override
        public String toString() {
            return "identity";
        }
    }

    public static <K, V> Function<K, V> forMap(Map<K, V> map) {
        return new FunctionForMapNoDefault<K, V>(map);
    }

    private static class FunctionForMapNoDefault<K, V> implements Function<K, V> {
        final Map<K, V> map;

        FunctionForMapNoDefault(Map<K, V> map) {
            this.map = map;
        }

        public V apply(K key) {
            V result = map.get(key);
            if (result != null || map.containsKey(key)) {
                return result;
            }
            throw new IllegalArgumentException("Key '" + key + "' not present in map");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final FunctionForMapNoDefault that = (FunctionForMapNoDefault) o;
            return !(map != null ? !map.equals(that.map) : that.map != null);
        }

        @Override
        public int hashCode() {
            return map != null ? map.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "forMap(" + map + ")";
        }

    }

    public static <K, V> Function<K, V> forMap(Map<K, ? extends V> map, V defaultValue) {
        return new ForMapWithDefault<K, V>(map, defaultValue);
    }

    private static class ForMapWithDefault<K, V> implements Function<K, V> {
        final Map<K, ? extends V> map;
        final V defaultValue;

        ForMapWithDefault(Map<K, ? extends V> map, V defaultValue) {
            this.map = map;
            this.defaultValue = defaultValue;
        }

        public V apply(K key) {
            V result = map.get(key);
            return (result != null || map.containsKey(key)) ? result : defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ForMapWithDefault that = (ForMapWithDefault) o;
            return Arrays.asList(map, defaultValue).equals(Arrays.asList(that.map, that.defaultValue));
        }

        @Override
        public int hashCode() {
            int result = map != null ? map.hashCode() : 0;
            result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "forMap(" + map + ", defaultValue=" + defaultValue + ")";
        }

    }

    public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f) {
        return new FunctionComposition<A, B, C>(g, f);
    }

    private static class FunctionComposition<A, B, C> implements Function<A, C> {
        private final Function<B, C> g;
        private final Function<A, ? extends B> f;

        public FunctionComposition(Function<B, C> g, Function<A, ? extends B> f) {
            this.g = g;
            this.f = f;
        }

        public C apply(A a) {
            return g.apply(f.apply(a));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FunctionComposition) {
                FunctionComposition<?, ?, ?> that = (FunctionComposition<?, ?, ?>) obj;
                return f.equals(that.f) && g.equals(that.g);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return f.hashCode() ^ g.hashCode();
        }

        @Override
        public String toString() {
            return g.toString() + "(" + f.toString() + ")";
        }

    }

    public static <T> Function<T, Boolean> forPredicate(Predicate<T> predicate) {
        return new PredicateFunction<T>(predicate);
    }

    private static class PredicateFunction<T> implements Function<T, Boolean> {

        private final Predicate<T> predicate;

        private PredicateFunction(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        public Boolean apply(T t) {
            return predicate.apply(t);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final PredicateFunction that = (PredicateFunction) o;
            return !(predicate != null ? !predicate.equals(that.predicate) : that.predicate != null);
        }

        @Override
        public int hashCode() {
            return predicate != null ? predicate.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "forPredicate(" + predicate + ")";
        }

    }

    public static <E> Function<Object, E> constant(final E value) {
        return new ConstantFunction<E>(value);
    }

    private static class ConstantFunction<E> implements Function<Object, E> {

        private final E value;

        public ConstantFunction(final E value) {
            this.value = value;
        }

        public E apply(final Object from) {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ConstantFunction that = (ConstantFunction) o;
            return !(value != null ? !value.equals(that.value) : that.value != null);
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "constant(" + value + ")";
        }

    }
}
