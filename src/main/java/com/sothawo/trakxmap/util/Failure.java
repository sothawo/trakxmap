/*
 Copyright 2015 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.sothawo.trakxmap.util;

import java.util.Objects;
import java.util.Optional;

/**
 * A Failure is an object that might be returned for example in an optional as the result of an operation. It
 * encapsulates an error messages with an optional error cause.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public final class Failure {
// ------------------------------ FIELDS ------------------------------

    /** the error message */
    private final String message;
    /** an optional cause */
    private final Optional<Throwable> cause;

// --------------------------- CONSTRUCTORS ---------------------------

    public Failure(String message) {
        this(message, null);
    }

    /**
     * creates a a Failure.
     *
     * @param message
     *         message
     * @param cause
     *         optional cause
     * @throws java.lang.NullPointerException
     *         if message is null
     */
    public Failure(String message, Throwable cause) {
        this.message = Objects.requireNonNull(message);
        this.cause = Optional.ofNullable(cause);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Optional<Throwable> getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }
}
