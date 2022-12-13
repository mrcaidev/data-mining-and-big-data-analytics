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
    private double minSupport;
    private double minConfidence;
    private List<Set<String>> dataSet;

    public static void main(String[] args) {
        Apriori apriori = new Apriori(0.1, 0.5);
        apriori.go();
    }

    Apriori(double minSupport, double minConfidence) {
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
        this.dataSet = Apriori.loadDataSet("data/association/top1000data.txt");
    }

    /**
     * Run the Apriori algorithm.
     */
    public void go() {
        // Record all frequent item sets.
        List<Map<Set<String>, Double>> allFrequentItemSetsList = new ArrayList<Map<Set<String>, Double>>();

        // Start with frequent 1 item sets.
        allFrequentItemSetsList.add(this.getFrequentOneItemSets());

        while (true) {
            // Get the frequent k-1 item sets.
            Map<Set<String>, Double> lastFrequentItemSets = allFrequentItemSetsList
                    .get(allFrequentItemSetsList.size() - 1);

            // Get the frequent k item sets.
            Map<Set<String>, Double> frequentItemSets = this.getFrequentItemSets(lastFrequentItemSets);

            // If there is no frequent k item set, stop exploring.
            if (frequentItemSets.isEmpty()) {
                break;
            }

            // Otherwise, record the frequent k item sets.
            allFrequentItemSetsList.add(frequentItemSets);
        }

        // Flatten all frequent item sets into a single map.
        Map<Set<String>, Double> allFrequentItemSets = new HashMap<Set<String>, Double>();
        for (Map<Set<String>, Double> frequentItemSets : allFrequentItemSetsList) {
            allFrequentItemSets.putAll(frequentItemSets);
        }

        // Print all frequent item sets.
        System.out.println("\nAll frequent item sets:\n");
        for (Set<String> itemSet : allFrequentItemSets.keySet()) {
            double support = Math.round(allFrequentItemSets.get(itemSet) * 100) / 100.0;
            System.out.println(itemSet + ", support=" + support);
        }

        // Get all association rules.
        Map<Map<Set<String>, Set<String>>, Double> associationRules = this.getAssociationRules(allFrequentItemSets);

        // Print all association rules.
        System.out.println("\nAll association rules:\n");
        for (Map<Set<String>, Set<String>> associationRule : associationRules.keySet()) {
            Set<String> condition = associationRule.keySet().iterator().next();
            Set<String> result = associationRule.get(condition);
            double confidence = Math.round(associationRules.get(associationRule) * 100) / 100.0;
            System.out.println(condition + " => " + result + ", confidence=" + confidence);
        }
    }

    /**
     * Load data set from a file.
     *
     * @param filePath The path of the file.
     * @return A list of data. Each data is a set of items.
     */
    private static List<Set<String>> loadDataSet(String filePath) {
        List<Set<String>> dataSet = new ArrayList<Set<String>>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {// 读一行文件
                List<String> rawData = Arrays.asList(line.trim().split(","));
                Set<String> data = new HashSet<String>(rawData);
                dataSet.add(data);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSet;
    }

    /**
     * Get all frequent 1 item sets.
     *
     * @return A map, whose key is a frequent 1 item set, and value is its
     *         corresponding support value.
     */
    private Map<Set<String>, Double> getFrequentOneItemSets() {
        Map<Set<String>, Double> frequentOneItemSets = new HashMap<Set<String>, Double>();
        Set<Set<String>> countedOneItemSets = new HashSet<Set<String>>();

        for (Set<String> data : this.dataSet) {
            for (String item : data) {
                // Treat each single item as a 1 item set.
                Set<String> itemSet = new HashSet<String>();
                itemSet.add(item);

                // If this 1 item set has been counted, skip it.
                if (countedOneItemSets.contains(itemSet)) {
                    continue;
                }

                // If this 1 item set is frequent, record it.
                double support = this.getSupport(itemSet);
                if (support >= this.minSupport) {
                    frequentOneItemSets.put(itemSet, support);
                }

                // Mark this 1 item set as counted.
                countedOneItemSets.add(itemSet);
            }
        }

        return frequentOneItemSets;
    }

    /**
     * Get all frequent k item sets based on the frequent k-1 item sets.
     *
     * @param lastFrequentItemSets The frequent k-1 item sets.
     * @return A map, whose key is a frequent k item set, and value is its
     *         corresponding support value.
     */
    private Map<Set<String>, Double> getFrequentItemSets(Map<Set<String>, Double> lastFrequentItemSets) {
        Map<Set<String>, Double> frequentItemSets = new HashMap<Set<String>, Double>();

        // For every two k-1 frequent item sets.
        for (Set<String> itemSet1 : lastFrequentItemSets.keySet()) {
            for (Set<String> itemSet2 : lastFrequentItemSets.keySet()) {
                // If the two k-1 item sets are the same, skip it.
                if (itemSet1.equals(itemSet2)) {
                    continue;
                }

                // Merge the two k-1 item sets into a new item set.
                Set<String> kItemSet = new HashSet<String>(itemSet1);
                kItemSet.addAll(itemSet2);

                // If the new item set is not a k item set, skip it.
                if (kItemSet.size() != itemSet1.size() + 1) {
                    continue;
                }

                // If the k item set has an infrequent k-1 subset, skip it.
                if (Apriori.hasInfrequentSubset(kItemSet, lastFrequentItemSets)) {
                    continue;
                }

                // If the merged item set is frequent, record it.
                double support = this.getSupport(kItemSet);
                if (support >= this.minSupport) {
                    frequentItemSets.put(kItemSet, support);
                }
            }
        }

        return frequentItemSets;
    }

    /**
     * Check if a given k item set has an infrequent k-1 subset.
     *
     * @param itemSet              The given k item set.
     * @param lastFrequentItemSets The frequent k-1 item sets.
     * @return True if the given k item set has an infrequent k-1 subset, or false
     *         otherwise.
     */
    private static boolean hasInfrequentSubset(Set<String> itemSet, Map<Set<String>, Double> lastFrequentItemSets) {
        // For every k-1 subset of the given item set.
        for (String item : itemSet) {
            Set<String> subset = new HashSet<String>(itemSet);
            subset.remove(item);

            // If this subset is not frequent, return true.
            if (!lastFrequentItemSets.containsKey(subset)) {
                return true;
            }
        }
        return false;
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
    private static Map<Set<String>, Set<String>> getCandidateRules(Set<String> frequentItemSet) {
        Map<Set<String>, Set<String>> candidateRules = new HashMap<Set<String>, Set<String>>();

        // For every possible condition size.
        for (int i = 1; i < frequentItemSet.size(); i++) {
            // Get all possible conditions.
            Set<Set<String>> conditions = Apriori.getCombinations(frequentItemSet, i);

            // For every possible condition.
            for (Set<String> condition : conditions) {
                // Get the result.
                Set<String> result = new HashSet<String>(frequentItemSet);
                result.removeAll(condition);

                // Record the association rule.
                candidateRules.put(condition, result);
            }
        }
        return candidateRules;
    }

    private Map<Map<Set<String>, Set<String>>, Double> getAssociationRules(
            Map<Set<String>, Double> allFrequentItemSets) {
        Map<Map<Set<String>, Set<String>>, Double> associationRules = new HashMap<Map<Set<String>, Set<String>>, Double>();

        // For every frequent item set.
        for (Set<String> frequentItemSet : allFrequentItemSets.keySet()) {
            // Get all possible association rules.
            Map<Set<String>, Set<String>> candidateRules = Apriori.getCandidateRules(frequentItemSet);

            // For every possible association rule.
            for (Set<String> condition : candidateRules.keySet()) {
                // Get the condition and the result.
                Set<String> result = candidateRules.get(condition);

                // Get the confidence.
                double confidence = allFrequentItemSets.get(frequentItemSet) / allFrequentItemSets.get(condition);

                // If the confidence is not high enough, skip it.
                if (confidence < this.minConfidence) {
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
            Set<Set<String>> remainingCombinations = Apriori.getCombinations(remainingElements, size - 1);

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

    /**
     * Get the support value of a given item set.
     *
     * @param itemSet The given item set.
     * @return The support value of the given item set.
     */
    private double getSupport(Set<String> itemSet) {
        double support = 0.0;
        for (Set<String> data : this.dataSet) {
            if (!data.containsAll(itemSet)) {
                continue;
            }
            support += 1;
        }
        return support / this.dataSet.size();
    }
}
