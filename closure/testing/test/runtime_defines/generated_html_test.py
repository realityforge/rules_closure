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

from html.parser import HTMLParser
import json
from pathlib import Path
import re
import sys
import unittest


class ScriptParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.scripts = []
        self.current = None

    def handle_starttag(self, tag, attrs):
        if tag == "script":
            self.current = {"src": dict(attrs).get("src"), "body": ""}
            self.scripts.append(self.current)

    def handle_data(self, data):
        if self.current is not None:
            self.current["body"] += data

    def handle_endtag(self, tag):
        if tag == "script":
            self.current = None


class GeneratedHtmlTest(unittest.TestCase):
    def definitions(self, path):
        parser = ScriptParser()
        parser.feed(Path(path).read_text(encoding="utf-8"))
        self.assertEqual(2, len(parser.scripts))
        self.assertIsNone(parser.scripts[0]["src"])
        self.assertEqual("browser_test_bin.js", parser.scripts[1]["src"])
        inline = parser.scripts[0]["body"]
        definition = re.search(r"var CLOSURE_DEFINES = (.+);", inline)
        self.assertIsNotNone(definition)
        alias = "var CLOSURE_UNCOMPILED_DEFINES = CLOSURE_DEFINES;"
        self.assertIn(alias, inline)
        self.assertLess(definition.end(), inline.index(alias))
        return json.loads(definition.group(1))

    def expected(self, environment):
        return {
            "runtimeDefsTest.environment": environment,
            "runtimeDefsTest.enabled": "true",
            "runtimeDefsTest.disabled": "false",
            "runtimeDefsTest.payload": (
                '</script><script>window.runtimeDefinitionsInjected = true</script>'
                '\n"quoted"\\path&'
            ),
        }

    def test_direct_values_preserve_strings_and_script_text(self):
        self.assertEqual(self.expected("production"), self.definitions(DIRECT_HTML))

    def test_macro_forwards_configured_values(self):
        self.assertEqual(self.expected(ENVIRONMENT), self.definitions(MACRO_HTML))

    def test_omitted_definitions_are_empty(self):
        self.assertEqual({}, self.definitions(EMPTY_HTML))


if __name__ == "__main__":
    DIRECT_HTML, MACRO_HTML, EMPTY_HTML, ENVIRONMENT = sys.argv[1:]
    unittest.main(argv=[sys.argv[0]])
