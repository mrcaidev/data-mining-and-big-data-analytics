package dev.mrcai.datamining.preprocessing;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FeatureSelection {
    private Instances instances;

    public static void main(String[] args) throws Exception {
        FeatureSelection fs = new FeatureSelection("data/preprocessing/iris.arff");
        Instances selectedInstances = fs.select(2);
        IOUtils.saveInstances("outputs/preprocessing/iris-selected.arff", selectedInstances);
    }

    public FeatureSelection(String filePath) throws Exception {
        instances = IOUtils.loadInstances(filePath);
    }

    private Instances select(int selectNum) throws Exception {
        AttributeSelection selection = new AttributeSelection();
        selection.setInputFormat(instances);

        ASEvaluation evaluation = new InfoGainAttributeEval();
        selection.setEvaluator(evaluation);

        Ranker rank = new Ranker();
        rank.setThreshold(0);
        rank.setNumToSelect(selectNum);
        selection.setSearch(rank);

        return Filter.useFilter(instances, selection);
    }
}
