package bq.provider;

import org.junit.jupiter.api.Test;

import bq.BqTest;
import bx.sql.duckdb.DuckTable;

public class MassiveProviderTest extends BqTest {

	
	@Test
	public void testIt() {
		
	
		
		MassiveProvider p = new MassiveProvider().dataSource(getDataSource());
		DuckTable t = p.forSymbol("GOOG").fetchIntoTable();
		
		t.prettyQuery().select();
		
		
		t.prettyQuery().select(c->c.sql("select * from "+t.getTableName()));
		
		
		
	
	}
}
