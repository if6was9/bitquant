package bq.provider;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

import bq.BasicOHLCV;
import bq.other.DuckDataManager;
import bx.sql.duckdb.DuckDataSource;

public class DuckDataManagerTest {

	
	DuckDataSource ds;
	JdbcClient client;
	
	
	@BeforeEach
	public void setup() {
		ds = DuckDataSource.createInMemory();
		client = JdbcClient.create(ds);
		
	}
	
	@AfterEach
	public void cleanup() {
		ds.close();
	}
	
	
	@Test
	@Disabled
	public void testInsert() {
		var ohlcv = BasicOHLCV.of(LocalDate.of(2025, 11,11),10.0,11.0,6.0,9.0,100.0);
		
		DuckDataManager ddm = new DuckDataManager().dataSource(this.ds);
		ddm.insert("test",List.of(ohlcv));
		
	}
	@Test
	public void testIt() {
		String sql = String.format("create table btc as (select date,open,high,low,close,volume from '%s' order by date)",new File("src/test/resources/btc-price-data.csv").toString());
		int count = client.sql(sql).update();
		
		DuckDataManager ddm = new DuckDataManager().dataSource(this.ds);
		
		ddm.selectAll("btc").forEach(it->{
			System.out.println(it);
		});
		
	}
}
