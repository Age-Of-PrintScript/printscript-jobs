package age.of.printscript.jobs

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object JobSerializer {
    val objectMapper: ObjectMapper =
        jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

    fun serializeRequest(request: JobRequest): String = objectMapper.writeValueAsString(request)

    fun deserializeRequest(json: String): JobRequest = objectMapper.readValue(json)

    fun serializeResponse(response: JobResponse): String = objectMapper.writeValueAsString(response)

    fun deserializeResponse(json: String): JobResponse = objectMapper.readValue(json)
}
