package bq;

import bq.ta4j.Bars;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator;
import org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator.ConvergenceDivergenceType;
import org.ta4j.core.num.Num;

public class PriceTableTest extends BqTest {

  @Test
  public void testIt() {

    PriceTable pt = PriceTable.from(loadBtcPriceData("btc"));

   // pt.getDuckTable().getJdbcClient().sql("delete from btc where date<'2025-01-15'").update();
    Assertions.assertThat(pt.getDuckTable()).isNotNull();

  
   // ema(foo(close),3)
   pt.addIndicator("ema",bs->{
	   return new EMAIndicator(new ClosePriceIndicator(bs), 3);
   });
    
   
   pt.getDuckTable().show();
  }
  
 
  
}
