package org.glebchanskiy.lois1.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

        if (checkGrammarRules(formula) == one) {
            return one;
        }

        if (!isCorrectSequenceBrackets(formula)) {
            return two;
        }

        String[] terms = divideExpression(formula, disjunction);  //делим на слагаемые

        if (checkRepetitionOfSigns(deleteBrackets(terms))) {
            return two;
        }

        String[] multipliers;
        int multiplierCount = 0;
        String[] firstMultipliers = new String[0];

        ArrayList<String[]> lastTerms = new ArrayList<>();
        for (int i =0; i < deleteBrackets(terms).length; i++) {
            multipliers = divideExpression(deleteBrackets(terms)[i], conjunction); //делим слагаемое на отдельные "формулы"
            if (multiplierCount == 0) {
                multiplierCount = multipliers.length;
                firstMultipliers = multipliers;
                lastTerms.add(multipliers);
            } else {
                if(check(multiplierCount, multipliers, lastTerms, firstMultipliers) == one){
                    return one;
                }
                lastTerms.add(multipliers);
            }

            if (checkInside(multipliers) == one) {
                return one;
            }
        }
        return isCorrectBrackets(terms, multiplierCount);
    }

    private static int checkGrammarRules(String formula) {
        for (int i = 0; i < formula.length(); i++) {
            if (grammarRules.indexOf(formula.charAt(i)) == -1) { //некорретные символы
                return one;
            }
        }
        return two;
    }

    private static int check(int multiplierCount, String[] multipliers,
                             ArrayList<String[]> lastTerms , String[] firstMultipliers){
        if (multiplierCount != multipliers.length){ //сравниваем кол-во множителей у слагаемых
            return one;
        }
        if (equalExpression(lastTerms, multipliers)) {
            return one;
        }

        if (checkRepetitionOfSymbolsInMultipliers(multiplierCount, multipliers, firstMultipliers) == one) {
            return one;
        }
        return two;
    }
    private static int checkInside(String[] multipliers) {
        for (int i = 0; i < multipliers.length; i++) {
            int lastIndex1 = multipliers[i].length();
            if (lastIndex1 > 2) {//если кол-во знаков у множителя > 2
                return one;
            }

            if (checkInside(i, multipliers, lastIndex1) == one) {
                return one;
            }
        }
        return two;
    }

    private static int checkInside(int i, String[] multipliers, int lastIndex1) {
        for (int j = i + 1; j < multipliers.length; j++) {
            int lastIndex2 = multipliers[j].length();
            //если в множителе некоторые символы ==, то это не сднф(A&(B&A))
            if (multipliers[i].charAt(lastIndex1 - 1) == multipliers[j].charAt(lastIndex2 - 1)) {
                return one;
            }
        }
        return two;
    }

    private static int checkRepetitionOfSymbolsInMultipliers(int multiplierCount, String[] multipliers, String[] firstMultipliers) {
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
        return two;
    }

    private static String[] deleteBrackets(String[] terms) {
        String[] termsWithoutBrackets = new String[terms.length];  //избавляемся от скобок

        int k = 0;
        for (String term : terms) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < term.length(); i++) {
                if (brackets.indexOf(term.charAt(i)) == -1)
                    stringBuilder.append(term.charAt(i));
            }
            termsWithoutBrackets[k] = stringBuilder.toString();
            k++;
        }
        return termsWithoutBrackets;
    }

    private static boolean checkRepetitionOfSigns(String[] terms) {
        for (String term : terms)
            for (int i = 0; i < term.length() - 1; i++) { //повторение знаков, например,(A&&B)
                if (term.charAt(i) == term.charAt(i + 1)) {//если за следующим знаком идет такой же то возращаем false
                    return true;
                }
            }
        return false;
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

    private static int getMaxCountViaGetCounters(String[] terms) {
        int maxCounter = 0;
        for (String term : terms) {
            int[] counters = CheckPdnfFormula.getCounters(term);
            if (counters.length > 1) {
                int firstCounter = counters[0];
                if (firstCounter > maxCounter) {
                    maxCounter = firstCounter;
                }
            }
        }
        return maxCounter;
    }

    private static int getMaxOpeningBracketCount(String[] terms) {
        int maxCount = 0;
        for (String term : terms) {
            int count = 0;
            for (int i = 0; i < term.length(); i++) {
                if (term.charAt(i) == '(') {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
            }
        }
        return maxCount;
    }


    private static int getCountsOfOpenBrackets(String term) {
        int count = 0;
        for (int i = 0; i < term.length(); i++) {
            if (term.charAt(i) == '(') {
                count++;
            }
        }
        return count;
    }


    private static int isCorrectBrackets(String[] terms, int multiplierCount) {
        int countTerms = terms.length;


        int maxCountOfOpen = getMaxOpeningBracketCount(terms);

        int maxCountViaGetCounters = getMaxCountViaGetCounters(terms);


        if (!(maxCountOfOpen == maxCountViaGetCounters && maxCountViaGetCounters == getCounters(terms[0])[0]
                && getCounters(terms[0])[0] == getCountsOfOpenBrackets(terms[0]))) {
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
