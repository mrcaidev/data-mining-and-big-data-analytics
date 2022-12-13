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
        norm.normalize();
        norm.saveDataSet("outputs/preprocessing/iris-normalized.arff");
    }

    public Normalization(String filePath) throws Exception {
        loadDataSet(filePath);
    }

    private void loadDataSet(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        dataSet = source.getDataSet();
    }

    private void normalize() throws Exception {
        Normalize norm = new Normalize();
        norm.setInputFormat(dataSet);
        dataSet = Filter.useFilter(dataSet, norm);
    }

    private void saveDataSet(String filePath) throws Exception {
        DataSink.write(filePath, dataSet);
    }
}
