package dev.mrcai.datamining.preprocessing;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class Normalization {
    private Instances instances;

    public static void main(String[] args) throws Exception {
        Normalization norm = new Normalization("data/preprocessing/iris.arff");
        Instances normalizedInstances = norm.normalize();
        IOUtils.saveInstances("outputs/preprocessing/iris-normalized.arff", normalizedInstances);
    }

    public Normalization(String filePath) throws Exception {
        instances = IOUtils.loadInstances(filePath);
    }

    private Instances normalize() throws Exception {
        Normalize norm = new Normalize();
        norm.setInputFormat(instances);
        return Filter.useFilter(instances, norm);
    }
}
