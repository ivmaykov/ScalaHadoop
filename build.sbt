import AssemblyKeys._

name := "ScalaHadoop"

scalaVersion := "2.9.1"

// sbt defaults to <project>/src/test/{scala,java} unless we put this in
// unmanagedSourceDirectories in Test <<= Seq( baseDirectory( _ / "test" ) ).join

unmanagedSourceDirectories in Compile <<= Seq( baseDirectory( _ / "src" ) ).join

unmanagedJars in Compile <++= baseDirectory map { base =>
   val baseDirectories = file(System.getenv("HADOOP_HOME"))
   val customJars = (baseDirectories ** "*.jar")
   customJars.classpath
}

// This is to prevent error [java.lang.OutOfMemoryError: PermGen space]
javaOptions += "-XX:MaxPermSize=256m"

javaOptions += "-Xmx1024m"

// For the sbt-assembly plugin to be able to generate single JAR files for easy deploys
assemblySettings

jarName in assembly := "ScalaHadoop.jar"

// This is so that sbt-assembly will exclude the Hadoop jars.
dependencyClasspath in assembly <<= (dependencyClasspath in assembly, baseDirectory) map { (deps, base) =>
  val compile = (file(System.getenv("HADOOP_HOME")) ** "*.jar").get
  deps filter { d => !(compile contains d.data) }
}

fullClasspath in assembly <<= (fullClasspath in assembly, baseDirectory) map { (cp, base) =>
  val compile = (file(System.getenv("HADOOP_HOME")) ** "*.jar").get
  cp filter { d => !(compile contains d.data) }
}

// Avoid packaging the scala library jar in the assembly
//publishArtifact in (Assembly, packageScala) := false