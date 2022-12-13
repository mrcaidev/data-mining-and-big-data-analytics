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
        mvs.substitute();
        mvs.saveDataSet("outputs/preprocessing/labor-substituted.arff");
    }

    MissingValueSubstitution(String filePath) throws Exception {
        loadDataSet(filePath);
        getSubstitutions();
    }

    private void loadDataSet(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        dataSet = source.getDataSet();
    }

    private void getSubstitutions() {
        int columnNum = dataSet.numAttributes();
        substitutions = new Object[columnNum];

        for (int column = 0; column < columnNum; column++) {
            Attribute attribute = dataSet.attribute(column);
            double meanOrMode = dataSet.meanOrMode(column);
            substitutions[column] = attribute.isAveragable() ? meanOrMode : attribute.value((int) meanOrMode);
        }
    }

    private void substitute() {
        int rowNum = dataSet.numInstances();
        int columnNum = dataSet.numAttributes();

        for (int row = 0; row < rowNum; row++) {
            Instance data = dataSet.instance(row);
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
    }

    private void saveDataSet(String filePath) throws Exception {
        DataSink.write(filePath, dataSet);
    }
}
