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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jp.furplag.data.json.JsonizableTest.Zero.AnotherOne;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

public class JsonizableTest {

  interface Zero extends Jsonizable<Zero> {

    @RequiredArgsConstructor
    @EqualsAndHashCode
    static class One implements Zero {
      final int id;
      final String name;
    }

    static class AnotherOne extends Zero.One {
      final Long version;
      AnotherOne(int id, String name, Long version) {
        super(id, name);
        this.version = version;
      }
    }

    static class AlwaysTwo extends Zero.One {
      AlwaysTwo() {
        super(2, "two");
      }
    }
  }

  @Test
  void test() {
    assertEquals("{\"id\":1,\"name\":\"one\"}", new Zero.One(1, "one") {}.json());
    assertEquals(new Zero.One(1, "test"), Jsonifier.deserialize(new Zero.One(1, "test").json(), Zero.One.class));
    assertEquals(new Zero.AnotherOne(1, "test", null), new Zero.One(1, "test").transduce(AnotherOne.class));
    assertEquals(new Zero.AnotherOne(1, "test", 2L), new Zero.One(1, "test") { @SuppressWarnings({ "unused" }) public long getVersion() { return 2; } }.transduce(Zero.AnotherOne.class));
    assertEquals(2, new Zero.One(1, "test").transduce(Zero.AlwaysTwo.class).id);
    assertEquals(new Zero.One(2, "two"), new Zero.One(1, "test").merge(new Zero.AlwaysTwo()));
    assertEquals(new Zero.One(1, "test"), new Zero.One(1, "test").merge(new Zero.AlwaysTwo(), "id", "name"));
  }
}
