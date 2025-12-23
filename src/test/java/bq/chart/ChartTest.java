package bq.chart;

import org.junit.jupiter.api.Test;

import bq.BqTest;
import bq.chart.Chart;
import bq.ta4j.Bars;

public class ChartTest extends BqTest {

  @Test
  public void testIt() {

 
    var bs = Bars.toBarSeries( getSampleGOOG().stream());
    
    Chart.newChart()
        .title("foo")
        .trace(
            "test",
            c -> {
              c.addData(bs);
            })
        .view();
  }
}
