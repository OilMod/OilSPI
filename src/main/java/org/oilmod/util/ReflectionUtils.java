package org.oilmod.util;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class ReflectionUtils {
    private static final IntFunction<boolean[]> ALL = boolean[]::new;

    private static boolean[] createIgnore(int from, int to, int length) {
        boolean[] result = new boolean[length];
        for (int i = 0; i < from; i++) {
            result[i] =true;
        }
        for (int i = to; i < length; i++) {
            result[i] =true;
        }
        return result;
    }

    public static <T extends I, I> Class<?>[] resolveGenericSuperInterface(Class<T> clazz, Class<I> superInter) {
        return resolveGenericSuperInterface(clazz, superInter, ALL);
    }

    public static <T extends I, I> Class<?>[] resolveGenericSuperInterface(Class<T> clazz, Class<I> superInter , int from, int to) {

        return resolveGenericSuperInterface(clazz, superInter, (l)->createIgnore(from, to, l));
    }

    public static <T extends I, I> Class<?>[] resolveGenericSuperInterface(Class<T> clazz, Class<I> superInter, IntFunction<boolean[]> ignore) {
        checkTypeParams(clazz, superInter);

        Queue<ParameterizedType> trace = new LinkedList<>();
         if (!_resolveGenericSuperInterface(trace, clazz, superInter) || trace.size() == 0) throw new IllegalStateException("Could not find any generics?! huh");

        return map(trace, ignore);
    }

    public static <T extends I, I> Class<?>[] resolveGenericSuperClass(Class<T> clazz, Class<I> superClass) {
        return resolveGenericSuperClass(clazz, superClass, ALL);

    }

    public static <T extends I, I> Class<?>[] resolveGenericSuperClass(Class<T> clazz, Class<I> superClass, int from, int to) {
        return resolveGenericSuperClass(clazz, superClass, (l)->createIgnore(from, to, l));
    }

    public static <T extends I, I> Class<?>[] resolveGenericSuperClass(Class<T> clazz, Class<I> superClass, IntFunction<boolean[]> ignore) {
        checkTypeParams(clazz, superClass);

        Queue<ParameterizedType> trace = new LinkedList<>();
        if (!_resolveGenericSuperClass(trace, clazz, superClass) || trace.size() == 0) throw new IllegalStateException("Could not find any generics?! huh");

        return map(trace, ignore);
    }

    private static <T extends I, I> void checkTypeParams(Class<T> clazz, Class<I> superr) {
        if (!superr.isAssignableFrom(clazz)) throw new IllegalStateException("Class " + clazz.toString() + " does not extend " + superr.toString());
    }

    private static Class<?>[] map(Queue<ParameterizedType> trace, IntFunction<boolean[]> ignoreProvider) {
        ParameterizedType interfaceType = trace.size()<=2?trace.peek():trace.poll();
        Type[] last = interfaceType.getActualTypeArguments();
        Class<?>[] result = new Class<?>[last.length];
        int[] mapping = IntStream.rangeClosed(0, last.length-1).toArray();
        boolean[] ignoreArray = ignoreProvider.apply(last.length);

        /*
         * this is a greedy algorithm with no backtracking so it will break if two assignable generics swap position.
         * either it will not detect the swap or if the second is stricter the second will be left unresolved.
         * it also cannot deal with added generics that overlap as co-assignable with preexisting
         *
         * this might be solveable by looking at the type annotation object idk
         */
        for(ParameterizedType type:trace) {
            //System.out.println("for " + ((Class)type.getRawType()).getSimpleName());
            Type[] currentTypes = type.getActualTypeArguments();
            boolean[] used = new boolean[currentTypes.length];
            for (int i = 0; i < mapping.length; i++) {
                if (result[i] != null)continue; //already found
                Type goal = last[mapping[i]];

                boolean found = false;

                for (int j = 0; j < currentTypes.length; j++) {
                    if (used[j])continue;
                    Type current = currentTypes[j];
                    if (checkBounds(goal, current)) {
                        if (current instanceof Class || current instanceof ParameterizedType) {
                            result[i] = getTypeClass(current);
                            mapping[i] = -1; //found so should not be needed anymore, if used again this will force an exception
                        } else {
                            mapping[i] = j;
                        }
                        used[j] = true;
                        found = true;
                        break;
                    }
                }
                if (!found&&!ignoreArray[i])
                    throw new IllegalStateException("Could not trace generics from interface to implementation");
            }
            last = currentTypes;
            /*System.out.println("Current mappings is: " + Arrays.toString(mapping));
            for (int j = 0; j < currentTypes.length; j++) {
                if (currentTypes[j] instanceof TypeVariable) {
                    System.out.println("currentType bounds are: " + Strings.concatArray(t->t.getTypeName() + "(" + t.simpleName(getClass()) + ")",((TypeVariable)currentTypes[j]).getBounds()));
                }

                System.out.println("currentType bounds are: " + currentTypes[j].simpleName(getClass()));
            }

            System.out.println("traces generics: " + Strings.concatArray(Type::getTypeName,type.getActualTypeArguments()));/**/
        }


        return result;
    }

    @SuppressWarnings("unchecked")
    private static boolean checkBounds(Type t1, Type t2) {
        Class[] c1 = getTypeBounds(t1);
        Class[] c2 = getTypeBounds(t2);
        for (int i = 0; i < c1.length; i++) {
            boolean found = false;
            for (int j = 0; j < c2.length; j++) {
                if (c1[i].isAssignableFrom(c2[j])) {
                    found = true;
                    break;
                }
            }
            if (!found)return false;
        }
        return true;
    }

    private static Class[] getTypeBounds(Type t) {
        if (t instanceof Class) return new Class[]{(Class) t};
        if (t instanceof ParameterizedType) return new Class[]{(Class) ((ParameterizedType) t).getRawType()};
        if (t instanceof TypeVariable) {
            TypeVariable v = (TypeVariable) t;
            Type[] bounds = v.getBounds();
            Class[] result = new Class[bounds.length];
            for (int i = 0; i < bounds.length; i++) {
                result[i] = getTypeClass(bounds[i]);
            }
            return result;
        }
        throw new IllegalStateException("could not convert type bound to classes");
    }


    private static Class getTypeClass(Type t) {
        if (t instanceof Class) return (Class) t;
        if (t instanceof ParameterizedType) return  (Class) ((ParameterizedType) t).getRawType();
        if (t instanceof TypeVariable) return getTypeBounds(t)[0]; //hacky but makes things easier
        throw new IllegalStateException("could not convert type to class");
    }


    private static boolean _resolveGenericSuperInterface(Queue<ParameterizedType> trace, Class<?> clazz, Class<?> superInter) {
        if (!superInter.isAssignableFrom(clazz)) return false; //we are on the wrong track
        //System.out.println("trace for class " + clazz.toGenericString() + " extends "  +  clazz.getGenericSuperclass().toString() + " and impl " + Strings.concatArray(clazz.getGenericInterfaces()));
        for (Type type:clazz.getGenericInterfaces()) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> raw = (Class<?>) pType.getRawType();
            if (raw == superInter) {
                trace.add(pType);  //found it
                return true;
            }
            if (_resolveGenericSuperInterface(trace, raw, superInter)) {
                trace.add(pType);  //found it so push out generics as well
                return true;
            }
        }
        //didnt find it so search deeper
        for (Class sinter:clazz.getInterfaces()) {
            if (_resolveGenericSuperInterface(trace, sinter, superInter)) {
                //no push we aint generic
                return true;
            }
        }
        //make sure we have a superclass, but we really should if we get here
        if (!clazz.isInterface()) {
            Class<?> sclazz = clazz.getSuperclass();
            if (sclazz == null)throw new IllegalStateException("there is no superclass of matching interface but interface still is assignable from class... huh?"); //reached Object (we should never get here hmm)
            if (_resolveGenericSuperInterface(trace ,sclazz, superInter)) {
                Type type = clazz.getGenericSuperclass();
                if (type instanceof ParameterizedType)trace.add((ParameterizedType) type);
                return true;
            }
        }
        throw new IllegalStateException("there is no superclass of matching interface but interface still is assignable from class... huh?"); //we should have already covered all cases?!
    }


    private static boolean _resolveGenericSuperClass(Queue<ParameterizedType> trace, Class<?> clazz, Class<?> superClass) {
        if (!superClass.isAssignableFrom(clazz)) return false; //we are on the wrong track
        //System.out.println("trace for class " + clazz.toGenericString() + " extends "  +  clazz.getGenericSuperclass().toString() + " and impl " + Strings.concatArray(clazz.getGenericInterfaces()));
        //make sure we have a superclass, but we really should if we get here
        if (!clazz.isInterface()) {
            Class<?> sclazz = clazz.getSuperclass();
            Type type = clazz.getGenericSuperclass();
            if (sclazz == null)throw new IllegalStateException("there is no superclass of matching interface but interface still is assignable from class... huh?"); //reached Object (we should never get here hmm)
            if (sclazz == superClass) {
                trace.add((ParameterizedType) type);  //found it
                return true;
            }
            if (_resolveGenericSuperClass(trace ,sclazz, superClass)) {
                if (type instanceof ParameterizedType)trace.add((ParameterizedType) type);
                return true;
            }
        }
        throw new IllegalStateException("there is no superclass of matching interface but interface still is assignable from class... huh?"); //we should have already covered all cases?!
    }
}
