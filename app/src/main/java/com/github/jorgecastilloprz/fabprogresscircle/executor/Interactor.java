/*
 * Copyright (C) 2014 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jorgecastilloprz.fabprogresscircle.executor;

/**
 * Interactors are the execution unit for the system, and they represent the use cases.
 *
 * This interface will be used to abstract concrete use cases from the executor logic.
 *
 * Created by jorge on 11/01/15.
 */
public interface Interactor {
    void run();
}
