package dev.mrcai.preprocessing;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class Normalization {
    private Instances instances;

    public static void main(String[] args) throws Exception {
        Normalization norm = new Normalization("data/preprocessing/iris.arff");
        Instances normalizedInstances = norm.normalize();
        saveInstances("outputs/preprocessing/iris-normalized.arff", normalizedInstances);
    }

    public Normalization(String filePath) throws Exception {
        instances = loadInstances(filePath);
    }

    private Instances normalize() throws Exception {
        Normalize norm = new Normalize();
        norm.setInputFormat(instances);
        return Filter.useFilter(instances, norm);
    }

    private static Instances loadInstances(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    private static void saveInstances(String filePath, Instances instances) throws Exception {
        DataSink.write(filePath, instances);
    }
}
