package dev.mrcai.datamining.association;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IOUtils {
    private IOUtils() {
    }

    public static List<Set<String>> loadItemSets(String filePath) {
        List<Set<String>> itemSets = new ArrayList<Set<String>>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] stringItems = line.trim().split(",");
                Set<String> itemSet = Set.of(stringItems);
                itemSets.add(itemSet);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemSets;
    }

    public static void printFrequentItemSets(Map<Set<String>, Double> frequentItemSets) {
        for (Set<String> itemSet : frequentItemSets.keySet()) {
            double support = Math.round(frequentItemSets.get(itemSet) * 100.0) / 100.0;
            System.out.println(itemSet + ", support=" + support);
        }
    }

    public static void printAssociationRules(Map<Map<Set<String>, Set<String>>, Double> rules) {
        for (Map<Set<String>, Set<String>> rule : rules.keySet()) {
            Set<String> condition = rule.keySet().iterator().next();
            Set<String> result = rule.get(condition);
            double confidence = Math.round(rules.get(rule) * 100) / 100.0;
            System.out.println(condition + " => " + result + ", confidence=" + confidence);
        }
    }
}
