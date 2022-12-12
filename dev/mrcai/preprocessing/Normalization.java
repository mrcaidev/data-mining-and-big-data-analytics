package dev.mrcai.preprocessing;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class Normalization {
    public static void main(String[] args) throws Exception {
        // Read data.
        DataSource source = new DataSource("data/preprocessing/iris.arff");
        Instances instances = source.getDataSet();

        // Normalize instances.
        Normalize norm = new Normalize();
        norm.setInputFormat(instances);
        Instances normalizedInstances = Filter.useFilter(instances, norm);

        // Write result.
        DataSink.write("outputs/preprocessing/iris-normalized.arff", normalizedInstances);
    }
}
