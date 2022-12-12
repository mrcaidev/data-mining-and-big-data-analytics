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
    public static void main(String[] args) throws Exception {
        // Read data.
        DataSource source = new DataSource("data/preprocessing/iris.arff");
        Instances instances = source.getDataSet();

        // Target count of features.
        Ranker rank = new Ranker();
        rank.setThreshold(0);
        rank.setNumToSelect(2);

        // Algorithm to select features.
        ASEvaluation evaluation = new InfoGainAttributeEval();

        // Apply rank and evaluation to selection.
        AttributeSelection selection = new AttributeSelection();
        selection.setEvaluator(evaluation);
        selection.setSearch(rank);
        selection.setInputFormat(instances);

        // Select features.
        Instances selectedInstances = Filter.useFilter(instances, selection);

        // Write result.
        DataSink.write("outputs/preprocessing/iris-selected.arff", selectedInstances);
    }
}
