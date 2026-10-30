/*
 * Copyright 2026 The Closure Rules Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CompilerExtensionsTest {
  private static final String SERVICE = "META-INF/services/" + CompilerFactory.class.getName();
  private static final String SOURCE =
      "/** @define {string} */ var extensionValue = 'default'; console.log(extensionValue);\n";

  @Rule public final TemporaryFolder temporary = new TemporaryFolder();
  private Path source;
  private Path output;
  private Path configuration;
  private Path report;

  @Before
  public void setUp() throws IOException {
    Path root = temporary.getRoot().toPath();
    source = root.resolve("input.js");
    output = root.resolve("output.js");
    configuration = root.resolve("compiler.config");
    report = root.resolve("compiler.report");
    Files.writeString(source, SOURCE);
    Files.writeString(configuration, "configured\n");
  }

  @Test
  public void configuredCompilerRunsBeforeCompletionAndEachInvocationReadsItsInput() throws Exception {
    try (AutoCloseable ignored = services(TestCompilerFactory.class)) {
      JsCompiler program = new JsCompiler();
      assertEquals(0, program.apply(arguments(extensionArguments())).intValue());
      assertEquals("console.log(\"configured\");\n", Files.readString(output));
      assertEquals("configured\n" + Files.readString(output), Files.readString(report));

      Files.writeString(configuration, "next invocation\n");
      assertEquals(0, program.apply(arguments(extensionArguments())).intValue());
      assertEquals("console.log(\"next invocation\");\n", Files.readString(output));
      assertEquals("next invocation\n" + Files.readString(output), Files.readString(report));
    }
  }

  @Test
  public void absentConfigurationUsesStockCompilerWithoutLoadingServices() throws Exception {
    try (AutoCloseable ignored = services(TestCompilerFactory.class, AnotherFactory.class)) {
      assertEquals(0, new JsCompiler().apply(arguments()).intValue());
      assertEquals("console.log(\"default\");\n", Files.readString(output));
      assertFalse(Files.exists(report));
    }
  }

  @Test
  public void configurationAndReportMustBePaired() {
    assertIOException(
        "compiler_config and compiler_report must be supplied together",
        "--compiler_config", configuration.toString());
    assertIOException(
        "compiler_config and compiler_report must be supplied together",
        "--compiler_report", report.toString());
  }

  @Test
  public void configuredCompilationRequiresAFactory() throws Exception {
    try (AutoCloseable ignored = services()) {
      assertIOException("No CompilerFactory is available for compiler_config", extensionArguments());
    }
  }

  @Test
  public void configuredCompilationRejectsMultipleFactories() throws Exception {
    try (AutoCloseable ignored = services(TestCompilerFactory.class, AnotherFactory.class)) {
      assertIOException("Multiple CompilerFactory implementations are available", extensionArguments());
      assertFalse(Files.exists(report));
    }
  }

  @Test
  public void compilationFailureSkipsCompletion() throws Exception {
    Files.writeString(source, "var broken = ;\n");
    try (AutoCloseable ignored = services(TestCompilerFactory.class)) {
      assertEquals(1, new JsCompiler().apply(arguments(extensionArguments())).intValue());
      assertFalse(Files.exists(report));
    }
  }

  @Test
  public void checksOnlySkipsCompletion() throws Exception {
    try (AutoCloseable ignored = services(TestCompilerFactory.class)) {
      for (String flag : List.of("--checks_only", "--checks-only")) {
        List<List<String>> forms = new ArrayList<>();
        forms.add(List.of(flag));
        for (String value : List.of("true", "on", "yes", "1")) {
          forms.add(List.of(flag + "=" + value));
          forms.add(List.of(flag, value));
        }
        for (List<String> form : forms) {
          Files.deleteIfExists(output);
          List<String> flags = new ArrayList<>(Arrays.asList(extensionArguments()));
          flags.addAll(form);
          assertEquals(form.toString(), 0,
              new JsCompiler().apply(arguments(flags.toArray(new String[0]))).intValue());
          assertEquals(form.toString(), "", Files.readString(output));
          assertFalse(form.toString(), Files.exists(report));
        }
      }
    }
  }

  @Test
  public void explicitFalseAfterChecksOnlyRunsCompletion() throws Exception {
    List<String> flags = new ArrayList<>(Arrays.asList(extensionArguments()));
    flags.addAll(List.of("--checks_only", "--checks-only=false"));
    try (AutoCloseable ignored = services(TestCompilerFactory.class)) {
      assertEquals(0, new JsCompiler().apply(arguments(flags.toArray(new String[0]))).intValue());
      assertEquals("configured\n" + Files.readString(output), Files.readString(report));
    }
  }

  @Test
  public void configurationReadFailuresPropagate() throws Exception {
    Files.delete(configuration);
    try (AutoCloseable ignored = services(TestCompilerFactory.class)) {
      assertIOException(configuration.toString(), extensionArguments());
      assertFalse(Files.exists(report));
    }
  }

  @Test
  public void completionFailuresPropagateAfterJavaScriptIsWritten() throws Exception {
    try (AutoCloseable ignored = services(RejectingCompletionFactory.class)) {
      assertIOException("Extension completion failed", extensionArguments());
      assertTrue(Files.exists(output));
      assertFalse(Files.exists(report));
    }
  }

  private String[] extensionArguments() {
    return new String[] {
      "--compiler_config", configuration.toString(), "--compiler_report", report.toString()
    };
  }

  private List<String> arguments(String... extra) {
    Path relativeSource = Path.of("").toAbsolutePath().relativize(source.toAbsolutePath());
    List<String> args = new ArrayList<>(Arrays.asList(
        "--js", relativeSource.toString(),
        "--js_output_file", output.toString(),
        "--compilation_level", "ADVANCED"));
    args.addAll(Arrays.asList(extra));
    return args;
  }

  private void assertIOException(String message, String... flags) {
    try {
      new JsCompiler().apply(arguments(flags));
      fail("Expected an IOException");
    } catch (RuntimeException exception) {
      assertTrue(exception.getCause() instanceof IOException);
      assertEquals(message, exception.getCause().getMessage());
    }
  }

  private AutoCloseable services(Class<?>... factories) throws IOException {
    Path root = temporary.newFolder().toPath();
    if (factories.length > 0) {
      Path descriptor = root.resolve(SERVICE);
      Files.createDirectories(descriptor.getParent());
      List<String> names = new ArrayList<>();
      for (Class<?> factory : factories) {
        names.add(factory.getName());
      }
      Files.write(descriptor, names);
    }
    Thread thread = Thread.currentThread();
    ClassLoader previous = thread.getContextClassLoader();
    URLClassLoader loader = new URLClassLoader(new URL[] {root.toUri().toURL()}, previous) {
      @Override
      public Enumeration<URL> getResources(String name) throws IOException {
        return name.equals(SERVICE) ? findResources(name) : super.getResources(name);
      }
    };
    thread.setContextClassLoader(loader);
    return () -> {
      thread.setContextClassLoader(previous);
      loader.close();
    };
  }

  public static final class AnotherFactory implements CompilerFactory {
    @Override
    public Compiler create(Path configuration, Path report) {
      return new Compiler();
    }
  }

  public static final class RejectingCompletionFactory implements CompilerFactory {
    @Override
    public Compiler create(Path configuration, Path report) {
      return new Compiler();
    }

    @Override
    public void finish(Compiler compiler, Path javascript) throws IOException {
      throw new IOException("Extension completion failed");
    }
  }
}
