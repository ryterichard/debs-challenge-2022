package app.sources;

import app.datatypes.SymbolEvent;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class SymbolEventSource implements SourceFunction<SymbolEvent> {

    private final String dataFilePath;
    private final int servingSpeed;

    private transient BufferedReader reader;
    private transient InputStream gzipStream;

    /**
     * Serves the SymbolEvent records from the specified and ordered gzipped input files.
     * Events are served exactly in order of their time stamps
     * at the speed at which they were originally generated.
     *
     * @param dataFilePath The gzipped input path from which the JobEvent records are read.
     */
    public SymbolEventSource(String dataFilePath) {
        this(dataFilePath, 0, 1);
    }

    /**
     * Serves the SymbolEvent records from the specified and ordered gzipped input file path.
     * Events are served exactly in order of their time stamps
     * in a serving speed which is proportional to the specified serving speed factor.
     *
     * @param dataFilePath The gzipped input file path from which the JobEvent records are read.
     * @param servingSpeedFactor The serving speed factor by which the logical serving time is adjusted.
     */
    public SymbolEventSource(String dataFilePath, int servingSpeedFactor) {
        this(dataFilePath, 0, servingSpeedFactor);
    }

    /**
     * Serves the SymbolEvent records from the specified and ordered gzipped input file.
     * Events are served out-of time stamp order with specified maximum random delay
     * in a serving speed which is proportional to the specified serving speed factor.
     *
     * @param dataFilePath The gzipped input file from which the JobEvent records are read.
     * @param maxEventDelaySecs The max time in seconds by which events are delayed.
     * @param servingSpeedFactor The serving speed factor by which the logical serving time is adjusted.
     */
    public SymbolEventSource(String dataFilePath, int maxEventDelaySecs, int servingSpeedFactor) {
        if(maxEventDelaySecs < 0) {
            throw new IllegalArgumentException("Max event delay must be positive");
        }
        this.dataFilePath = dataFilePath;
        this.servingSpeed = servingSpeedFactor;
    }

    @Override
    public void run(SourceFunction.SourceContext<SymbolEvent> sourceContext) throws Exception {

        File directory = new File(dataFilePath);
        File[] files = directory.listFiles();
        assert files != null;
        Arrays.sort(files);
        for (File file : files) {

            FileInputStream fis = new FileInputStream(file);
            gzipStream = new GZIPInputStream(fis);
            reader = new BufferedReader(new InputStreamReader(gzipStream, StandardCharsets.UTF_8));


            String line;
            SymbolEvent event;

//            while (reader.ready() && (line = reader.readLine()) != null) {
//                event = SymbolEvent.fromString(line);
//                sourceContext.collectWithTimestamp(event, event.timeStamp);
//            }

            this.reader.close();
            this.reader = null;
            this.gzipStream.close();
            this.gzipStream = null;
        }

    }

    @Override
    public void cancel() {
        try {
            if (this.reader != null) {
                this.reader.close();
            }
            if (this.gzipStream != null) {
                this.gzipStream.close();
            }
        } catch(IOException ioe) {
            throw new RuntimeException("Could not cancel SourceFunction", ioe);
        } finally {
            this.reader = null;
            this.gzipStream = null;
        }
    }




}
