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
import java.util.Arrays;

/**
 * @author Developer
 */
public class ScalaDoc3Caller implements JavaMainCaller, Traceable {

  String classPath;

  @Override
  public void addJvmArgs(String... jvmArgs) {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("void", this, "addJvmArgs(String... args)");
    try {
      tracer.out().printfIndentln("jvmArgs = %s", Arrays.toString(jvmArgs));
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
        //        this.classPath = this.classPath.replace("\\", "/");
      }
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public void addOption(String key, File value) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
    // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void addOption(String key, boolean value) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
    // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void redirectToLog() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
    // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void run(boolean displayCmd) throws Exception {
    AbstractTracer tracer = getCurrentTracer();
    tracer.entry("boolean", this, "run(boolean displayCmd, boolean throwFailure)");
    try {
      tracer.out().printfIndentln("displayCmd = %b", displayCmd);

      ProcessBuilder processBuilder =
          new ProcessBuilder(
              "java",
              "-Djline.terminal=unix",
              "-classpath",
              String.format("\"%s\"", classPath),
              "-Dscala.usejavacp=true",
              "dotty.tools.scaladoc.Main",
              "-d",
              "./target/site/scaladocs",
              "./target/classes/");
      Path userDir = FileSystems.getDefault().getPath(".");
      File workingDir = userDir.toFile();
      File logFile = userDir.resolve("scaladoc.log").toFile();
      File errorFile = userDir.resolve("scaladoc-error.log").toFile();
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
