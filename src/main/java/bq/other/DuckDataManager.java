package bq.other;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import bq.BasicOHLCV;
import bq.OHLCV;
import bx.sql.DbException;
import bx.sql.Results;
import bx.sql.duckdb.DuckTable;
import bx.util.BxException;
import bx.util.Zones;

public class DuckDataManager {

	DataSource dataSource;
	
	public DuckDataManager dataSource(DataSource ds) {
		this.dataSource = ds;
		return this;
	}
	
	public JdbcClient getJdbcClient() {
		return JdbcClient.create(this.dataSource);
	}
	public DuckTable createOHLCV(String table) {
		String sql ="""
				CREATE TABLE %s (date date, open double null, high double null, low double null, close double null, volume double null)
				""";
		
		sql = String.format(sql, table);
		
		getJdbcClient().sql(sql).update();
		
		addOHLCVPrimaryKey(table);
		
		return DuckTable.of(this.dataSource, table);
		
	
		
	}
	
	public void addOHLCVPrimaryKey(String table) {
		String sql = String.format("ALTER TABLE %s ADD PRIMARY KEY(%s)",table,"date");
		
		getJdbcClient().sql(sql).update();
		
	}
	
	static class OHLCVRowMapper implements RowMapper<OHLCV> {

			@Override
			public OHLCV mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Results r = Results.create(rs);
				
				LocalDate date = r.getLocalDate("date").get();
				r.getDouble("open");
				r.getDouble("high");
				r.getDouble("low");
				r.getDouble("close");
				r.getDouble("volume");
				
				
				var ohlcv = BasicOHLCV.of(date.atStartOfDay(Zones.UTC).toInstant(),
						r.getBigDecimal("open").orElse(null),
						r.getBigDecimal("high").orElse(null),
						r.getBigDecimal("low").orElse(null),
						r.getBigDecimal("close").orElse(null),
						r.getBigDecimal("volume").orElse(null)
						
						
						);
			
				
				
				return ohlcv;
			}
	}
	
	
	public List<OHLCV> selectAll(String table) {
		
		return getJdbcClient().sql(String.format("select date,open,high,low,close,volume from %s order by date asc",table)).query(new OHLCVRowMapper()).list();
	
	}
	
	public void insert(String table, Iterable<OHLCV> data) {

		DuckTable t = DuckTable.of(dataSource, table);

		var appender = t.createAppender();
		try {

			for (OHLCV candle : data) {

				appender.beginRow();

				appender.append(candle.getTimestamp().atZone(Zones.UTC).toLocalDate());
				appender.append((Double) ((candle.getOpen().isPresent()) ? candle.getOpen().get().doubleValue() : null));
				appender.append((Double) ((candle.getHigh().isPresent()) ? candle.getHigh().get().doubleValue() : null));
				appender.append((Double) ((candle.getLow().isPresent()) ? candle.getLow().get().doubleValue() : null));
				appender.append((Double) ((candle.getClose().isPresent()) ? candle.getClose().get().doubleValue() : null));
				appender.append((Double) ((candle.getVolume().isPresent()) ? candle.getVolume().get().doubleValue() : null));
	
				appender.endRow();

			}

		}

		catch (SQLException e) {
			throw new DbException(e);
		} finally {
			try {
				appender.close();
			} catch (SQLException e) {
				throw new BxException(e);
			}
		}

	}
}
