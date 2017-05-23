package com.example.peka.moneytracker.utils;

import com.example.peka.moneytracker.models.Expense;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by peka on 13.05.17.
 */

public final class ExpenseUtils {
    private ExpenseUtils() {
    }

    public static Map<DateTime, List<Expense>> splitByDates(List<Expense> expenses) {
        Map<DateTime, List<Expense>> splitted = new TreeMap<>();
        List<Expense> localCopy = new ArrayList<>(expenses);
        Collections.sort(localCopy, new Comparator<Expense>() {
            @Override
            public int compare(Expense o1, Expense o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        if (!localCopy.isEmpty()) {
            DateTime firstDate = localCopy.get(0).getDate();
            List<Expense> inDay = new ArrayList<>();
            for (Expense expense : localCopy) {
                //todo dirty dirty dirty
                if (firstDate.withTimeAtStartOfDay().compareTo(expense.getDate().withTimeAtStartOfDay()) >= 0) {
                    inDay.add(expense);
                } else {
                    splitted.put(firstDate, inDay);
                    firstDate = expense.getDate();
                    inDay = new ArrayList<>();
                    inDay.add(expense);
                }
            }

            splitted.put(firstDate, inDay);
        }

        return splitted;
    }
}
