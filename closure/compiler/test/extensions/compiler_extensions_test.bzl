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

"""Regression coverage for declared compiler-extension inputs and outputs."""

load("@bazel_skylib//lib:unittest.bzl", "analysistest", "asserts")
load("//closure:defs.bzl", "ClosureJsBinaryInfo", "closure_js_binary")

def _compiler_report_impl(ctx):
    return [DefaultInfo(files = depset([ctx.attr.binary[ClosureJsBinaryInfo].compiler_report]))]

compiler_report = rule(
    implementation = _compiler_report_impl,
    attrs = {"binary": attr.label(mandatory = True, providers = [ClosureJsBinaryInfo])},
)

def _declared_files_test_impl(ctx):
    env = analysistest.begin(ctx)
    target = analysistest.target_under_test(env)
    info = target[ClosureJsBinaryInfo]
    actions = analysistest.target_actions(env)
    asserts.equals(env, 1, len(actions))
    action = actions[0]
    asserts.equals(env, "compiler.config", info.compiler_config.basename)
    asserts.true(env, info.compiler_config in action.inputs.to_list())
    asserts.true(env, info.compiler_report in action.outputs.to_list())
    asserts.true(env, info.compiler_report in target[DefaultInfo].files.to_list())
    return analysistest.end(env)

_declared_files_test = analysistest.make(_declared_files_test_impl)

def _paired_attributes_test_impl(ctx):
    env = analysistest.begin(ctx)
    asserts.expect_failure(env, "compiler_config and compiler_report must be supplied together")
    return analysistest.end(env)

_paired_attributes_test = analysistest.make(_paired_attributes_test_impl, expect_failure = True)

def compiler_extensions_test_suite(name):
    tests = []
    _declared_files_test(
        name = name + "_declared_files",
        target_under_test = ":configured",
    )
    tests.append(name + "_declared_files")

    for variant in ["configuration_only", "report_only"]:
        target = name + "_" + variant
        closure_js_binary(
            name = target,
            compiler_config = "compiler.config" if variant == "configuration_only" else None,
            compiler_report = target + ".report" if variant == "report_only" else None,
            deps = [":source"],
            tags = ["manual"],
        )
        test = target + "_test"
        _paired_attributes_test(
            name = test,
            target_under_test = ":" + target,
        )
        tests.append(test)

    native.test_suite(name = name, tests = tests)
