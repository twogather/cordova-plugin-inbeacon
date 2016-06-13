/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

var exec = require('cordova/exec');

/**
 * @constructor
 */
function InBeacon() {
}

/**
 * Loglevel 0  = no logging
 * @type {number}
 */
InBeacon.prototype.LOG_NONE     = 0;
/**
 * Loglevel 1  = errors only
 * @type {number}
 */
InBeacon.prototype.LOG_ERROR       = 1;
/**
 * Loglevel 2  = important logs and errors
 * @type {number}
 */
InBeacon.prototype.LOG_LOG    = 2;
/**
 * Loglevel 3  = more verbose
 * @type {number}
 */
InBeacon.prototype.LOG_INFO      = 3;
/**
 * Loglevel 4  = even more verbose
 * @type {number}
 */
InBeacon.prototype.LOG_DEBUG     = 4;


/**
 * To initialize connection with inBeacon API using ClientId and Secret
 *
 * @param {object} kwargs Keyword arguments is an object contain key/value pairs of clientId and secret
 * @param {Function} successCallback The function to call when succeeded
 * @param {Function} errorCallback The function to call when there is an error (OPTIONAL)
 */
InBeacon.prototype.initialize = function(kwargs, successCallback, errorCallback) {
    exec(successCallback, errorCallback || null, "InBeacon", "initialize", [kwargs]);
};

/**
 * To get the newest data from inBeacon API
 *
 * @param {Function} successCallback The function to call when succeeded
 * @param {Function} errorCallback The function to call when there is an error (OPTIONAL)
 */
InBeacon.prototype.refresh = function(successCallback, errorCallback){
    exec(successCallback, errorCallback || null, "InBeacon", "refresh", []);
}

/**
 * To change the logging level, using one of the predefined constants
 *
 * @param logLevel
 * @param {Function} successCallback The function to call when succeeded
 * @param {Function} errorCallback The function to call when there is an error (OPTIONAL)
 */
InBeacon.prototype.setLogLevel = function(logLevel, successCallback, errorCallback){
    exec(successCallback, errorCallback || null, "InBeacon", "setLogLevel", [logLevel]);
}

module.exports = new InBeacon();
