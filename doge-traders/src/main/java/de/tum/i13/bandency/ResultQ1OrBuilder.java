// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: challenger.proto

package de.tum.i13.bandency;

public interface ResultQ1OrBuilder extends
    // @@protoc_insertion_point(interface_extends:Challenger.ResultQ1)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 benchmark_id = 1;</code>
   * @return The benchmarkId.
   */
  long getBenchmarkId();

  /**
   * <code>int64 batch_seq_id = 2;</code>
   * @return The batchSeqId.
   */
  long getBatchSeqId();

  /**
   * <code>repeated .Challenger.Indicator indicators = 3;</code>
   */
  java.util.List<de.tum.i13.bandency.Indicator> 
      getIndicatorsList();
  /**
   * <code>repeated .Challenger.Indicator indicators = 3;</code>
   */
  de.tum.i13.bandency.Indicator getIndicators(int index);
  /**
   * <code>repeated .Challenger.Indicator indicators = 3;</code>
   */
  int getIndicatorsCount();
  /**
   * <code>repeated .Challenger.Indicator indicators = 3;</code>
   */
  java.util.List<? extends de.tum.i13.bandency.IndicatorOrBuilder> 
      getIndicatorsOrBuilderList();
  /**
   * <code>repeated .Challenger.Indicator indicators = 3;</code>
   */
  de.tum.i13.bandency.IndicatorOrBuilder getIndicatorsOrBuilder(
      int index);
}
