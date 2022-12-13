package dev.mrcai.preprocessing;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class Normalization {
    Instances dataSet;

    public static void main(String[] args) throws Exception {
        Normalization norm = new Normalization("data/preprocessing/iris.arff");
        Instances normalizedDataSet = norm.normalize();
        saveDataSet("outputs/preprocessing/iris-normalized.arff", normalizedDataSet);
    }

    public Normalization(String filePath) throws Exception {
        dataSet = loadDataSet(filePath);
    }

    private Instances normalize() throws Exception {
        Normalize norm = new Normalize();
        norm.setInputFormat(dataSet);
        return Filter.useFilter(dataSet, norm);
    }

    private static Instances loadDataSet(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    private static void saveDataSet(String filePath, Instances dataSet) throws Exception {
        DataSink.write(filePath, dataSet);
    }
}
