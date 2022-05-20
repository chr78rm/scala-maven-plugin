/*
 * This is free and unencumbered software released into the public domain.
 * See UNLICENSE.
 */
package scala_maven_executions;

import de.christofreichardt.diagnosis.AbstractTracer;
import de.christofreichardt.diagnosis.Traceable;
import de.christofreichardt.diagnosis.TracerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Chr. Reichardt
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
      if (Objects.nonNull(jvmArgs)) {
        this.jvmArgs.addAll(Arrays.asList(jvmArgs));
      }
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
      compatMap.put("-doc-source-url", "-source-links");

      if (Objects.nonNull(args)) {
        if (Objects.equals(args[0], "-doc-format:html")) {
          return;
        }
        List<String> migratedArgs =
            Arrays.stream(args)
                .map(arg -> compatMap.containsKey(arg) ? compatMap.get(arg) : arg)
                .collect(Collectors.toList());
        this.args.addAll(migratedArgs);
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
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addOption(String key, boolean value) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void redirectToLog() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void run(boolean displayCmd) throws Exception {
    run(displayCmd, true);
  }

  @Override
  public boolean run(boolean displayCmd, boolean throwFailure) throws MojoFailureException {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("boolean", this, "run(boolean displayCmd, boolean throwFailure)");
    try {
      tracer.out().printfIndentln("displayCmd = %b", displayCmd);
      tracer.out().printfIndentln("throwFailure = %b", throwFailure);
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

      if (displayCmd) {
        String cmd = commands.stream().collect(Collectors.joining(" "));
        this.requester.getLog().info(String.format("cmd: %s", cmd));
      }

      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      Path userDir = FileSystems.getDefault().getPath(".");
      File workingDir = userDir.toFile();
      Process process;
      try {
        process =
            processBuilder
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
      } catch (IOException ex) {
        throw new MojoFailureException(ex.getMessage(), ex);
      }
      try {
        int exitValue = process.waitFor();
        if (exitValue != 0) {
          throw new MojoFailureException(
              String.format("scaladoc_3 returned non-zero value: %d", exitValue));
        }
      } catch (InterruptedException ex) {
        throw new MojoFailureException(ex.getMessage(), ex);
      }

      return true;
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public SpawnMonitor spawn(boolean displayCmd) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public AbstractTracer getCurrentTracer() {
    return TracerFactory.getInstance().getCurrentPoolTracer();
  }
}
