package dev.mrcai.datamining.preprocessing;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * I/O utilities.
 */
public class IOUtils {
    /**
     * Private constructor to prevent instantiation.
     */
    private IOUtils() {
    }

    /**
     * Load instances from a file.
     *
     * @param filePath The path to the file to load the instances from.
     * @return The loaded instances.
     * @throws Exception If the instances cannot be loaded.
     */
    public static Instances loadInstances(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    /**
     * Save instances to a file.
     *
     * @param filePath  The path to the file to save the instances to.
     * @param instances The instances to save.
     * @throws Exception If the instances cannot be saved.
     */
    public static void saveInstances(String filePath, Instances instances) throws Exception {
        DataSink.write(filePath, instances);
    }
}
