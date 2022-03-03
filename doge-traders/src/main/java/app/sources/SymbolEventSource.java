package app.sources;

import app.datatypes.SymbolEvent;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class SymbolEventSource implements SourceFunction<SymbolEvent> {
    private final int maxDelayMsecs;
    private final int watermarkDelayMSecs;

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
        this.maxDelayMsecs = maxEventDelaySecs * 1000;
        this.watermarkDelayMSecs = Math.max(maxDelayMsecs, 10000);
        this.servingSpeed = servingSpeedFactor;
    }

    @Override
    public void run(SourceFunction.SourceContext<SymbolEvent> sourceContext) throws Exception {

        File directory = new File(dataFilePath);
        File[] files = directory.listFiles();
        Arrays.sort(files);
        for (File file : files) {

            FileInputStream fis = new FileInputStream(file);
            gzipStream = new GZIPInputStream(fis);
            reader = new BufferedReader(new InputStreamReader(gzipStream, "UTF-8"));

            generateUnorderedStream(sourceContext);

            this.reader.close();
            this.reader = null;
            this.gzipStream.close();
            this.gzipStream = null;
        }

    }

    private void generateUnorderedStream(SourceFunction.SourceContext<SymbolEvent> sourceContext) throws Exception {

        long servingStartTime = Calendar.getInstance().getTimeInMillis();
        long dataStartTime;

        Random rand = new Random(7452);
        PriorityQueue<Tuple2<Long, Object>> emitSchedule = new PriorityQueue<>(
                32,
                new Comparator<Tuple2<Long, Object>>() {
                    @Override
                    public int compare(Tuple2<Long, Object> o1, Tuple2<Long, Object> o2) {
                        return o1.f0.compareTo(o2.f0);
                    }
                });

        // read first event and insert it into emit schedule
        String line;
        SymbolEvent event;
        if (reader.ready() && (line = reader.readLine()) != null) {
            // read first event
            event = SymbolEvent.fromString(line);
            // extract starting timestamp
            dataStartTime = event.timeStamp / 1000;
            // get delayed time
            long delayedEventTime = dataStartTime + getNormalDelayMsecs(rand);

            emitSchedule.add(new Tuple2<Long, Object>(delayedEventTime, event));
            // schedule next watermark
            long watermarkTime = dataStartTime + watermarkDelayMSecs;
            Watermark nextWatermark = new Watermark(watermarkTime - maxDelayMsecs - 1);
            emitSchedule.add(new Tuple2<Long, Object>(watermarkTime, nextWatermark));

        } else {
            return;
        }

        // peek at next event
        if (reader.ready() && (line = reader.readLine()) != null) {
            event = SymbolEvent.fromString(line);
        }

        boolean done = false;

        // read events one-by-one and emit a random event from the buffer each time
        while ( (emitSchedule.size() > 0 || (reader.ready())) && !done ) {

            // insert all events into schedule that might be emitted next
            long curNextDelayedEventTime = !emitSchedule.isEmpty() ? emitSchedule.peek().f0 : -1;
            long jobEventTime = (event != null) ? event.timeStamp / 1000 : -1;
            while(
                    event != null && ( // while there is an event AND
                            emitSchedule.isEmpty() || // and no event in schedule OR
                                    jobEventTime < curNextDelayedEventTime + maxDelayMsecs) // not enough events in schedule
            )
            {
                // insert event into emit schedule
                long delayedEventTime = jobEventTime + getNormalDelayMsecs(rand);
                emitSchedule.add(new Tuple2<Long, Object>(delayedEventTime, event));

                // read next job event
                if (reader.ready() && (line = reader.readLine()) != null) {
                    event = SymbolEvent.fromString(line);
                    jobEventTime = event.timeStamp / 1000;
                }
                else {
                    event = null;
                    jobEventTime = -1;
                    done = true;
                }
            }

            // emit schedule is updated, emit next element in schedule
            Tuple2<Long, Object> head = emitSchedule.poll();
            long delayedEventTime = head.f0;

            long now = Calendar.getInstance().getTimeInMillis();
            long servingTime = toServingTime(servingStartTime, dataStartTime, delayedEventTime);
            long waitTime = servingTime - now;

            Thread.sleep( (waitTime > 0) ? waitTime : 0);

            if(head.f1 instanceof SymbolEvent) {
                SymbolEvent emitEvent = (SymbolEvent) head.f1;
                // emit event
                sourceContext.collectWithTimestamp(emitEvent, emitEvent.timeStamp / 1000);
            }
            else if(head.f1 instanceof Watermark) {
                Watermark emitWatermark = (Watermark)head.f1;
                // emit watermark
                sourceContext.emitWatermark(emitWatermark);
                // schedule next watermark
                long watermarkTime = delayedEventTime + watermarkDelayMSecs;
                Watermark nextWatermark = new Watermark(watermarkTime - maxDelayMsecs - 1);
                emitSchedule.add(new Tuple2<Long, Object>(watermarkTime, nextWatermark));
            }
        }

        // Reached the end of the file: flush the contents of the emitSchedule
        while (emitSchedule.size() > 0) {
            Tuple2<Long, Object> head = emitSchedule.poll();

            if(head.f1 instanceof SymbolEvent) {
                SymbolEvent emitEvent = (SymbolEvent) head.f1;
                // emit event
                sourceContext.collectWithTimestamp(emitEvent, emitEvent.timeStamp / 1000);
            }
            else if(head.f1 instanceof Watermark) {
                Watermark emitWatermark = (Watermark) head.f1;
                // emit watermark
                sourceContext.emitWatermark(emitWatermark);
            }
        }
    }

    public long toServingTime(long servingStartTime, long dataStartTime, long eventTime) {
        long dataDiff = eventTime - dataStartTime;
        return servingStartTime + (dataDiff / this.servingSpeed);
    }

    public long getNormalDelayMsecs(Random rand) {
        long delay = -1;
        long x = maxDelayMsecs / 2;
        while(delay < 0 || delay > maxDelayMsecs) {
            delay = (long)(rand.nextGaussian() * x) + x;
        }
        return delay;
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
