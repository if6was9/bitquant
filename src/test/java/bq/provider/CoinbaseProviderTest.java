package bq.provider;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import bq.OHLCV;
import bq.other.CandleWriter;
import bq.other.OldLoader;
import bq.provider.CoinbaseDataProvider;
import bx.sql.duckdb.DuckDataSource;
import bx.sql.duckdb.DuckS3Extension;
import bx.sql.duckdb.DuckTable;
import bx.util.Zones;
import software.amazon.awssdk.services.s3.S3Client;

public class CoinbaseProviderTest {

	

	@Test
	void testId() {

		CoinbaseDataProvider cb = new CoinbaseDataProvider();
		
		cb.newRequest().from(LocalDate.of(2025, 12, 1)).to(LocalDate.of(2025, 12, 7)).stream().toList().forEach(it->{
			System.out.println(it);
		});
		
		
	
	}
	
	@Test
	public void testNullTo() {
		CoinbaseDataProvider cb = new CoinbaseDataProvider();
		
		var candles = cb.newRequest().from(LocalDate.now(Zones.UTC).minusDays(3)).to(null).stream().toList();
		
		candles.forEach(it->{
			System.out.println(it);
		});
		Assertions.assertThat(candles).hasSize(4);
		Assertions.assertThat(candles.getLast().getDate()).isEqualTo(LocalDate.now(Zones.UTC));
		Assertions.assertThat(candles.get(candles.size()-1).getDate()).isEqualTo(LocalDate.now(Zones.UTC));
		Assertions.assertThat(candles.get(candles.size()-2).getDate()).isEqualTo(cb.getLastClosedTradingDay());
	}
	
	@Test
	void testSingle() {

		CoinbaseDataProvider cb = new CoinbaseDataProvider();
		
		var list = cb.newRequest().from(LocalDate.of(2025, 12, 1)).to(LocalDate.of(2025, 12, 1)).stream().toList();
		Assertions.assertThat(list).hasSize(1);
		Assertions.assertThat(list.getFirst().getDate()).hasDayOfMonth(1);
		Assertions.assertThat(list.getFirst().getDate()).hasMonthValue(12);
		
		Assertions.assertThat(list.getFirst().getDate()).hasYear(2025);
	}
	
	@Test
	public void testSymbol() {
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("BTC")).isEqualTo("BTC-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("btc")).isEqualTo("BTC-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("BTC_USD")).isEqualTo("BTC-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("BTC/USD")).isEqualTo("BTC-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("X:BTC")).isEqualTo("BTC-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("BTC/EUR")).isEqualTo("BTC-EUR");
		
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("ETH")).isEqualTo("ETH-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("ETH")).isEqualTo("ETH-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("ETH_USD")).isEqualTo("ETH-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("ETH/USD")).isEqualTo("ETH-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("X:ETH")).isEqualTo("ETH-USD");
		Assertions.assertThat(CoinbaseDataProvider.toCoinbaseSymbol("ETH/EUR")).isEqualTo("ETH-EUR");
	}
}
