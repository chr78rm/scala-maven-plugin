/*
 * This is free and unencumbered software released into the public domain.
 * See UNLICENSE.
 */
package scala_maven_executions;

import de.christofreichardt.diagnosis.AbstractTracer;
import de.christofreichardt.diagnosis.Traceable;
import de.christofreichardt.diagnosis.TracerFactory;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;

/**
 * @author Developer
 */
public class ScalaDoc3Caller implements JavaMainCaller, Traceable {

  String classPath;
  final List<String> jvmArgs = new ArrayList<>();
  final String apidocMainClassName;
  final AbstractMojo requester;
  String outputPath =
      FileSystems.getDefault()
          .getPath(".", "target", "site", "scaladocs")
          .toAbsolutePath()
          .toString();
  final List<String> args = new ArrayList<>();
  final String targetClassesDir;

  public ScalaDoc3Caller(AbstractMojo mojo, String apidocMainClassName, String targetClassesDir) {
    this.requester = mojo;
    this.apidocMainClassName = apidocMainClassName;
    this.targetClassesDir = targetClassesDir;
  }

  @Override
  public void addJvmArgs(String... jvmArgs) {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("void", this, "addJvmArgs(String... args)");
    try {
      tracer.out().printfIndentln("jvmArgs = %s", Arrays.toString(jvmArgs));
      this.jvmArgs.addAll(Arrays.asList(jvmArgs));
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public void addArgs(String... args) {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("void", this, "addArgs(String... args)");
    try {
      tracer.out().printfIndentln("args = %s", Arrays.toString(args));

      Map<String, String> compatMap = new HashMap<>();
      compatMap.put("-doc-footer", "-project-footer");
      compatMap.put("-doc-title", "-project");
      compatMap.put("-doc-version", "-project-version");
      String[] supportedOptions = {"-project-footer", "-project", "-project-version"};

      if (args != null) {
        List<String> migratedArgs =
            Arrays.stream(args)
                .map(arg -> compatMap.containsKey(arg) ? compatMap.get(arg) : arg)
                .collect(Collectors.toList());
        if (Arrays.asList(supportedOptions).contains(migratedArgs.get(0))) {
          this.args.addAll(migratedArgs);
        } else {
          this.requester
              .getLog()
              .warn(String.format("Unsupported option '%s' has been ignored.", args[0]));
        }
      }
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public void addOption(String key, String value) {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("void", this, "addOption(String key, String value)");
    try {
      tracer.out().printfIndentln("%s = %s", key, value);
      if (key.equals("-classpath")) {
        this.classPath = value;
      } else if (key.equals("-d")) {
        this.outputPath = value;
      }
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public void addOption(String key, File value) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
  }

  @Override
  public void addOption(String key, boolean value) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
  }

  @Override
  public void redirectToLog() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
  }

  @Override
  public void run(boolean displayCmd) throws Exception {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("boolean", this, "run(boolean displayCmd, boolean throwFailure)");
    try {
      tracer.out().printfIndentln("displayCmd = %b", displayCmd);
      tracer.out().printfIndentln("this.jvmArgs = %s", this.jvmArgs);
      tracer.out().printfIndentln("this.args = %s", this.args);

      List<String> commands = new ArrayList<>();
      commands.add("java");
      commands.addAll(this.jvmArgs);
      commands.add("-classpath");
      commands.add(this.classPath);
      commands.add("-Dscala.usejavacp=true");
      commands.add(this.apidocMainClassName);
      commands.addAll(this.args);
      commands.add("-d");
      commands.add(this.outputPath);
      commands.add(this.targetClassesDir);

      tracer.out().printfIndentln("commands = %s", commands);

      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      Path userDir = FileSystems.getDefault().getPath(".");
      File workingDir = userDir.toFile();
      File logFile = userDir.resolve("scaladoc.log").toFile();
      Process process =
          processBuilder
              .directory(workingDir)
              .redirectOutput(ProcessBuilder.Redirect.to(logFile))
              .redirectError(ProcessBuilder.Redirect.to(logFile))
              .start();
      int exitValue = process.waitFor();
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public boolean run(boolean displayCmd, boolean throwFailure) throws Exception {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("boolean", this, "run(boolean displayCmd, boolean throwFailure)");
    try {
      tracer.out().printfIndentln("displayCmd = %b", displayCmd);
      tracer.out().printfIndentln("throwFailure = %b", throwFailure);

      ProcessBuilder processBuilder =
          new ProcessBuilder("scaladoc", "-d", "target/site/scaladocs", "src/main/scala");
      Path userDir = FileSystems.getDefault().getPath(".");
      File workingDir = userDir.toFile();
      File logFile = userDir.resolve("scaladoc.log").toFile();
      Process process =
          processBuilder
              .directory(workingDir)
              .redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))
              .start();
      int exitValue = process.waitFor();

      return exitValue == 0;
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public SpawnMonitor spawn(boolean displayCmd) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
    // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public AbstractTracer getCurrentTracer() {
    return TracerFactory.getInstance().getCurrentPoolTracer();
  }
}
