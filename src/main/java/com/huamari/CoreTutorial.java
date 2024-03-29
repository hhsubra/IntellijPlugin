package com.huamari;

/**
 * Created with IntelliJ IDEA.
 * User: hhasubra
 * Date: 10/25/12
 * Time: 10:26 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;

/**
 * Example usage of the JaCoCo core API. In this tutorial a single target class
 * will be instrumented and executed. Finally the coverage information will be
 * dumped.
 */
public final class CoreTutorial {

    /**
     * The test target we want to see code coverage for.
     */
//    public static class TestTarget implements Runnable {
//
//        public void run() {
//            final int n = 7;
//            final String status = isPrime(n) ? "prime" : "not prime";
//            System.out.printf("%s is %s%n", Integer.valueOf(n), status);
//        }
//
//        private boolean isPrime(final int n) {
//            for (int i = 2; i * i <= n; i++) {
//                if ((n ^ i) == 0) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//    }

    /**
     * A class loader that loads classes from in-memory data.
     */
    public static class MemoryClassLoader extends ClassLoader {

        private final Map<String, byte[]> definitions = new HashMap<String, byte[]>();

        /**
         * Add a in-memory representation of a class.
         *
         * @param name
         *            name of the class
         * @param bytes
         *            class definition
         */
        public void addDefinition(final String name, final byte[] bytes) {
            definitions.put(name, bytes);
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve)
                throws ClassNotFoundException {
            final byte[] bytes = definitions.get(name);
            if (bytes != null) {
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.loadClass(name, resolve);
        }

    }

    private InputStream getTargetClass(final String name) {
        final String resource = '/' + name.replace('.', '/') + ".class";
        return getClass().getResourceAsStream(resource);
    }

    private void printCounter(final String unit, final ICounter counter) {
        final Integer missed = Integer.valueOf(counter.getMissedCount());
        final Integer total = Integer.valueOf(counter.getTotalCount());
        System.out.printf("%s of %s %s missed%n", missed, total, unit);
    }

    private String getColor(final int status) {
        switch (status) {
            case ICounter.NOT_COVERED:
                return "red";
            case ICounter.PARTLY_COVERED:
                return "yellow";
            case ICounter.FULLY_COVERED:
                return "green";
        }
        return "";
    }

    private void runTutorial() throws Exception {
        final String targetName = MultiplyTest.class.getName();

        // For instrumentation and runtime we need a IRuntime instance
        // to collect execution data:
        final IRuntime runtime = new LoggerRuntime();

        // The Instrumenter creates a modified version of our test target class
        // that contains additional probes for execution data recording:
        final Instrumenter instr = new Instrumenter(runtime);
        final byte[] instrumented = instr
                .instrument(getTargetClass(targetName));

        // Now we're ready to run our instrumented class and need to startup the
        // runtime first:
        runtime.startup();

        // In this tutorial we use a special class loader to directly load the
        // instrumented class definition from a byte[] instances.
        final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
        memoryClassLoader.addDefinition(targetName, instrumented);
        final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

        // Here we execute our test target class through its Runnable interface:
        final Runnable targetInstance = (Runnable) targetClass.newInstance();
        targetInstance.run();

        // At the end of test execution we collect execution data and shutdown
        // the runtime:
        final ExecutionDataStore executionData = new ExecutionDataStore();
        runtime.collect(executionData, null, false);
        runtime.shutdown();

        // Together with the original class definition we can calculate coverage
        // information:


        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
        analyzer.analyzeClass(getTargetClass(targetName));

        // Let's dump some metrics and line coverage information:
        for (final IClassCoverage cc : coverageBuilder.getClasses()) {
            System.out.printf("Coverage of class %s%n", cc.getName());

            printCounter("instructions", cc.getInstructionCounter());
            printCounter("branches", cc.getBranchCounter());
            printCounter("lines", cc.getLineCounter());
            printCounter("methods", cc.getMethodCounter());
            printCounter("complexity", cc.getComplexityCounter());

            for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
                System.out.printf("Line %s: %s%n", Integer.valueOf(i),
                        getColor(cc.getLine(i).getStatus()));
            }
        }

        ExecutionDataWriter fileWriter = new ExecutionDataWriter(new FileOutputStream(new File("executeData.info"))) ;
        for(ExecutionData executionDatas : executionData.getContents()) {
            fileWriter.visitClassExecution(executionDatas);
        }
        try {
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Execute the example.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new CoreTutorial().runTutorial();
    }

    private CoreTutorial() {
    }

}
