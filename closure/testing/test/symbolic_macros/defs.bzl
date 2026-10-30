# Copyright 2026 The Closure Rules Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""A symbolic macro exercising the public browser-test macro."""

load("//closure/testing:webdriver_test.bzl", "webdriver_test")

def _browser_test_impl(name, visibility):
    webdriver_test(
        name = name,
        browsers = ["@rules_browsers//browsers/chromium:chromium"],
        test_file_js = "pass.js",
        visibility = visibility,
    )

symbolic_browser_test = macro(implementation = _browser_test_impl)
