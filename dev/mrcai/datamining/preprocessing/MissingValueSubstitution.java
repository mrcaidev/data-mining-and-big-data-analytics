package dev.mrcai.datamining.preprocessing;

import weka.core.Instance;
import weka.core.Instances;

public class MissingValueSubstitution {
    private Instances instances = null;

    public static void main(String[] args) throws Exception {
        MissingValueSubstitution mvs = new MissingValueSubstitution("data/labor.arff");
        mvs.save("outputs/labor-substituted.arff");
    }

    public MissingValueSubstitution(String filePath) throws Exception {
        instances = IOUtils.loadInstances(filePath);
        substitute();
    }

    public void save(String filePath) throws Exception {
        IOUtils.saveInstances(filePath, instances);
    }

    private void substitute() {
        double[] substitutions = getSubstitutions();

        int columnNum = instances.numAttributes();
        for (Instance instance : instances) {
            for (int column = 0; column < columnNum; column++) {
                instance.replaceMissingValues(substitutions);
            }
        }
    }

    /**
     * Get the substitution value for each attribute.
     *
     * If the attribute is averagable, the substitution is its mean value.
     * Otherwise, the substitution is its mode value.
     *
     * Actually, the `substitutions` should be of type `Instance`,
     * but Weka does not support the instantiation of `Instance`.
     * So we use `List<Object>` instead.
     *
     * @return The substitution for each attribute.
     */
    private double[] getSubstitutions() {
        int columnNum = instances.numAttributes();
        double[] substitutions = new double[columnNum];

        for (int column = 0; column < columnNum; column++) {
            substitutions[column] = instances.meanOrMode(column);
        }

        return substitutions;
    }
}
