package com.cassandra;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Command to generate 2000 10k files:
 * for x in `seq 1 2000`; do dd if=/dev/zero of=file$x.dat bs=1k seek=0 count=10240; done
 */
@State(Scope.Thread)
public class MmapPerf {

//    @Param({ "4096", "65536" })
    @Param({ "65536" })
    int bufferSize;

    @Param({ "false", "true" })
    boolean useDirectBuffer;

    @Param({ "/Users/szhou/repo/cassandra/1kfiles",
            "/Users/szhou/repo/cassandra/10kfiles",
            "/Users/szhou/repo/cassandra/100kfiles",
            "/Users/szhou/repo/cassandra/1mfiles",
            "/Users/szhou/repo/cassandra/10mfiles"})
    String filePath;

    @Setup
    public void setup() {
//        RandomAccessFile f = new RandomAccessFile("t", "rw");
//        f.setLength(1024 * 1024 * 1024);
    }

    @Benchmark
    public void readMapping() {
        File[] files = new File(filePath).listFiles();
        if (files == null) {
            throw new IllegalArgumentException("No test files");
        }

        for (File file : files) {
            try (FileChannel channel = new RandomAccessFile(file, "r").getChannel()) {

                int total = 0;
                while (total < file.length()) {
                    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, total, Math.min(bufferSize, file.length() - total));
                    total += buffer.limit();
                }

                assert total == file.length() : "size not match";
            } catch (IOException e) {
                System.err.println("Error when reading " + file);
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Benchmark
    public void readChannel() {
        File[] files = new File(filePath).listFiles();
        if (files == null) {
            throw new IllegalArgumentException("No test files");
        }

        for (File file : files) {
            try (FileChannel channel = new RandomAccessFile(file, "r").getChannel()) {
                int total = 0;
                while (total < file.length()) {
                    ByteBuffer buffer = useDirectBuffer ? ByteBuffer.allocateDirect(bufferSize) : ByteBuffer.allocate(bufferSize);
                    total += channel.read(buffer);
                }

                assert total == file.length() : "size not match";
            } catch (IOException e) {
                System.err.println("Error when reading " + file);
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MmapPerf.class.getSimpleName())
                // number of times the warmup iteration should take place
                .warmupIterations(1)
                //number of times the actual iteration should take place
                .measurementIterations(4)
                .forks(1)
                .shouldDoGC(true)
                .mode(Mode.AverageTime)
                .build();


        new Runner(opt).run();
    }
}
