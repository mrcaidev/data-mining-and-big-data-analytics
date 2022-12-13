package dev.mrcai.preprocessing;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class MissingValueSubstitution {
    Instances dataSet;
    Object[] substitutions;

    public static void main(String[] args) throws Exception {
        MissingValueSubstitution mvs = new MissingValueSubstitution("data/preprocessing/labor.arff");
        Instances substitutedDataSet = mvs.substitute();
        saveDataSet("outputs/preprocessing/labor-substituted.arff", substitutedDataSet);
    }

    public MissingValueSubstitution(String filePath) throws Exception {
        dataSet = loadDataSet(filePath);
        substitutions = getSubstitutions();
    }

    private Object[] getSubstitutions() {
        int columnNum = dataSet.numAttributes();
        Object[] substitutions = new Object[columnNum];

        for (int column = 0; column < columnNum; column++) {
            Attribute attribute = dataSet.attribute(column);
            double meanOrMode = dataSet.meanOrMode(column);
            substitutions[column] = attribute.isAveragable() ? meanOrMode : attribute.value((int) meanOrMode);
        }

        return substitutions;
    }

    private Instances substitute() {
        Instances substitutedDataSet = new Instances(dataSet);

        int rowNum = substitutedDataSet.numInstances();
        int columnNum = substitutedDataSet.numAttributes();

        for (int row = 0; row < rowNum; row++) {
            Instance data = substitutedDataSet.instance(row);
            for (int column = 0; column < columnNum; column++) {
                if (!data.isMissing(column)) {
                    continue;
                }
                if (data.attribute(column).isAveragable()) {
                    data.setValue(column, (double) substitutions[column]);
                } else {
                    data.setValue(column, (String) substitutions[column]);
                }
            }
        }

        return substitutedDataSet;
    }

    private static Instances loadDataSet(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        return source.getDataSet();
    }

    private static void saveDataSet(String filePath, Instances dataSet) throws Exception {
        DataSink.write(filePath, dataSet);
    }
}
