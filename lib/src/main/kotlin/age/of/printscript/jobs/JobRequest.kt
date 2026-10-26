package age.of.printscript.jobs

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ValidateJobRequest::class, name = "VALIDATE"),
    JsonSubTypes.Type(value = ExecuteJobRequest::class, name = "EXECUTE"),
    JsonSubTypes.Type(value = TestJobRequest::class, name = "TEST"),
    JsonSubTypes.Type(value = LintJobRequest::class, name = "LINT"),
    JsonSubTypes.Type(value = FormatJobRequest::class, name = "FORMAT"),
)
sealed interface JobRequest {
    val jobId: String
    val snippetId: String
    val snippetUrl: String
    val languageVersion: String
    val schemaVersion: Int
}

data class ValidateJobRequest(
    override val jobId: String,
    override val snippetId: String,
    override val snippetUrl: String,
    override val languageVersion: String,
    override val schemaVersion: Int = 1,
) : JobRequest

data class ExecuteJobRequest(
    override val jobId: String,
    override val snippetId: String,
    override val snippetUrl: String,
    override val languageVersion: String,
    val inputs: List<String> = emptyList(),
    override val schemaVersion: Int = 1,
) : JobRequest

data class TestJobRequest(
    override val jobId: String,
    override val snippetId: String,
    override val snippetUrl: String,
    override val languageVersion: String,
    val inputs: List<String> = emptyList(),
    val expectedOutputs: List<String> = emptyList(),
    override val schemaVersion: Int = 1,
) : JobRequest

data class LintJobRequest(
    override val jobId: String,
    override val snippetId: String,
    override val snippetUrl: String,
    override val languageVersion: String,
    val rules: Map<String, String?> = emptyMap(),
    override val schemaVersion: Int = 1,
) : JobRequest

data class FormatJobRequest(
    override val jobId: String,
    override val snippetId: String,
    override val snippetUrl: String,
    override val languageVersion: String,
    val rules: Map<String, String?> = emptyMap(),
    override val schemaVersion: Int = 1,
) : JobRequest
