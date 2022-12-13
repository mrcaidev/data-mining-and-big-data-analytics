package dev.mrcai.association;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Apriori {
    private List<Set<String>> itemSets;
    private double minSupport;
    private double minConfidence;

    public static void main(String[] args) {
        Apriori apriori = new Apriori("data/association/top1000data.txt", 0.1, 0.5);
        apriori.go();
    }

    Apriori(String filePath, double minSupport, double minConfidence) {
        itemSets = loadItemSets(filePath);
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
    }

    public void go() {
        // Record all frequent item sets.
        List<Map<Set<String>, Double>> frequentItemSetsList = new ArrayList<Map<Set<String>, Double>>();

        // Start with frequent 1 item sets.
        frequentItemSetsList.add(getFrequentOneItemSets());

        while (true) {
            // Get the frequent k-1 item sets.
            Map<Set<String>, Double> frequentLastItemSets = frequentItemSetsList.get(frequentItemSetsList.size() - 1);

            // Get the frequent k item sets.
            Map<Set<String>, Double> frequentKItemSets = getFrequentKItemSets(frequentLastItemSets);

            // If there is no frequent k item set, stop exploring.
            if (frequentKItemSets.isEmpty()) {
                break;
            }

            // Otherwise, record the frequent k item sets.
            frequentItemSetsList.add(frequentKItemSets);
        }

        // Flatten all frequent item sets into a single map.
        Map<Set<String>, Double> allFrequentItemSets = new HashMap<Set<String>, Double>();
        for (Map<Set<String>, Double> frequentItemSets : frequentItemSetsList) {
            allFrequentItemSets.putAll(frequentItemSets);
        }

        // Print all frequent item sets.
        System.out.println("\nAll frequent item sets:\n");
        for (Set<String> itemSet : allFrequentItemSets.keySet()) {
            double support = Math.round(allFrequentItemSets.get(itemSet) * 100) / 100.0;
            System.out.println(itemSet + ", support=" + support);
        }

        // Get all association rules.
        Map<Map<Set<String>, Set<String>>, Double> associationRules = getAssociationRules(allFrequentItemSets);

        // Print all association rules.
        System.out.println("\nAll association rules:\n");
        for (Map<Set<String>, Set<String>> associationRule : associationRules.keySet()) {
            Set<String> condition = associationRule.keySet().iterator().next();
            Set<String> result = associationRule.get(condition);
            double confidence = Math.round(associationRules.get(associationRule) * 100) / 100.0;
            System.out.println(condition + " => " + result + ", confidence=" + confidence);
        }
    }

    private Map<Set<String>, Double> getFrequentOneItemSets() {
        // Record all frequent 1 item sets, and their corresponding support values.
        Map<Set<String>, Double> frequentOneItemSets = new HashMap<Set<String>, Double>();

        // Record all 1 item sets that have been counted, to avoid duplicate counting.
        Set<Set<String>> countedOneItemSets = new HashSet<Set<String>>();

        for (Set<String> itemSet : itemSets) {
            for (String item : itemSet) {
                // Treat each single item as a 1 item set.
                Set<String> oneItemSet = new HashSet<String>();
                oneItemSet.add(item);

                // If this 1 item set has been counted, skip it.
                if (countedOneItemSets.contains(oneItemSet)) {
                    continue;
                }

                // If this 1 item set is frequent, record it.
                double support = getSupport(oneItemSet);
                if (support >= minSupport) {
                    frequentOneItemSets.put(oneItemSet, support);
                }

                // Mark this 1 item set as counted.
                countedOneItemSets.add(oneItemSet);
            }
        }

        return frequentOneItemSets;
    }

    private Map<Set<String>, Double> getFrequentKItemSets(Map<Set<String>, Double> frequentLastItemSets) {
        // Record all frequent k item sets, and their corresponding support values.
        Map<Set<String>, Double> frequentKItemSets = new HashMap<Set<String>, Double>();

        // Record all k item sets that have been counted, to avoid duplicate counting.
        Set<Set<String>> countedKItemSets = new HashSet<Set<String>>();

        // For every two k-1 frequent item sets.
        for (Set<String> frequentLastItemSet1 : frequentLastItemSets.keySet()) {
            for (Set<String> frequentLastItemSet2 : frequentLastItemSets.keySet()) {
                // If the two k-1 item sets are the same, skip it.
                if (frequentLastItemSet1.equals(frequentLastItemSet2)) {
                    continue;
                }

                // Merge the two k-1 item sets into a new item set.
                Set<String> kItemSet = new HashSet<String>(frequentLastItemSet1);
                kItemSet.addAll(frequentLastItemSet2);

                // If the new item set is not a k item set, skip it.
                if (kItemSet.size() != frequentLastItemSet1.size() + 1) {
                    continue;
                }

                // If the k item set has been counted, skip it.
                if (countedKItemSets.contains(kItemSet)) {
                    continue;
                }

                // If the k item set has an infrequent k-1 subset, skip it.
                if (hasInfrequentSubset(kItemSet, frequentLastItemSets)) {
                    continue;
                }

                // If the merged item set is frequent, record it.
                double support = getSupport(kItemSet);
                if (support >= minSupport) {
                    frequentKItemSets.put(kItemSet, support);
                }

                // Mark this k item set as counted.
                countedKItemSets.add(kItemSet);
            }
        }

        return frequentKItemSets;
    }

    private static boolean hasInfrequentSubset(Set<String> itemSet, Map<Set<String>, Double> frequentLastItemSets) {
        // For every k-1 subset of the given item set.
        for (String item : itemSet) {
            Set<String> subset = new HashSet<String>(itemSet);
            subset.remove(item);

            // If this subset is not frequent, return true.
            if (!frequentLastItemSets.containsKey(subset)) {
                return true;
            }
        }
        return false;
    }

    private double getSupport(Set<String> itemSet) {
        double support = 0.0;
        for (Set<String> realItemSet : itemSets) {
            if (!realItemSet.containsAll(itemSet)) {
                continue;
            }
            support += 1;
        }
        return support / itemSets.size();
    }

    /**
     * Get all possible association rules based on the given frequent item set.
     *
     * @param frequentItemSet The frequent item set.
     * @return A map, whose key is a condition, and value is its result.
     *
     *         For example, if the given frequent item set is {A, B, C}, the
     *         association rules are {{A} -> {B, C}, {B} -> {A, C}, {C} -> {A, B},
     *         {A, B} -> {C}, {A, C} -> {B}, {B, C} -> {A}}.
     */
    private Map<Map<Set<String>, Set<String>>, Double> getAssociationRules(Map<Set<String>, Double> frequentItemSets) {
        // Record all association rules, and their corresponding confidence values.
        Map<Map<Set<String>, Set<String>>, Double> associationRules = new HashMap<Map<Set<String>, Set<String>>, Double>();

        // For every frequent item set.
        for (Set<String> frequentItemSet : frequentItemSets.keySet()) {
            // Get all possible association rules.
            Map<Set<String>, Set<String>> candidateRules = getPossibleAssociationRules(frequentItemSet);

            // For every possible association rule.
            for (Set<String> condition : candidateRules.keySet()) {
                // Get the condition and the result.
                Set<String> result = candidateRules.get(condition);

                // Get the confidence.
                double confidence = frequentItemSets.get(frequentItemSet) / frequentItemSets.get(condition);

                // If the confidence is not high enough, skip it.
                if (confidence < minConfidence) {
                    continue;
                }

                // Otherwise, record the association rule.
                Map<Set<String>, Set<String>> associationRule = new HashMap<Set<String>, Set<String>>();
                associationRule.put(condition, result);
                associationRules.put(associationRule, confidence);
            }
        }

        return associationRules;
    }

    /**
     * Get all possible association rules based on the given frequent item set.
     *
     * @param frequentItemSet The frequent item set.
     * @return A map, whose key is a condition, and value is its result.
     *
     *         For example, if the given frequent item set is {A, B, C}, the
     *         association rules are {{A} -> {B, C}, {B} -> {A, C}, {C} -> {A, B},
     *         {A, B} -> {C}, {A, C} -> {B}, {B, C} -> {A}}.
     */
    private static Map<Set<String>, Set<String>> getPossibleAssociationRules(Set<String> frequentItemSet) {
        // Record all possible association rules.
        Map<Set<String>, Set<String>> rules = new HashMap<Set<String>, Set<String>>();

        // For every possible condition size.
        for (int i = 1; i < frequentItemSet.size(); i++) {
            // Get all possible conditions.
            Set<Set<String>> conditions = getCombinations(frequentItemSet, i);

            // For every possible condition.
            for (Set<String> condition : conditions) {
                // Get the result.
                Set<String> result = new HashSet<String>(frequentItemSet);
                result.removeAll(condition);

                // Record the association rule.
                rules.put(condition, result);
            }
        }

        return rules;
    }

    /**
     * Get all possible combinations of a given set with a given size.
     *
     * @param set  The given set.
     * @param size The given size.
     * @return All possible combinations of the given set with the given size.
     *
     *         For example, if the given set is {A, B, C}, the combinations of size
     *         2 are {{A, B}, {A, C}, {B, C}}.
     */
    private static Set<Set<String>> getCombinations(Set<String> set, int size) {
        Set<Set<String>> combinations = new HashSet<Set<String>>();

        // If the size is 1, return all possible single element sets.
        if (size == 1) {
            for (String element : set) {
                Set<String> combination = new HashSet<String>();
                combination.add(element);
                combinations.add(combination);
            }
            return combinations;
        }

        // For every possible first element.
        for (String element : set) {
            // Get the remaining elements.
            Set<String> remainingElements = new HashSet<String>(set);
            remainingElements.remove(element);

            // Get all possible combinations of the remaining elements.
            Set<Set<String>> remainingCombinations = getCombinations(remainingElements, size - 1);

            // For every possible combination of the remaining elements.
            for (Set<String> remainingCombination : remainingCombinations) {
                // Add the first element to the combination.
                Set<String> combination = new HashSet<String>(remainingCombination);
                combination.add(element);

                // Record the combination.
                combinations.add(combination);
            }
        }

        return combinations;
    }

    private static List<Set<String>> loadItemSets(String filePath) {
        List<Set<String>> itemSets = new ArrayList<Set<String>>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> itemList = Arrays.asList(line.trim().split(","));
                Set<String> itemSet = new HashSet<String>(itemList);
                itemSets.add(itemSet);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemSets;
    }
}
