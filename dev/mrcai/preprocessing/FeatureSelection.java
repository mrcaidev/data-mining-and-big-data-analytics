package dev.mrcai.preprocessing;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FeatureSelection {
    private Instances instances;

    public static void main(String[] args) throws Exception {
        FeatureSelection fs = new FeatureSelection("data/preprocessing/iris.arff");
        Instances selectedInstances = fs.select(2);
        saveInstances("outputs/preprocessing/iris-selected.arff", selectedInstances);
    }

    public FeatureSelection(String filePath) throws Exception {
        instances = loadInstances(filePath);
    }

    private Instances select(int selectNum) throws Exception {
        // Target count of features.
        Ranker rank = new Ranker();
        rank.setThreshold(0);
        rank.setNumToSelect(selectNum);

        // Algorithm to select features.
        ASEvaluation evaluation = new InfoGainAttributeEval();

        // Apply rank and evaluation to selection.
        AttributeSelection selection = new AttributeSelection();
        selection.setEvaluator(evaluation);
        selection.setSearch(rank);
        selection.setInputFormat(instances);

        // Select features.
        return Filter.useFilter(instances, selection);
    }

    private static Instances loadInstances(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    private static void saveInstances(String filePath, Instances instances) throws Exception {
        DataSink.write(filePath, instances);
    }
}
