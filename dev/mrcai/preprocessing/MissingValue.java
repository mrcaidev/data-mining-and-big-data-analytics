package dev.mrcai.preprocessing;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class MissingValue {
    public static void main(String[] args) throws Exception {
        // Read data.
        DataSource source = new DataSource("data/preprocessing/labor.arff");
        Instances instances = source.getDataSet();
        int rowNum = instances.numInstances();
        int columnNum = instances.numAttributes();

        // Calculate mean value for each column.
        double[] meanValues = new double[columnNum];
        for (int column = 0; column < columnNum; column++) {
            int sum = 0;
            int count = 0;
            for (int row = 0; row < rowNum; row++) {
                Instance instance = instances.instance(row);
                if (instance.isMissing(column)) {
                    continue;
                }
                sum += instance.value(column);
                count++;
            }
            meanValues[column] = (double) sum / count;
        }

        // Substitute missing value with mean value.
        for (int column = 0; column < columnNum; column++) {
            for (int row = 0; row < rowNum; row++) {
                Instance instance = instances.instance(row);
                if (instance.isMissing(column)) {
                    instance.setValue(column, meanValues[column]);
                }
            }
        }

        // Write result.
        DataSink.write("outputs/preprocessing/labor-full.arff", instances);
    }
}
