package dev.mrcai.preprocessing;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class MissingValueSubstitution {
    private Instances instances;
    private Object[] substitutions;

    public static void main(String[] args) throws Exception {
        MissingValueSubstitution mvs = new MissingValueSubstitution("data/preprocessing/labor.arff");
        Instances substitutedInstances = mvs.substitute();
        saveInstances("outputs/preprocessing/labor-substituted.arff", substitutedInstances);
    }

    public MissingValueSubstitution(String filePath) throws Exception {
        instances = loadInstances(filePath);
        substitutions = getSubstitutions();
    }

    private Object[] getSubstitutions() {
        int columnNum = instances.numAttributes();
        Object[] substitutions = new Object[columnNum];

        for (int column = 0; column < columnNum; column++) {
            Attribute attribute = instances.attribute(column);
            double meanOrMode = instances.meanOrMode(column);
            substitutions[column] = attribute.isAveragable() ? meanOrMode : attribute.value((int) meanOrMode);
        }

        return substitutions;
    }

    private Instances substitute() {
        Instances substitutedInstances = new Instances(instances);

        int rowNum = substitutedInstances.numInstances();
        int columnNum = substitutedInstances.numAttributes();

        for (int row = 0; row < rowNum; row++) {
            Instance instance = substitutedInstances.instance(row);
            for (int column = 0; column < columnNum; column++) {
                if (!instance.isMissing(column)) {
                    continue;
                }
                if (instance.attribute(column).isAveragable()) {
                    instance.setValue(column, (double) substitutions[column]);
                } else {
                    instance.setValue(column, (String) substitutions[column]);
                }
            }
        }

        return substitutedInstances;
    }

    private static Instances loadInstances(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    private static void saveInstances(String filePath, Instances instances) throws Exception {
        DataSink.write(filePath, instances);
    }
}
