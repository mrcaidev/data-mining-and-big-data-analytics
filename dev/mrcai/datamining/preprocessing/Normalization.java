package dev.mrcai.datamining.preprocessing;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class Normalization {
    private Instances instances = null;

    public static void main(String[] args) throws Exception {
        Normalization norm = new Normalization("data/preprocessing/iris.arff");
        norm.save("outputs/preprocessing/iris-normalized.arff");
    }

    public Normalization(String filePath) throws Exception {
        instances = IOUtils.loadInstances(filePath);
        normalize();
    }

    public void save(String filePath) throws Exception {
        IOUtils.saveInstances(filePath, instances);
    }

    private void normalize() throws Exception {
        Normalize norm = new Normalize();
        norm.setInputFormat(instances);
        instances = Filter.useFilter(instances, norm);
    }
}
