package dev.mrcai.datamining.preprocessing;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class IOUtils {
    private IOUtils() {
    }

    public static Instances loadInstances(String filePath) throws Exception {
        return new DataSource(filePath).getDataSet();
    }

    public static void saveInstances(String filePath, Instances instances) throws Exception {
        DataSink.write(filePath, instances);
    }
}
