package bq.shell;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.jshell.JShell;

public class Shell {

  private Shell() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) throws IOException {
    JShell shell = JShell.builder().executionEngine("local").build();

    String init =
        Files.asCharSource(new File("./src/main/resources/bqsh/init.jsh"), StandardCharsets.UTF_8)
            .read();

    shell.eval(init);

    if (args.length != 1) {
      usage();
    }
    File scriptFile = new File(args[0]);

    String script = Files.asCharSource(scriptFile, StandardCharsets.UTF_8).read();

    shell.eval(script);
  }

  static void usage() {
    System.err.println("Usage: Shell <jsh script>");
    System.exit(1);
  }

  public void test() {

    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("jshell");

      String script =
          """
          var a =1;
          var b =2;

          """;

      // result is the return value or last variable set
      Object result = engine.eval(script);
      System.out.println("Result: " + result);

    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }
}
