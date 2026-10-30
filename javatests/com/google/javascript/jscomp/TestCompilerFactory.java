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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Compiler extension used by the Java lifecycle tests and a real Bazel worker. */
public final class TestCompilerFactory implements CompilerFactory {
  private Compiler created;
  private Path report;
  private String value;
  private boolean initialized;

  @Override
  public Compiler create(Path configuration, Path report) throws IOException {
    this.report = report;
    value = Files.readString(configuration).trim();
    created = new Compiler() {
      @Override
      public void initChunks(
          List<SourceFile> externs, List<JSChunk> chunks, CompilerOptions options) {
        initialized = true;
        options.setDefineToStringLiteral("extensionValue", value);
        super.initChunks(externs, chunks, options);
      }
    };
    return created;
  }

  @Override
  public void finish(Compiler compiler, Path javascript) throws IOException {
    if (compiler != created || !initialized) {
      throw new IOException("Completion did not receive the initialized compiler");
    }
    Files.writeString(report, value + "\n" + Files.readString(javascript));
  }
}
