/*
 * Copyleft 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.uop.intermittent.faults.utils;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
public class CacheStore {

    public static void cacheStore(Object instance, Object value, String metricName, String metricGroup) throws Exception {
        Cache cacheInstance;
        cacheInstance = CacheCollection.getCacheCollection().getCacheInstance(metricGroup);
        if (cacheInstance == null) {
            cacheInstance = new Cache();
            CacheCollection.getCacheCollection().addCacheInstance(metricGroup, cacheInstance);
        }
        Store.CacheStore(instance, value, metricName, cacheInstance);
    }
}

