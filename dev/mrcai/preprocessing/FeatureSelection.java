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
    Instances dataSet;

    public static void main(String[] args) throws Exception {
        FeatureSelection fs = new FeatureSelection("data/preprocessing/iris.arff");
        Instances selectedDataSet = fs.select(2);
        saveDataSet("outputs/preprocessing/iris-selected.arff", selectedDataSet);
    }

    public FeatureSelection(String filePath) throws Exception {
        dataSet = loadDataSet(filePath);
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
        selection.setInputFormat(dataSet);

        // Select features.
        return Filter.useFilter(dataSet, selection);
    }

    private static Instances loadDataSet(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    private static void saveDataSet(String filePath, Instances dataSet) throws Exception {
        DataSink.write(filePath, dataSet);
    }
}
