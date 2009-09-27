package yodafunkta;

import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

public abstract class Functor {

    private Object[] parameters;

    protected static List<Class<?>> list(Class<? extends Object> object, List<Class<?>> objects) {
        List<Class<?>> result = new LinkedList<Class<?>>();
        result.add(object);
        result.addAll(objects);
        return result;
    }

    protected static Object[] merge(Object[]... arrayOfObjects) {
        List<Object> result = new LinkedList<Object>();
        for (Object[] objects : arrayOfObjects) {
            result.addAll(asList(objects));
        }
        return result.toArray();
    }
    
    public static Functor f(String methodName, Object... parameters) {
        return functor(methodName, parameters);
    }

    public static Functor functor(String methodName, Object... parameters) {
        return new BaseFunctor(methodName, parameters);
    }

    protected Object[] getParameters() {
        return parameters;
    }

    protected void setParameters(Object... parameters) {
        this.parameters = parameters;
    }

    protected Object[] allParameters(Object... moreParameters) {
        return merge(getParameters(), moreParameters);
    }

    public Functor param(Object parameter) {
        return params(new Object[] { parameter });
    }

    public Functor params(Object... moreParameters) {
        return cloneItself().addParams(moreParameters);
    }

    private Functor addParams(Object[] moreParameters) {
        this.parameters = merge(this.parameters, moreParameters);
        return this;
    }

    public boolean evaluate(Object... parameters) {
        try {
            return (Boolean) run(parameters);
        } catch (ClassCastException e) {
            throw new RuntimeException("The return of the method should be a boolean", e);
        }
    }

    public <T, Z> List<T> map(Z... elements) {
        return map(asList(elements));
    }

    @SuppressWarnings("unchecked")
    public <T, Z> List<T> map(List<Z> list) {
        List<T> result = new LinkedList<T>();
        for (Z element : list) {
            result.add((T) run(element));
        }
        return result;
    }

    public <T> List<T> filter(T... elements) {
        return filter(asList(elements));
    }

    public <T> List<T> filter(List<T> list) {
        List<T> result = new LinkedList<T>();
        for (T element : list) {
            if ((Boolean) run(element)) result.add(element);
        }
        return result;
    }

    public Functor of(Functor other) {
        return new CombineFunctor(this, other);
    }

    public abstract <T> T run(Object... parameters);
    
    protected abstract Functor cloneItself();
}
