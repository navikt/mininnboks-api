package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.IOException
import java.time.LocalDate
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

@Provider
class JacksonConfig : ContextResolver<ObjectMapper> {
    companion object {
        val mapper = ObjectMapper()

        init {
            mapper.registerModule(KotlinModule().addDeserializer(LocalDate::class.java, object : JsonDeserializer<LocalDate>() {
                @Throws(IOException::class, JsonProcessingException::class)
                override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LocalDate {
                    return LocalDate.parse(jsonParser.text)
                }
            }))
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    override fun getContext(type: Class<*>?): ObjectMapper {
        return mapper
    }
}
