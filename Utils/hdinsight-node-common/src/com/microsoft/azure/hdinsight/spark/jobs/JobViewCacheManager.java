/**
 * Copyright (c) Microsoft Corporation
 * <p/>
 * All rights reserved.
 * <p/>
 * MIT License
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.microsoft.azure.hdinsight.spark.jobs;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.azure.hdinsight.sdk.rest.spark.executor.Executor;
import com.microsoft.azure.hdinsight.sdk.rest.spark.job.Job;
import com.microsoft.azure.hdinsight.sdk.rest.spark.stage.Stage;
import com.microsoft.azuretools.azurecommons.helpers.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class JobViewCacheManager {
    private static final LoadingCache<ApplicationKey, List<Job>> sparkJobLocalCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .initialCapacity(20)
            .build(new CacheLoader<ApplicationKey, List<Job>>() {
                @Override
                public List<Job> load(ApplicationKey key) throws Exception {
                    return SparkRestUtil.getLastAttemptJobsFromApp(key);
                }
            });

    private static final LoadingCache<ApplicationKey, List<Stage>> sparkStageLocalCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .initialCapacity(20)
            .build(new CacheLoader<ApplicationKey, List<Stage>>() {
                @Override
                public List<Stage> load(ApplicationKey key) throws Exception {
                    return SparkRestUtil.getAllStageFromApp(key);
                }
            });

    private static final LoadingCache<ApplicationKey, List<Executor>> sparkExecutorLocalCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .initialCapacity(20)
            .build(new CacheLoader<ApplicationKey, List<Executor>>() {
                @Override
                public List<Executor> load(ApplicationKey key) throws Exception {
                    return SparkRestUtil.getAllExecutorFromApp(key);
                }
            });

    public static List<Executor> getExecutors(@NotNull ApplicationKey key) throws ExecutionException {
        return sparkExecutorLocalCache.get(key);
    }

    public static List<Job> getJob(@NotNull ApplicationKey key) throws ExecutionException {
        return sparkJobLocalCache.get(key);
    }

    public static List<Stage> getStages(@NotNull ApplicationKey key) throws ExecutionException {
        return sparkStageLocalCache.get(key);
    }
}