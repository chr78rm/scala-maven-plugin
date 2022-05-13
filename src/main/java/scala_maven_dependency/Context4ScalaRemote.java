/*
 * This is free and unencumbered software released into the public domain.
 * See UNLICENSE.
 */
package scala_maven_dependency;

import de.christofreichardt.diagnosis.AbstractTracer;
import de.christofreichardt.diagnosis.TracerFactory;
import java.util.Collections;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import scala_maven.MavenArtifactResolver;
import scala_maven.VersionNumber;

public class Context4ScalaRemote extends ContextBase implements Context {
  private final String scalaOrganization;
  private final MavenArtifactResolver mavenArtifactResolver;

  public Context4ScalaRemote(
      VersionNumber scalaVersion,
      VersionNumber scalaCompatVersion,
      ArtifactIds aids,
      String scalaOrganization,
      MavenArtifactResolver mavenArtifactResolver) {
    super(scalaVersion, scalaCompatVersion, aids);
    this.scalaOrganization = scalaOrganization;
    this.mavenArtifactResolver = mavenArtifactResolver;
  }

  @Override
  public boolean hasInDistro(Artifact artifact) throws Exception {
    return scalaOrganization.equalsIgnoreCase(artifact.getGroupId())
        && aids.scalaDistroArtifactIds().contains(artifact.getArtifactId());
  }

  @Override
  public Set<Artifact> findLibraryAndDependencies() throws Exception {
    return mavenArtifactResolver.getJarAndDependencies(
        scalaOrganization, aids.scalaLibraryArtifactId(), scalaVersion.toString(), null);
  }

  @Override
  public Set<Artifact> findCompilerAndDependencies() throws Exception {
    AbstractTracer tracer = TracerFactory.getInstance().getCurrentPoolTracer();
    tracer.entry("Set<Artifact>", this, "findCompilerAndDependencies()");
    try {
      tracer.out().printfIndentln("scalaOrganization = %s", scalaOrganization);
      tracer
          .out()
          .printfIndentln("aids.scalaCompilerArtifactId() = %s", aids.scalaCompilerArtifactId());
      tracer.out().printfIndentln("scalaVersion = %s", scalaVersion);

      Set<Artifact> compilerAndDependencies =
          mavenArtifactResolver.getJarAndDependencies(
              scalaOrganization, aids.scalaCompilerArtifactId(), scalaVersion.toString(), null);

      tracer.out().printfIndentln("compilerAndDependencies = %s", compilerAndDependencies);

      return compilerAndDependencies;
    } finally {
      tracer.wayout();
    }
  }

  @Override
  public Set<Artifact> findScalaDocAndDependencies() throws Exception {
    AbstractTracer tracer = TracerFactory.getInstance().getCurrentPoolTracer();
    tracer.entry("Set<Artifact>", this, "findScalaDocAndDependencies()");
    try {
      tracer.out().printfIndentln("scalaOrganization = %s", scalaOrganization);
      tracer
          .out()
          .printfIndentln("aids.scalaCompilerArtifactId() = %s", aids.scalaCompilerArtifactId());
      tracer.out().printfIndentln("scalaVersion = %s", scalaVersion);

      return Collections.emptySet();
    } finally {
      tracer.wayout();
    }
  }
}
