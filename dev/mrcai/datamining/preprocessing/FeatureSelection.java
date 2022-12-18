package dev.mrcai.datamining.preprocessing;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FeatureSelection {
    private Instances instances = null;

    public static void main(String[] args) throws Exception {
        FeatureSelection fs = new FeatureSelection("data/iris.arff", 2);
        fs.save("outputs/iris-selected.arff");
    }

    public FeatureSelection(String filePath, int featureNum) throws Exception {
        instances = IOUtils.loadInstances(filePath);
        select(featureNum);
    }

    public void save(String filePath) throws Exception {
        IOUtils.saveInstances(filePath, instances);
    }

    private void select(int featureNum) throws Exception {
        AttributeSelection selection = new AttributeSelection();
        selection.setInputFormat(instances);

        ASEvaluation evaluation = new InfoGainAttributeEval();
        selection.setEvaluator(evaluation);

        Ranker rank = new Ranker();
        rank.setThreshold(0);
        rank.setNumToSelect(featureNum);
        selection.setSearch(rank);

        instances = Filter.useFilter(instances, selection);
    }
}
