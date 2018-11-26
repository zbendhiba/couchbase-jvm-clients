/*
 * Copyright (c) 2018 Couchbase, Inc.
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

package com.couchbase.client.java;

import com.couchbase.client.core.Core;
import com.couchbase.client.core.CoreContext;
import com.couchbase.client.core.msg.kv.GetRequest;
import com.couchbase.client.core.msg.kv.InsertRequest;
import com.couchbase.client.core.msg.kv.RemoveRequest;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.kv.GetAccessor;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.InsertAccessor;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.MutationSpec;
import com.couchbase.client.java.kv.RemoveAccessor;
import com.couchbase.client.java.kv.FullInsertOptions;
import com.couchbase.client.java.kv.GetOptions;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.RemoveOptions;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.couchbase.client.core.util.Validators.notNull;
import static com.couchbase.client.core.util.Validators.notNullOrEmpty;

/**
 * The {@link AsyncCollection} provides basic asynchronous access to all collection APIs.
 *
 * <p>This type of API provides asynchronous support through the concurrency mechanisms
 * that ship with Java 8 and later, notably the async {@link CompletableFuture}. It is the
 * async mechanism with the lowest overhead (best performance) but also comes with less
 * bells and whistles as the {@link ReactiveCollection} for example.</p>
 *
 * <p>Most of the time we recommend using the {@link ReactiveCollection} unless you need the
 * last drop of performance or if you are implementing higher level primitives on top of this
 * one.</p>
 *
 * @since 3.0.0
 */
public class AsyncCollection {

  /**
   * Holds the underlying core which is used to dispatch operations.
   */
  private final Core core;

  /**
   * Holds the core context of the attached core.
   */
  private final CoreContext coreContext;

  /**
   * Holds the environment for this collection.
   */
  private final CouchbaseEnvironment environment;

  /**
   * The name of the collection.
   */
  private final String name;

  /**
   * The scope of the collection.
   */
  private final String scope;

  /**
   * Creates a new {@link AsyncCollection}.
   *
   * @param name the name of the collection.
   * @param scope the scope of the collection.
   * @param core the core into which ops are dispatched.
   * @param environment the surrounding environment for config options.
   */
  public AsyncCollection(final String name, final String scope, final Core core,
                  final CouchbaseEnvironment environment) {
    this.name = name;
    this.scope = scope;
    this.core = core;
    this.coreContext = core.context();
    this.environment = environment;
  }

  /**
   * Provides access to the underlying {@link Core}.
   */
  Core core() {
    return core;
  }

  /**
   * Provides access to the underlying {@link CouchbaseEnvironment}.
   */
  CouchbaseEnvironment environment() {
    return environment;
  }

  /**
   * Fetches a Document (or a fragment of it) from a collection with default options.
   *
   * <p>The {@link Optional} indicates if the document has been found or not. If the document
   * has not been found, an empty optional will be returned.</p>
   *
   * @param id the document id which is used to uniquely identify it.
   * @return a {@link CompletableFuture} indicating once loaded or failed.
   */
  public CompletableFuture<Optional<GetResult>> get(final String id) {
    return get(id, GetOptions.DEFAULT);
  }

  /**
   * Fetches a Document (or a fragment of it) from a collection with custom options.
   *
   * <p>The {@link Optional} indicates if the document has been found or not. If the document
   * has not been found, an empty optional will be returned.</p>
   *
   * @param id the document id which is used to uniquely identify it.
   * @param options custom options to change the default behavior.
   * @return a {@link CompletableFuture} completing once loaded or failed.
   */
  public CompletableFuture<Optional<GetResult>> get(final String id, final GetOptions options) {
    notNullOrEmpty(id, "Id");
    notNull(options, "GetOptions");

    Duration timeout = Optional.ofNullable(options.timeout()).orElse(environment.kvTimeout());
    GetRequest request = new GetRequest(id, timeout, coreContext);
    return GetAccessor.get(core, request);
  }

  /**
   * Removes a Document from a collection with default options.
   *
   * @param id the id of the document to remove.
   * @return a {@link CompletableFuture} completing once removed or failed.
   */
  public CompletableFuture<MutationResult> remove(final String id) {
    return remove(id, RemoveOptions.DEFAULT);
  }

  /**
   * Removes a Document from a collection with custom options.
   *
   * @param id the id of the document to remove.
   * @param options custom options to change the default behavior.
   * @return a {@link CompletableFuture} completing once removed or failed.
   */
  public CompletableFuture<MutationResult> remove(final String id, final RemoveOptions options) {
    notNullOrEmpty(id, "Id");
    notNull(options, "RemoveOptions");

    Duration timeout = options.timeout().orElse(environment.kvTimeout());
    RemoveRequest request = new RemoveRequest(id, options.cas(), timeout, coreContext);
    return RemoveAccessor.remove(core, request);
  }

  /**
   * Inserts a document fragment into a document which does not exist yet with default options.
   *
   * @param id the unique ID of the document which will be created.
   * @param content the content to be inserted.
   * @return a {@link CompletableFuture} completing once inserted or failed.
   */
  public CompletableFuture<MutationResult> insert(final String id, final MutationSpec content) {
    return insert(id, content, InsertOptions.DEFAULT);
  }

  /**
   * Inserts a full document which does not exist yet with default options.
   *
   * @param id the unique ID of the document which will be created.
   * @param content the content to be inserted.
   * @param <T> the generic type of the content to be inserted.
   * @return a {@link CompletableFuture} completing once inserted or failed.
   */
  public <T> CompletableFuture<MutationResult> insert(final String id, final T content) {
    return insert(id, content, FullInsertOptions.DEFAULT);
  }

  /**
   * Inserts a document fragment into a document which does not exist yet with custom options.
   *
   * @param id the unique ID of the document which will be created.
   * @param spec the content to be inserted.
   * @param options custom options to customize the insert behavior.
   * @return a {@link CompletableFuture} completing once inserted or failed.
   */
  public CompletableFuture<MutationResult> insert(final String id, final MutationSpec spec,
                                                  final InsertOptions options) {
    notNullOrEmpty(id, "Id");
    notNull(spec, "MutationSpec");
    notNull(options, "InsertOptions");

    throw new UnsupportedOperationException("Not Implemented yet: subdoc insert");
  }

  /**
   * Inserts a full document which does not exist yet with custom options.
   *
   * @param id the unique ID of the document which will be created.
   * @param content the content to be inserted.
   * @param options custom options to customize the insert behavior.
   * @param <T> the generic type of the content to be inserted.
   * @return a {@link CompletableFuture} completing once inserted or failed.
   */
  public <T> CompletableFuture<MutationResult> insert(final String id, final T content,
                                                      final FullInsertOptions<T> options) {
    notNullOrEmpty(id, "Id");
    notNull(content, "Content");
    notNull(options, "InsertOptions");

    byte[] encoded = null; // TODO: implement me
    long expiration = options.expiry().getSeconds();
    int flags = 0; // TODO
    byte datatype = 0; // TODO
    Duration timeout = Optional.ofNullable(options.timeout()).orElse(environment.kvTimeout());

    InsertRequest request = new InsertRequest(id, encoded, expiration, flags, datatype,
      timeout, coreContext);
    return InsertAccessor.insert(core, request);
  }


}