package dev.mrcai.datamining.preprocessing;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class MissingValueSubstitution {
    private Instances instances;

    public static void main(String[] args) throws Exception {
        MissingValueSubstitution mvs = new MissingValueSubstitution("data/preprocessing/labor.arff");
        Instances substitutedInstances = mvs.substitute();
        IOUtils.saveInstances("outputs/preprocessing/labor-substituted.arff", substitutedInstances);
    }

    public MissingValueSubstitution(String filePath) throws Exception {
        instances = IOUtils.loadInstances(filePath);
    }

    /**
     * Substitute missing values of all instances.
     *
     * @return The substituted instances.
     */
    private Instances substitute() {
        Object[] substitutions = getSubstitutions();

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

    /**
     * Get the substitution value for each attribute.
     *
     * If the attribute is averagable, the substitution is its mean value.
     * Otherwise, the substitution is its mode value.
     *
     * @return The substitution for each attribute.
     */
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
}
