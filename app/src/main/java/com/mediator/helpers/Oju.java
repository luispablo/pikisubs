package com.mediator.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by luispablo on 10/04/15.
 */
public class Oju {

    public static int matches(Set<String> set1, Set<String> set2) {
        int matches = 0;

        for (String string1 : set1) {
            for (String string2 : set2) {
                if (string1.equals(string2)) matches++;
            }
        }

        return matches;
    }

    public static Set<String> lowerCaseTerms(String string) {
        String replaced = string.toLowerCase().replaceAll(" ", ".");
        List<String> termsList = Oju.filter(Arrays.asList(replaced.split("\\.")), new Oju.UnaryChecker<String>() {
            @Override
            public boolean check(String item) {
                return !item.trim().isEmpty();
            }
        });
        return Oju.set(termsList);
    }

    public static String join(List<String> items, String character) {
        String joined = "";

        for (String item : items) joined += (joined.isEmpty() ? "" : character) + item;

        return joined;
    }

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

    public static <T, M> List<M> map(List<T> list, UnaryOperator<T, M> operator) {
        List<M> mapped = new ArrayList<>();

        for (T t : list) mapped.add(operator.operate(t));

        return mapped;
    }

    public static <T, M> Set<M> map(Set<T> set, UnaryOperator<T, M> operator) {
        Set<M> mapped = new HashSet<>();

        for (T t : set) mapped.add(operator.operate(t));

        return mapped;
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

    public interface UnaryOperator<T, M> {
        M operate(T t);
    }
    public interface BinaryChecker<T, M> {
        boolean check(T item, M possibility);
    }
    public interface UnaryChecker<T> {
        boolean check(T item);
    }
}
