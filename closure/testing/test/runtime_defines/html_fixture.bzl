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

"""Extracts generated HTML through a browser test's runfiles."""

def _html_fixture_impl(ctx):
    files = [
        file
        for file in ctx.attr.browser_test[DefaultInfo].default_runfiles.files.to_list()
        if file.extension == "html"
    ]
    if len(files) != 1:
        fail("Expected exactly one HTML runfile from %s, got %s" % (
            ctx.attr.browser_test.label,
            [file.short_path for file in files],
        ))
    return [DefaultInfo(files = depset(files))]

html_fixture = rule(
    implementation = _html_fixture_impl,
    attrs = {"browser_test": attr.label(mandatory = True)},
)
