package bq;

import bx.util.Slogger;
import java.io.IOException;
import org.slf4j.Logger;

public class Main {

  static Logger logger = Slogger.forEnclosingClass();

  public static void main(String[] args) throws IOException {

    logger.atInfo().log("Hello!");
  }
}
