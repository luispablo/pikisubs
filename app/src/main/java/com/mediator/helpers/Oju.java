package com.mediator.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by luispablo on 10/04/15.
 */
public class Oju {

    public static <T> List<T> replace(List<T> list, T newItem, UnaryChecker<T> checker) {
        List<T> newList = new ArrayList<>();

        for (T t : list) newList.add(checker.check(t) ? newItem : t);

        return newList;
    }

    public static <T> T findBy(List<T> list, UnaryChecker<T> checker) {
        for (T t : list) {
            if (checker.check(t)) return t;
        }
        return null;
    }

    public static String right(String string, int length) {
        return string.substring(string.length() - length, string.length());
    }

    public static String left(String string, int length) {
        return string.substring(0, length);
    }

    public static String leftFromLast(String string, String part) {
        return string.substring(0, string.lastIndexOf(part));
    }

    public static <T> List<T> filter(List<T> list, UnaryChecker<T> checker) {
        List<T> filteredList = new ArrayList<>();

        for (T item : list) {
            if (checker.check(item)) filteredList.add(item);
        }

        return filteredList;
    }

    public static String rigthFromLast(String string, String part) {
        return string.substring(string.lastIndexOf(part) + 1, string.length());
    }

    public static <T, M> boolean any(T item, M[] possibilities, BinaryChecker<T, M> checker) {
        boolean any = false;

        for (M possibility : possibilities) {
            if (checker.check(item, possibility)) any = true;
        }

        return any;
    }

    public static boolean anyEndsWith(String string, String[] possibilities) {
        return any(string, possibilities, new BinaryChecker<String, String>() {
            @Override
            public boolean check(String item, String possibility) {
                return item.endsWith(possibility);
            }
        });
    }

    public static boolean anyContains(String string, String[] possibilities) {
        return any(string, possibilities, new BinaryChecker<String, String>() {
            @Override
            public boolean check(String item, String possibility) {
                return item.contains(possibility);
            }
        });
    }

    public static <T, M> List<M> reduce(List<T> list, Reducer<T, M> reducer) {
        List<M> reduced = new ArrayList<>();

        for (T t : list) reduced.add(reducer.reduce(t));

        return reduced;
    }

    public static <T, M> Set<M> reduce(Set<T> set, Reducer<T, M> reducer) {
        Set<M> reduced = new HashSet<>();

        for (T t : set) reduced.add(reducer.reduce(t));

        return reduced;
    }

    public static <T> Set<T> set(List<T> list) {
        Set<T> set = new HashSet<>();
        set.addAll(list);

        return set;
    }

    public static <T> List<T> list(Vector<T> vector) {
        List<T> list = new ArrayList<>();
        list.addAll(vector);

        return list;
    }

    public static <T> List<T> list(Set<T> set) {
        List<T> list = new ArrayList<>();
        list.addAll(set);

        return list;
    }

    public interface Reducer<T, M> {
        M reduce(T t);
    }
    public interface BinaryChecker<T, M> {
        boolean check(T item, M possibility);
    }
    public interface UnaryChecker<T> {
        boolean check(T item);
    }
}
