/*
 * Copyright (C) 2017+ furplag (https://github.com/furplag)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.furplag.data.json;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jp.furplag.sandbox.reflect.Reflections;
import jp.furplag.sandbox.reflect.SavageReflection;
import jp.furplag.sandbox.stream.Streamr;
import jp.furplag.sandbox.trebuchet.Trebuchet;

/**
 * convert utilities between JSON and object .
 *
 * @author furplag
 *
 * @param <T> the type which origin under converting
 */
public interface Jsonizable<T> {

  /**
   * shorthand for extract the JSON formatted string of the object .
   *
   * @return JSON string
   */
  default String json() {
    return Jsonifier.serializeBrutaly(this);
  }

  /**
   * shorthand for extract the parameter of the object .
   *
   * @return {@link Map} ( {@link String} key : {@link Object} value )
   */
  default Map<String, Object> map() {
    return SavageReflection.read(this, Streamr.stream(Reflections.getFields(this)).filter(Reflections::isStatic).map(Field::getName).toArray(String[]::new));
  }

  /**
   * returns the instance of &lt;R&gt; which the parameter duplicate from this object .
   *
   * @param <R> the type of object to materialize
   * @param deserializeType the class of object to materialize, may not be null
   * @param excludeFieldNames field name (s) to be excludes
   * @return an instance of {@code deserializeType}
   */
  default <R> R transduce(Class<R> deserializeType, String... excludeFieldNames) {
    return Jsonifier.deserialize(json(), deserializeType);
  }

  /**
   * set parameter from source object, if those are convertible .
   *
   * @param <U> the type of {@link Jsonizable} one
   * @param source the class of object to materialize, may not be null
   * @param excludeFieldNames field name (s) to be excludes
   * @return an instance of {@code deserializeType}
   */
  @SuppressWarnings({ "unchecked" })
  default <U extends Jsonizable<?>> T merge(U source, String... excludeFieldNames) {/* @formatter:off */
    final BiFunction<U, Set<String>, Stream<Map.Entry<String, Object>>> _filter = (_source, excludes) -> Streamr.Filter.filtering(_source.map().entrySet(), (entry) -> !excludes.contains(entry.getKey()));
    final Consumer<Map.Entry<String, Object>> _set = (e) -> SavageReflection.set(this, e.getKey(), e.getValue());
    Trebuchet.Consumers.orNot(
        source, Streamr.collect(HashSet::new, excludeFieldNames)
      , (_source, excludes) -> _filter.apply(_source, excludes).forEach(_set)
    );
    /* @formatter:on */
    return (T) this;
  }
}
