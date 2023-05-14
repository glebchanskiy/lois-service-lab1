package org.glebchanskiy.lois1.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class CheckPdnfFormula {
    private static final String grammarRules = "()/\\!ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int zero = 0;
    private static final int one = 1;
    private static final int two = 2;

    private static final String conjunction = "/\\";
    private static final String disjunction = "\\/";
    private static final String brackets = "()";

    public static int checkFormula(String formula) {

        if (formula == null || formula.equals("")) {

            return two;
        }

        for (int i = 0; i < formula.length(); i++) {
            if (grammarRules.indexOf(formula.charAt(i)) == -1) { //некорретные символы

                return one;
            }
        }

        if (!isCorrectSequenceBrackets(formula)) {
            return two;
        }

        String[] terms = divideExpression(formula, disjunction);  //делим на слагаемые
        String[] terms2 = new String[terms.length];  //избавляемся от скобок

        int k = 0;
        for (String term : terms) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < term.length(); i++) {
                if (brackets.indexOf(term.charAt(i)) == -1)
                    stringBuilder.append(term.charAt(i));
            }
            terms2[k] = stringBuilder.toString();
            k++;
        }

        for (String term : terms2)
            for (int i = 0; i < term.length() - 1; i++) { //повторение знаков, например,(A&&B)
                if (term.charAt(i) == term.charAt(i + 1)) //если за следующим знаком идет такой же то возращаем false
                    return two;

            }

        String[] multipliers;
        String[] firstMultipliers = new String[0];
        ArrayList<String[]> lastTerms = new ArrayList<>();
        int multiplierCount = 0;

        for (String term : terms2) {
            multipliers = divideExpression(term, conjunction); //делим слагаемое на отдельные "формулы"
            if (multiplierCount == 0) {
                multiplierCount = multipliers.length;
                firstMultipliers = multipliers;
                lastTerms.add(multipliers);
            } else {
                if (multiplierCount != multipliers.length) { //сравниваем кол-во множителей у слагаемых

                    return one;
                }
                if (equalExpression(lastTerms, multipliers)) {

                    return one;
                }

                lastTerms.add(multipliers);


                for (int i = 0; i < multiplierCount; i++) {
                    int count = 0;
                    int start = 0;

                    for (int j = 0; j < multiplierCount; j++) {
                        int lastIndex2 = multipliers[j].length() - 1;
                        while (firstMultipliers[i].indexOf(multipliers[j].charAt(lastIndex2), start) != -1) {
                            count++;
                            start = firstMultipliers[i].indexOf(multipliers[j].charAt(lastIndex2), start) + 1;
                        }
                    }
                    if (count != 1) {
                        return one;
                    }
                }
            }

            for (int i = 0; i < multipliers.length; i++) {
                int lastIndex1 = multipliers[i].length();
                if (lastIndex1 > 2) {//если кол-во знаков у множителя > 2
                    return one;
                }

                for (int j = i + 1; j < multipliers.length; j++) {
                    int lastIndex2 = multipliers[j].length();
                    //если в множителе некоторые символы ==, то это не сднф(A&(B&A))
                    if (multipliers[i].charAt(lastIndex1 - 1) == multipliers[j].charAt(lastIndex2 - 1)) {
                        return one;
                    }
                }
            }
        }
        return isCorrectBrackets(terms, multiplierCount);
    }

    private static boolean isCorrectSequenceBrackets(String formula) {
        int counter = 0;
        for (int i = 0; i < formula.length(); i++) { //проверяем на правильную последовательность скобок
            if (formula.charAt(i) == '(')
                counter++;
            else if (formula.charAt(i) == ')')
                counter--;
            if (counter < 0)
                return false;
        }
        if (counter != 0)
            return false;

        if (formula.length() != 1)
            for (int i = 0; i < formula.length(); i++) {
                if (symbols.indexOf(formula.charAt(i)) != -1) {
                    int prev = 1, next = 1;
                    try {
                        if (formula.charAt(i - 1) == '!') {
                            prev += 2;
                            next++;
                        }
                        if (formula.charAt(i - prev) != '(' && formula.charAt(i + next) != ')')
                            return false;
                    } catch (Exception ignored) {
                    }
                }
            }
        return true;
    }

    private static boolean equalExpression(ArrayList<String[]> terms, String[] term2) {
        for (String[] term1 : terms) {
            boolean result = true;
            for (String multiplier : term1) {
                int lastIndex1 = multiplier.length() - 1;
                for (String s : term2) {
                    int lastIndex2 = s.length() - 1;
                    if (multiplier.charAt(lastIndex1) == s.charAt(lastIndex2))
                        result &= multiplier.equals(s);
                }
            }
            if (result)
                return true;
        }
        return false;
    }

    private static String[] divideExpression(String formula, String term) {
        int end;
        ArrayList<String> result = new ArrayList<>();

        int start = 0;
        while (true) {
            if (formula.indexOf(term, start) == -1) {
                result.add(formula.substring(start));
                break;
            }

            end = formula.indexOf(term, start);
            result.add(formula.substring(start, end));
            start = end + 2;
        }


        return result.toArray(new String[0]);
    }

    private static String reverseExpression(String expression) {
        StringBuilder output = new StringBuilder();
        for (int i = expression.length() - 1; i >= 0; i--) {
            switch (expression.charAt(i)) {
                case ')' -> output.append('(');
                case '(' -> output.append(')');
                case '/' -> output.append('\\');
                case '\\' -> output.append('/');
                default -> {
                    if (expression.charAt(i - 1) == '!') {
                        output.append(expression.charAt(i - 1)).append(expression.charAt(i));
                        i--;
                    } else {
                        output.append(expression.charAt(i));
                    }
                }
            }

        }
        return String.valueOf(output);
    }

    private static String[] reverseTerms(String[] terms) {
        String[] reversed = Arrays.copyOf(terms, terms.length);

        Collections.reverse(Arrays.asList(reversed));

        for (int i = 0; i < reversed.length; i++) {

            reversed[i] = reverseExpression(reversed[i]);
        }
        return reversed;
    }

    public static int[] getCounters(String term) {
        int countOpenBrackets = 0;
        int countCloseBrackets = 0;
        for (int j = 0; j < term.length(); j++) {
            if (term.charAt(j) == '(')
                countOpenBrackets++;
            if (term.charAt(j) == ')')
                countCloseBrackets++;
            try {
                if (term.charAt(j) == '!')
                    if (term.charAt(j - 1) == '(' && term.charAt(j + 2) == ')') {
                        countOpenBrackets--;
                        countCloseBrackets--;
                    } else

                        return new int[]{two};
            } catch (Exception e) {

                return new int[]{two};
            }
        }
        return new int[]{countOpenBrackets, countCloseBrackets};
    }

    private static int isCorrectBrackets(String[] terms, int multiplierCount) {
        int countTerms = terms.length;


        int maxCountOfOpen = Arrays.stream(terms)
                .mapToInt(term -> (int) term.chars()
                        .filter(c -> c == '(')
                        .count())
                .max()
                .orElse(0);

        int maxCountViaGetCounters = Arrays.stream(terms)
                .map(CheckPdnfFormula::getCounters)
                .filter(arr -> arr.length > 1)
                .map(arr -> arr[0]).max(Comparator.comparingInt(a -> a))
                .orElse(0);



        if(!(maxCountOfOpen == maxCountViaGetCounters && maxCountViaGetCounters == getCounters(terms[0])[0]
                && getCounters(terms[0])[0]  == terms[0].chars()
                .mapToObj(c -> (char) c)
                .filter(c -> c == '(')
                .count())){
            terms = reverseTerms(terms);
        }
        for (int i = 0; i < countTerms; i++) {
            int[] counters = getCounters(terms[i]);
            if (counters.length == 1) {
                return two;
            }
            int countOpenBrackets = counters[0];
            int countCloseBrackets = counters[1];


            if (i == 0) {
                if (terms.length == 1 && divideExpression(terms[i], conjunction).length == 1) {
                    if (countOpenBrackets != 0 || countCloseBrackets != 0)
                        return two;
                } else if (countOpenBrackets != (multiplierCount - 1 + countTerms - 1) ||
                        countCloseBrackets != multiplierCount - 1) {
                    return two;
                }
            } else if (countCloseBrackets != multiplierCount ||
                    countOpenBrackets != multiplierCount - 1) {

                return two;
            }
        }
        return zero;
    }
}
