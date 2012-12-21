package com.huamari;

/**
 * Created with IntelliJ IDEA.
 * User: hhasubra
 * Date: 10/25/12
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;

/**
 * This example reads given execution data files and dumps their content.
 */
public final class ExecDump {

    /**
     * Reads all execution data files specified as the arguments and dumps the
     * content.
     *
     * @param args
     *            list of execution data files
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        for (final String file : args) {
            dumpContent(file);
        }
    }

    private static void dumpContent(final String file) throws IOException {
        System.out.printf("exec file: %s%n", file);
        System.out.println("CLASS ID         HITS/PROBES   CLASS NAME");

        final FileInputStream in = new FileInputStream(file);
        final ExecutionDataReader reader = new ExecutionDataReader(in);
        reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
            public void visitSessionInfo(final SessionInfo info) {
                System.out.printf("Session \"%s\": %s - %s%n", info.getId(),
                        new Date(info.getStartTimeStamp()),
                        new Date(info.getDumpTimeStamp()));
            }
        });
        reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
            public void visitClassExecution(final ExecutionData data) {
                System.out.printf("%016x  %3d of %3d   %s%n",
                        Long.valueOf(data.getId()),
                        Integer.valueOf(getHitCount(data.getData())),
                        Integer.valueOf(data.getData().length), data.getName());
            }
        });
        reader.read();
        in.close();
        System.out.println();
    }

    private static int getHitCount(final boolean[] data) {
        int count = 0;
        for (final boolean hit : data) {
            if (hit) {
                count++;
            }
        }
        return count;
    }

    private ExecDump() {
    }
}