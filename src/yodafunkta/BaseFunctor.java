package yodafunkta;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

class BaseFunctor extends Functor {

    private final Class<?> declaringClass;

    private final String methodName;

    private Method method;

    public BaseFunctor(String methodName, Object... parameters) {
        setParameters(parameters);
        this.methodName = methodName;
        this.declaringClass = findCallerClass();
    }

    private Class<?> declaringClass() {
        return declaringClass;
    }

    private List<Class<?>[]> allPossibleTypesFor(Object... parameters) {

        List<Class<?>[]> result = new LinkedList<Class<?>[]>();

        List<List<Class<?>>> alternatives = allPossibleTypesFor(new LinkedList<Object>(asList(parameters)));
        for (List<Class<?>> paramClasses : alternatives) {
            result.add(paramClasses.toArray(new Class<?>[] {}));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private List<List<Class<?>>> allPossibleTypesFor(Deque<Object> parameters) {

        if (parameters.isEmpty()) emptyList();

        Object first = parameters.pop();
        Deque<Object> remainingParameters = parameters;

        List<List<Class<?>>> result = new LinkedList<List<Class<?>>>();

        if (remainingParameters.isEmpty()) {
            result.addAll(allPossibleTypesFor(first, EMPTY_LIST));
        } else {
            for (List<Class<?>> paramClasses : allPossibleTypesFor(remainingParameters)) {
                result.addAll(allPossibleTypesFor(first, paramClasses));
            }
        }
        return result;
    }

    private List<List<Class<?>>> allPossibleTypesFor(Object first, List<Class<?>> paramClasses) {
        try {

            List<List<Class<?>>> result = new LinkedList<List<Class<?>>>();

            // Object type
            result.add(list(first.getClass(), paramClasses));

            // Primitive type
            try {
                Class<?> paramClass = (Class<?>) first.getClass().getDeclaredField("TYPE").get(null);
                result.add(list(paramClass, paramClasses));
            } catch (NoSuchFieldException e) {
                // ignore
            }

            return result;

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> findCallerClass() {
        try {
            throw new Exception("Dirty trick to capture the caller class");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement line : stackTrace) {
                if (line.getClassName().matches(getClass().getPackage().getName() + "\\.\\w*Functor")) continue;
                try {
                    return Class.forName(line.getClassName());
                } catch (ClassNotFoundException e1) {
                    throw new RuntimeException("No caller class found", e1);
                }
            }
        }
        throw new RuntimeException("No caller class found");
    }

    private Method findMethod(Object... parameters) throws ClassNotFoundException, NoSuchMethodException {

        Class<?> aClass = declaringClass();

        for (Class<?>[] parameterTypes : allPossibleTypesFor(parameters)) {
            try {
                Method method = aClass.getDeclaredMethod(methodName(), parameterTypes);
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("Functor method should be static");
                }
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                // Let's try other alternatives
                continue;
            }
        }
        throw new NoSuchMethodException("Method not found");
    }

    private Object executeMethodWith(Object... parameters) {
        try {
            return theMethod(parameters).invoke(null, parameters);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String methodName() {
        return methodName;
    }

    private Method theMethod(Object... parameters) throws ClassNotFoundException, NoSuchMethodException {
        if (method == null) {
            method = findMethod(parameters);
        }
        return method;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T run(Object... parameters) {
        return (T) executeMethodWith(allParameters(parameters));
    }
    
    protected Functor cloneItself() {
        BaseFunctor result = new BaseFunctor(methodName, getParameters());
        // No need to find the method again, if we already found it.
        result.method = method;
        return result;
    }
}
