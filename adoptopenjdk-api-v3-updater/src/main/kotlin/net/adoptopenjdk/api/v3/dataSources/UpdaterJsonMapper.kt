package net.adoptopenjdk.api.v3.dataSources

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr353.JSR353Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedTimeUpgrader : SimpleModule() {
    init {
        addDeserializer(ZonedDateTime::class.java, object : JsonDeserializer<ZonedDateTime>() {
            override fun deserialize(parser: JsonParser?, context: DeserializationContext?): ZonedDateTime {
                if (parser!!.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
                    return InstantDeserializer.ZONED_DATE_TIME.deserialize(parser, context)
                } else {
                    return LocalDateTimeDeserializer.INSTANCE.deserialize(parser, context)
                            .atZone(ZoneId.of("UTC"))
                }
            }
        })
    }
}

object UpdaterJsonMapper {
    val mapper: ObjectMapper = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .findAndRegisterModules()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .registerModule(JSR353Module())
            .registerModule(ZonedTimeUpgrader())
}