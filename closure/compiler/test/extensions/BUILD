# Copyright 2026 The Closure Rules Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("//closure:defs.bzl", "closure_js_binary", "closure_js_library")
load("//closure/private:file_test.bzl", "file_test")
load(":compiler_extensions_test.bzl", "compiler_extensions_test_suite", "compiler_report")

package(default_testonly = True)

licenses(["notice"])

closure_js_library(
    name = "source",
    srcs = ["extension.js"],
    no_closure_library = True,
)

closure_js_binary(
    name = "configured",
    compiler = "//javatests/com/google/javascript/jscomp:extension_worker",
    compiler_config = "compiler.config",
    compiler_report = "configured.report",
    deps = [":source"],
)

compiler_report(
    name = "report",
    binary = ":configured",
)

file_test(
    name = "configured_javascript_test",
    content = "console.log(\"configured\");\n",
    file = "configured.js",
)

file_test(
    name = "configured_report_test",
    content = "configured\nconsole.log(\"configured\");\n",
    file = ":report",
)

compiler_extensions_test_suite(name = "analysis_tests")
