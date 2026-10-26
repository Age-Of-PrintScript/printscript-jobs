package age.of.printscript.jobs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JobSerializationTest {
    @Test
    fun `serialize and deserialize ValidateJobRequest`() {
        val request =
            ValidateJobRequest(
                jobId = "job-1",
                snippetId = "snip-1",
                snippetUrl = "http://storage/snip-1",
                languageVersion = "1.1",
            )
        val json = JobSerializer.serializeRequest(request)
        assertTrue(json.contains(""""type":"VALIDATE""""))

        val deserialized = JobSerializer.deserializeRequest(json) as ValidateJobRequest
        assertEquals(request, deserialized)
    }

    @Test
    fun `serialize and deserialize ExecuteJobRequest`() {
        val request =
            ExecuteJobRequest(
                jobId = "job-2",
                snippetId = "snip-2",
                snippetUrl = "http://storage/snip-2",
                languageVersion = "1.0",
                inputs = listOf("input1", "input2"),
            )
        val json = JobSerializer.serializeRequest(request)
        assertTrue(json.contains(""""type":"EXECUTE""""))

        val deserialized = JobSerializer.deserializeRequest(json) as ExecuteJobRequest
        assertEquals(request, deserialized)
    }

    @Test
    fun `serialize and deserialize TestJobRequest`() {
        val request =
            TestJobRequest(
                jobId = "job-3",
                snippetId = "snip-3",
                snippetUrl = "http://storage/snip-3",
                languageVersion = "1.1",
                inputs = listOf("in"),
                expectedOutputs = listOf("out"),
            )
        val json = JobSerializer.serializeRequest(request)
        assertTrue(json.contains(""""type":"TEST""""))

        val deserialized = JobSerializer.deserializeRequest(json) as TestJobRequest
        assertEquals(request, deserialized)
    }

    @Test
    fun `serialize and deserialize LintJobRequest`() {
        val request =
            LintJobRequest(
                jobId = "job-4",
                snippetId = "snip-4",
                snippetUrl = "http://storage/snip-4",
                languageVersion = "1.1",
                rules = mapOf("identifier-format" to "camelCase", "println-no-expression" to null),
            )
        val json = JobSerializer.serializeRequest(request)
        assertTrue(json.contains(""""type":"LINT""""))

        val deserialized = JobSerializer.deserializeRequest(json) as LintJobRequest
        assertEquals(request, deserialized)
    }

    @Test
    fun `serialize and deserialize FormatJobRequest`() {
        val request =
            FormatJobRequest(
                jobId = "job-5",
                snippetId = "snip-5",
                snippetUrl = "http://storage/snip-5",
                languageVersion = "1.0",
                rules = mapOf("indent-inside-if" to "4", "enforce-spacing-around-equals" to null),
            )
        val json = JobSerializer.serializeRequest(request)
        assertTrue(json.contains(""""type":"FORMAT""""))

        val deserialized = JobSerializer.deserializeRequest(json) as FormatJobRequest
        assertEquals(request, deserialized)
    }

    @Test
    fun `serialize and deserialize ValidateJobResponse valid and invalid`() {
        val validResponse = ValidateJobResponse(jobId = "job-1", isValid = true)
        val validJson = JobSerializer.serializeResponse(validResponse)
        assertTrue(validJson.contains(""""type":"VALIDATE""""))
        val deserializedValid = JobSerializer.deserializeResponse(validJson) as ValidateJobResponse
        assertTrue(deserializedValid.isValid)
        assertTrue(deserializedValid.errors.isEmpty())

        val invalidResponse =
            ValidateJobResponse(
                jobId = "job-1",
                isValid = false,
                errors = listOf(ValidationErrorDto("Syntax error at line 1")),
            )
        val invalidJson = JobSerializer.serializeResponse(invalidResponse)
        val deserializedInvalid = JobSerializer.deserializeResponse(invalidJson) as ValidateJobResponse
        assertFalse(deserializedInvalid.isValid)
        assertEquals(1, deserializedInvalid.errors.size)
        assertEquals("Syntax error at line 1", deserializedInvalid.errors.first().message)
    }

    @Test
    fun `serialize and deserialize ExecuteJobResponse`() {
        val response =
            ExecuteJobResponse(
                jobId = "job-2",
                outputs = listOf("Hello", "World"),
            )
        val json = JobSerializer.serializeResponse(response)
        assertTrue(json.contains(""""type":"EXECUTE""""))

        val deserialized = JobSerializer.deserializeResponse(json) as ExecuteJobResponse
        assertEquals(response, deserialized)
    }

    @Test
    fun `serialize and deserialize TestJobResponse`() {
        val response =
            TestJobResponse(
                jobId = "job-3",
                success = false,
                message = "Expected output 'out' but got 'error'",
            )
        val json = JobSerializer.serializeResponse(response)
        assertTrue(json.contains(""""type":"TEST""""))

        val deserialized = JobSerializer.deserializeResponse(json) as TestJobResponse
        assertEquals(response, deserialized)
    }

    @Test
    fun `serialize and deserialize LintJobResponse`() {
        val response =
            LintJobResponse(
                jobId = "job-4",
                warnings =
                    listOf(
                        LintWarningDto(
                            message = "Identifier should be camelCase",
                            positionDto = PositionDto(line = 2, charPosition = 5),
                        ),
                    ),
            )
        val json = JobSerializer.serializeResponse(response)
        assertTrue(json.contains(""""type":"LINT""""))

        val deserialized = JobSerializer.deserializeResponse(json) as LintJobResponse
        assertEquals(response, deserialized)
    }

    @Test
    fun `serialize and deserialize FormatJobResponse`() {
        val response = FormatJobResponse(jobId = "job-5")
        val json = JobSerializer.serializeResponse(response)
        assertTrue(json.contains(""""type":"FORMAT""""))

        val deserialized = JobSerializer.deserializeResponse(json) as FormatJobResponse
        assertEquals(response, deserialized)
    }

    @Test
    fun `serialize and deserialize SystemErrorJobResponse`() {
        val response =
            SystemErrorJobResponse(
                jobId = "job-6",
                reason = "Out of memory or timeout",
            )
        val json = JobSerializer.serializeResponse(response)
        assertTrue(json.contains(""""type":"SYSTEM_ERROR""""))

        val deserialized = JobSerializer.deserializeResponse(json) as SystemErrorJobResponse
        assertEquals(response, deserialized)
    }

    @Test
    fun `deserialize ignores unknown fields for forward compatibility`() {
        val jsonWithUnknownFields =
            """
            {
                "type": "VALIDATE",
                "jobId": "job-compat",
                "snippetId": "snip-compat",
                "snippetUrl": "http://storage/snip-compat",
                "languageVersion": "1.1",
                "schemaVersion": 1,
                "someFutureField": "futureValue"
            }
            """.trimIndent()

        val deserialized = JobSerializer.deserializeRequest(jsonWithUnknownFields) as ValidateJobRequest
        assertEquals("job-compat", deserialized.jobId)
    }

    @Test
    fun `verify QueueNames constants`() {
        assertEquals("jobs:requests", QueueNames.REQUESTS_STREAM)
        assertEquals("jobs:responses", QueueNames.RESPONSES_STREAM)
        assertEquals("printscript-workers", QueueNames.PRINTSCRIPT_WORKER_GROUP)
        assertEquals("snippet-service-workers", QueueNames.SNIPPET_SERVICE_GROUP)
    }
}
