package bq.other;

import bq.DataManager;
import bq.OHLCV;
import bx.sql.duckdb.DuckS3Extension;
import bx.sql.duckdb.DuckTable;
import bx.util.Slogger;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.springframework.jdbc.core.simple.JdbcClient;
import software.amazon.awssdk.services.s3.S3Client;

public class OldLoader {

  Logger logger = Slogger.forEnclosingClass();

  DataSource dataSource;
  JdbcClient jdbcClient;

  S3Client s3;

  public OldLoader s3Client(S3Client client) {
    this.s3 = client;
    return this;
  }

  public OldLoader dataSource(DataSource ds) {
    this.dataSource = ds;
    this.jdbcClient = JdbcClient.create(ds);
    return this;
  }

  public String toS3Url(String symbol) {
    return String.format("s3://%s/%s", "bqdat", toS3Key(symbol));
  }

  public String toS3Key(String symbol) {
    symbol = symbol.toLowerCase().replace("/", "").replace(":", "").replace("-", "");
    return String.format("crypto/daily/%s.csv.gz", symbol);
  }

  public void saveS3(String symbol, String table) {

    String s3Url = String.format("s3://%s/%s", "bqdat", toS3Key(table));
    String sql =
        String.format(
            "copy (select date,open,high,low,close,volume from %s order by date asc) to '%s'",
            table, s3Url);

    DuckS3Extension.load(jdbcClient).useCredentialChain();

    int x = jdbcClient.sql(sql).update();
    System.out.println(sql);
    System.out.println(x);
  }

  // DuckTable createOHLCV(String tableName) {
  // return new CandleWriter().dataSource(dataSource).createOHLCV(tableName);
  // }

  public String symbolToProduct(String symbol) {
    return symbol.trim().toUpperCase() + "-USD";
  }

  public void sync(String symbol) {

    DataManager ddm = new DataManager().dataSource(dataSource);
    String tableName = symbol;
    var table = DuckTable.of(dataSource, symbol);

    if (!table.exists()) {

      logger.atInfo().log("creating {}", tableName);
      table = ddm.createOHLCV(tableName, true);
    }

    String sql = String.format("select date from %s order by date desc limit 1", tableName);

    Optional<Object> val = jdbcClient.sql(sql).query().optionalValue();

    DuckTable tempTable = DuckTable.of(dataSource, "temp_" + System.currentTimeMillis());
    tempTable.drop();
    if (val.isEmpty()) {
      logger.atInfo().log("load everything....");

      Stream<OHLCV> data = Stream.of(); // (symbolToProduct(symbol));

      ddm.createOHLCV(tempTable.getTableName(), true);
      ddm.insert(tempTable.getTableName(), data.toList());

      sql =
          String.format(
              "insert into %s (select * from %s where date not in (select date from %s))",
              table.getTableName(), tempTable.getTableName(), table.getTableName());
      jdbcClient.sql(sql).update();

    } else {
      logger.atInfo().log("loading incremental " + val);
    }

    tempTable.drop();
  }

  public void fetchS3(String symbol, String table) {

    String sql =
        String.format(
            "CREATE TABLE %s as (select * from '%s' order by date)", table, toS3Url(symbol));

    jdbcClient.sql(sql).update();

    System.out.println(sql);
  }

  public void mergeOHLCV(String target, String source) {
    String sql =
        String.format(
            "insert into %s (select * from %s where date not in (select date from btc))",
            target, source, target);
    jdbcClient.sql(sql).update();
  }
}
