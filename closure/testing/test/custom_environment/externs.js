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

/**
 * @fileoverview Browser bindings used by the custom-environment test suite.
 * Engine-specific stack frame types are intentionally absent.
 * @externs
 */

/** @constructor */
function Event() {}

/** @interface */
function EventListener() {}

/** @param {!Event} event */
EventListener.prototype.handleEvent = function(event) {};

/** @constructor */
function Node() {}

/** @param {!Node} child @return {!Node} */
Node.prototype.appendChild = function(child) {};

/** @type {?string} */
Node.prototype.textContent;

/** @constructor @extends {Node} */
function Element() {}

/** @type {string} */
Element.prototype.id;

/** @type {string} */
Element.prototype.dir;

/** @type {string} */
Element.prototype.innerHTML;

/** @type {!Object<string, string>} */
Element.prototype.style;

/** @constructor @extends {Element} */
function HTMLElement() {}

/** @type {string} */
HTMLElement.prototype.href;

/** @type {?function(!Event): *} */
HTMLElement.prototype.onclick;

/** @constructor */
function Location() {}

/** @type {string} */
Location.prototype.href;

/** @type {string} */
Location.prototype.pathname;

/** @type {string} */
Location.prototype.search;

/** @type {string} */
Location.prototype.hash;

/** @constructor @implements {Iterable<!Array<string>>} */
function URLSearchParams() {}

/** @return {!Iterator<!Array<string>>} */
URLSearchParams.prototype[Symbol.iterator] = function() {};

/** @constructor @param {string} url @param {string=} base */
function URL(url, base) {}

/** @type {!URLSearchParams} */
URL.prototype.searchParams;

/** @constructor */
function Document() {}

/** @type {string} */
Document.prototype.title;

/** @type {!Location} */
Document.prototype.location;

/** @type {!HTMLElement} */
Document.prototype.body;

/** @param {string} id @return {?HTMLElement} */
Document.prototype.getElementById = function(id) {};

/** @param {string} tag @return {!HTMLElement} */
Document.prototype.createElement = function(tag) {};

/** @param {string} text @return {!Node} */
Document.prototype.createTextNode = function(text) {};

/** @type {!Document} */
var document;

/** @constructor */
function Performance() {}

/** @return {number} */
Performance.prototype.now = function() {};

/** @type {!Performance} */
var performance;

/** @constructor */
function Window() {}

/** @type {!Location} */
Window.prototype.location;

/** @type {!Performance} */
Window.prototype.performance;

/** @type {?function((Event|string), string=, number=, number=, *=): undefined} */
Window.prototype.onerror;

/** @type {?function(!Event): *} */
Window.prototype.onload;

/** @param {string} type @param {!EventListener} listener */
Window.prototype.addEventListener = function(type, listener) {};

/** @param {!Function} callback @param {number=} delay @return {number} */
Window.prototype.setTimeout = function(callback, delay) {};

/** @type {!Window} */
var window;

/** @param {!Function} callback @param {number=} delay @return {number} */
function setTimeout(callback, delay) {}

/** @param {number} timeoutId */
function clearTimeout(timeoutId) {}

/** @type {!Location} */
var location;

/** @constructor */
function Navigator() {}

/** @type {string} */
Navigator.prototype.userAgent;

/** @type {!Navigator} */
var navigator;

/** @constructor */
function Console() {}

/** @param {...*} values */
Console.prototype.log = function(values) {};

/** @param {...*} values */
Console.prototype.error = function(values) {};

/** @param {...*} values */
Console.prototype.group = function(values) {};

Console.prototype.groupEnd = function() {};

/** @type {!Console} */
var console;
