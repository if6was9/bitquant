package bq.other;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import bx.sql.DbException;
import bx.sql.duckdb.DuckTable;
import bx.util.BxException;
import bx.util.Zones;

public class CandleWriter {

	DataSource ds;

	
	public CandleWriter dataSource(DataSource ds) {
		
		this.ds = ds;
		return this;
	}
	
	public JdbcClient getJdbcClient() {
		return JdbcClient.create(ds);
	}
	
	
	



}
