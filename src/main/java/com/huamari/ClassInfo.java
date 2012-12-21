package com.huamari;

/**
 * Created with IntelliJ IDEA.
 * User: hhasubra
 * Date: 10/25/12
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageVisitor;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;

/**
 * This example reads given Java class files, directories or JARs and dumps
 * information about the classes.
 */
public final class ClassInfo implements ICoverageVisitor {

    private final Analyzer analyzer;
    private final  ExecutionDataStore executionDataStore;

    private ClassInfo() {
        executionDataStore = new ExecutionDataStore();
        analyzer = new Analyzer(executionDataStore, this);
    }

    private void dumpInfo(final String file) throws IOException {
        analyzer.analyzeAll(new File(file));
    }

    public void visitCoverage(final IClassCoverage coverage) {
        System.out.printf("class name:   %s%n", coverage.getName());
        System.out.printf("class id:     %016x%n",
                Long.valueOf(coverage.getId()));
        System.out.printf("instructions: %s%n", Integer.valueOf(coverage
                .getInstructionCounter().getTotalCount()));
        System.out.printf("branches:     %s%n",
                Integer.valueOf(coverage.getBranchCounter().getTotalCount()));
        System.out.printf("lines:        %s%n",
                Integer.valueOf(coverage.getLineCounter().getTotalCount()));
        System.out.printf("methods:      %s%n",
                Integer.valueOf(coverage.getMethodCounter().getTotalCount()));
        System.out.printf("complexity:   %s%n%n", Integer.valueOf(coverage
                .getComplexityCounter().getTotalCount()));

    }

    /**
     * Reads all class file specified as the arguments and dumps information
     * about it to <code>stdout</code>.
     *
     * @param args
     *            list of class files
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final ClassInfo info = new ClassInfo();
        for (final String file : args) {
            info.dumpInfo(file);
        }
    }

}