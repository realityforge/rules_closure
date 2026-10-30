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

"""Runtime regression for links collected from an extra runfiles attribute."""

load("//closure/private:defs.bzl", "collect_runfiles")

def _fixture_impl(ctx):
    default_file = ctx.file.default_file
    data_file = ctx.file.data_file
    return [DefaultInfo(
        files = depset([default_file, data_file]),
        default_runfiles = ctx.runfiles(
            files = [default_file],
            symlinks = {"runfiles_test/default": default_file},
            root_symlinks = {"runfiles_test_default_root": default_file},
        ),
        data_runfiles = ctx.runfiles(
            files = [data_file],
            symlinks = {"runfiles_test/data": data_file},
            root_symlinks = {"runfiles_test_data_root": data_file},
        ),
    )]

runfiles_fixture = rule(
    implementation = _fixture_impl,
    attrs = {
        "data_file": attr.label(allow_single_file = True, mandatory = True),
        "default_file": attr.label(allow_single_file = True, mandatory = True),
    },
)

def _test_impl(ctx):
    files = {file.basename: file.short_path for file in ctx.files.runtime}
    executable = ctx.outputs.executable
    ctx.actions.write(
        output = executable,
        is_executable = True,
        content = """#!/usr/bin/env bash
set -euo pipefail
default_file="${TEST_SRCDIR}/${TEST_WORKSPACE}/%s"
data_file="${TEST_SRCDIR}/${TEST_WORKSPACE}/%s"
cmp "$default_file" "${TEST_SRCDIR}/${TEST_WORKSPACE}/runfiles_test/default"
cmp "$default_file" "${TEST_SRCDIR}/runfiles_test_default_root"
cmp "$data_file" "${TEST_SRCDIR}/${TEST_WORKSPACE}/runfiles_test/data"
cmp "$data_file" "${TEST_SRCDIR}/runfiles_test_data_root"
""" % (files["default.txt"], files["data.txt"]),
    )
    return [DefaultInfo(
        executable = executable,
        runfiles = collect_runfiles(ctx, [executable], extra_runfiles_attrs = ["runtime"]),
    )]

runfiles_links_test = rule(
    implementation = _test_impl,
    test = True,
    attrs = {
        # Automatic runfiles collection must not supply the fixture's links.
        "runtime": attr.label(mandatory = True),
    },
)
