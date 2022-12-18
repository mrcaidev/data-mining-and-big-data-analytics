package dev.mrcai.datamining.preprocessing;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
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
        List<Object> substitutions = getSubstitutions();

        int rowNum = instances.numInstances();
        int columnNum = instances.numAttributes();
        for (int row = 0; row < rowNum; row++) {
            Instance instance = instances.instance(row);
            for (int column = 0; column < columnNum; column++) {
                if (!instance.isMissing(column)) {
                    continue;
                }

                if (instance.attribute(column).isAveragable()) {
                    instance.setValue(column, (double) substitutions.get(column));
                } else {
                    instance.setValue(column, (String) substitutions.get(column));
                }
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
    private List<Object> getSubstitutions() {
        List<Object> substitutions = new ArrayList<Object>();

        int columnNum = instances.numAttributes();
        for (int column = 0; column < columnNum; column++) {
            Attribute attribute = instances.attribute(column);
            double meanOrMode = instances.meanOrMode(column);
            Object substitution = attribute.isAveragable() ? meanOrMode : attribute.value((int) meanOrMode);
            substitutions.add(substitution);
        }

        return substitutions;
    }
}
