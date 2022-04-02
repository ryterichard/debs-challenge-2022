package app.datatypes;

import de.tum.i13.bandency.CrossoverEvent;
import de.tum.i13.bandency.Indicator;

import java.util.List;

public class BatchResult {

    private Long batchId;
    private Long benchmarkId;
    private List<Indicator> indicatorList;
    private List<CrossoverEvent> crossoverEventList;

    public BatchResult(Long batchId, Long benchmarkId, List<Indicator> indicatorList, List<CrossoverEvent> crossoverEventList) {
        this.batchId = batchId;
        this.benchmarkId = benchmarkId;
        this.indicatorList = indicatorList;
        this.crossoverEventList = crossoverEventList;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getBenchmarkId() {
        return benchmarkId;
    }

    public void setBenchmarkId(Long benchmarkId) {
        this.benchmarkId = benchmarkId;
    }

    public List<Indicator> getIndicatorList() {
        return indicatorList;
    }

    public void setIndicatorList(List<Indicator> indicatorList) {
        this.indicatorList = indicatorList;
    }

    public List<CrossoverEvent> getCrossoverEventList() {
        return crossoverEventList;
    }

    public void setCrossoverEventList(List<CrossoverEvent> crossoverEventList) {
        this.crossoverEventList = crossoverEventList;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "batchId=" + batchId +
                ", benchmarkId=" + benchmarkId +
                ", indicatorList=" + indicatorList +
                ", crossoverEventList=" + crossoverEventList +
                '}';
    }
}
