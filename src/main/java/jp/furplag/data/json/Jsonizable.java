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
  default <U extends Jsonizable<?>> T merge(U source, String... excludeFieldNames) {
    Trebuchet.Consumers.orNot(this, source, _excludeFieldNameSet(excludeFieldNames), Jsonizable::_set);

    return (T) this;
  }

  /**
   * array paramater collect as a {@link Set} .
   *
   * @param excludeFieldNames field name (s) to be excludes
   * @return {@link Set} of field name (s) to be excludes, return empty {@link Set} if there no valid one
   */
  private static Set<String> _excludeFieldNameSet(final String[] excludeFieldNames) {
    return Streamr.collect(HashSet::new, excludeFieldNames);
  }

  /**
   * filtering to parameter for update .
   *
   * @param <U> the type of {@link Jsonizable} one
   * @param source the class of object to materialize, may not be null
   * @param excludeFieldNames field name (s) to be excludes
   * @return update parameter represented by the type of {@link Map.Entry}
   */
  private static <U extends Jsonizable<?>> Stream<Map.Entry<String, Object>> _filter(final U source, Set<String> excludeFieldNames) {
    return Streamr.Filter.filtering(source.map().entrySet(), (_parameter) -> !excludeFieldNames.contains(_parameter.getKey()));
  }

  /**
   * set parameter from source object, if those are convertible .
   *
   * @param <T> the type which origin under converting
   * @param <U> the type of {@link Jsonizable} one
   * @param source the class of object to materialize, may not be null
   * @param excludeFieldNames field name (s) to be excludes
   */
  private static <T, U extends Jsonizable<?>> void _set(final T _this, final U source, Set<String> excludeFieldNames) {
    _filter(source, excludeFieldNames).forEach((_parameter) -> SavageReflection.set(_this, _parameter.getKey(), _parameter.getValue()));
  }
}
