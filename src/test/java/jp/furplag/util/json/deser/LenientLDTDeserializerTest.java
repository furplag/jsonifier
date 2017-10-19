/**
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

package jp.furplag.util.json.deser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class LenientLDTDeserializerTest {

  @Test
  public void testLenientLDTDeserializer() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method parseDateToDateTime = LenientLDTDeserializer.class.getDeclaredMethod("parseDateToDateTime", String.class);
    Method parseArrayToDateTime = LenientLDTDeserializer.class.getDeclaredMethod("parseArrayToDateTime", int[].class);
    AccessibleObject.setAccessible(new AccessibleObject[] {parseDateToDateTime, parseArrayToDateTime}, true);

    assertNull(parseDateToDateTime.invoke(new LenientLDTDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new Object[] {null}));
    assertNull(parseArrayToDateTime.invoke(new LenientLDTDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new Object[] {null}));
    assertNull(parseArrayToDateTime.invoke(new LenientLDTDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new Object[] {new int[] {1, 2}}));
    assertThat(parseArrayToDateTime.invoke(new LenientLDTDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new Object[] {new int[] {1, 2, 3}}), is(LocalDateTime.of(1, 2, 3, 0, 0)));
    assertNull(parseArrayToDateTime.invoke(new LenientLDTDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new Object[] {new int[] {1, 2, 3, 4}}));
  }
}
