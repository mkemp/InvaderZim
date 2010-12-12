import java.util.Arrays;
import java.util.Iterator;

public class Reduce<E> {

    private final Operation<E> operation;
    private final boolean useFirstValue;

    public Reduce(final Operation<E> operation) {
        this(operation, true);
    }

    public Reduce(final Operation<E> operation, final boolean useFirstValue) {
        super();
        this.operation = operation;
        this.useFirstValue = useFirstValue;
    }

    public E reduce(final E... items) {
        return reduce(Arrays.asList(items));
    }

    public E reduce(final Iterable<E> iterable) {
        return reduce(iterable.iterator());
    }

    private E reduce(final Iterator<E> iterator) {
        E accumulator = operation.initialValue();
        if (useFirstValue && iterator.hasNext()) {
            accumulator = iterator.next();
        }
        while (iterator.hasNext()) {
            accumulator = operation.evaluate(accumulator, iterator.next());
        }
        return accumulator;
    }


    public static interface Operation<E> {

        E evaluate(E accumulator, E operand);

        E initialValue();
    }

    public static enum BooleanOperation implements Operation<Boolean> {
        And() {
            @Override
            public Boolean evaluate(final Boolean accumulator, final Boolean operand) {
                return accumulator && operand;
            }

            @Override
            public Boolean initialValue() {
                return true;
            }
        },
        Or() {
            @Override
            public Boolean evaluate(final Boolean accumulator, final Boolean operand) {
                return accumulator || operand;
            }

            @Override
            public Boolean initialValue() {
                return false;
            }
        }
    }

    public static enum IntegerOperation implements Operation<Integer> {
        Add() {
            @Override
            public Integer evaluate(Integer accumulator, Integer operand) {
                return accumulator + operand;
            }
            @Override
            public Integer initialValue() {
                return 0;
            }
        },
        Divide() {
            @Override
            public Integer evaluate(Integer accumulator, Integer operand) {
                return accumulator / operand;
            }
            @Override
            public Integer initialValue() {
                return 1;
            }
        },
        Max() {
            @Override
            public Integer evaluate(final Integer accumulator, final Integer operand) {
                return Math.max(accumulator, operand);
            }

            @Override
            public Integer initialValue() {
                return Integer.MIN_VALUE;
            }
        },
        Min() {
            @Override
            public Integer evaluate(final Integer accumulator, final Integer operand) {
                return Math.min(accumulator, operand);
            }

            @Override
            public Integer initialValue() {
                return Integer.MAX_VALUE;
            }
        },
        Multiply() {
            @Override
            public Integer evaluate(Integer accumulator, Integer operand) {
                return accumulator * operand;
            }
            @Override
            public Integer initialValue() {
                return 1;
            }
        },
        Subtract() {
            @Override
            public Integer evaluate(Integer accumulator, Integer operand) {
                return accumulator - operand;
            }
            @Override
            public Integer initialValue() {
                return 0;
            }
        }
    }
}
