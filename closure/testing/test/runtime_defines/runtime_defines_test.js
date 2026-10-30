// Copyright 2026 The Closure Rules Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

goog.provide('runtimeDefsTest');

goog.require('goog.testing.asserts');
goog.require('goog.testing.jsunit');

/** @define {string} */
runtimeDefsTest.environment = goog.define('runtimeDefsTest.environment', 'unset');

/** @define {string} */
runtimeDefsTest.enabled = goog.define('runtimeDefsTest.enabled', 'unset');

/** @define {string} */
runtimeDefsTest.disabled = goog.define('runtimeDefsTest.disabled', 'unset');

/** @define {string} */
runtimeDefsTest.payload = goog.define('runtimeDefsTest.payload', 'unset');

function testDefinitionsAreAvailableWhenTheBundleLoads() {
  assertTrue(
      runtimeDefsTest.environment === 'production' ||
      runtimeDefsTest.environment === 'preview');
  assertEquals('true', runtimeDefsTest.enabled);
  assertEquals('false', runtimeDefsTest.disabled);
  assertEquals(
      '</script><script>window.runtimeDefinitionsInjected = true</script>' +
          '\n"quoted"\\path&',
      runtimeDefsTest.payload);
  assertUndefined(goog.global['runtimeDefinitionsInjected']);
}
