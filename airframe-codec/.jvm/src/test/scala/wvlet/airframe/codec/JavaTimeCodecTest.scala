/*
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
package wvlet.airframe.codec
import java.time.{Instant, ZonedDateTime}

import wvlet.airframe.codec.PrimitiveCodec.StringCodec
import wvlet.airframe.metrics.TimeParser
import wvlet.airframe.msgpack.spi.MessagePack
import wvlet.airframe.surface.Surface

/**
  */
class JavaTimeCodecTest extends CodecSpec {

  test("support ZonedDateTime") {
    roundtrip(Surface.of[ZonedDateTime], ZonedDateTime.now())
    roundtrip(Surface.of[ZonedDateTime], ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]"))

    val codec = MessageCodec.of[ZonedDateTime]
    val p     = MessagePack.newBufferPacker
    p.packString("non-date string")
    val v = codec.unpackMsgPack(p.toByteArray)
    v shouldBe empty
  }

  test("support java.util.Date") {
    val now = java.util.Date.from(Instant.now())
    roundtrip(Surface.of[java.util.Date], now)
  }

  test("parse various time strings in InstantCodec") {
    val timeStr = "2018-05-26 21:10:29-0800"
    val z       = TimeParser.parseAtLocalTimeZone(timeStr).get
    val i1      = z.toInstant

    val msgpack = StringCodec.toMsgPack(timeStr)
    val i2      = JavaInstantTimeCodec.fromMsgPack(msgpack)

    i1 shouldBe i2
  }
}
