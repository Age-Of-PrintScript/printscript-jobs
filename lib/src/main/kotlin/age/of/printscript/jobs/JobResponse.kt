package age.of.printscript.jobs

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ValidateJobResponse::class, name = "VALIDATE"),
    JsonSubTypes.Type(value = ExecuteJobResponse::class, name = "EXECUTE"),
    JsonSubTypes.Type(value = TestJobResponse::class, name = "TEST"),
    JsonSubTypes.Type(value = LintJobResponse::class, name = "LINT"),
    JsonSubTypes.Type(value = FormatJobResponse::class, name = "FORMAT"),
    JsonSubTypes.Type(value = SystemErrorJobResponse::class, name = "SYSTEM_ERROR"),
)
sealed interface JobResponse {
    val jobId: String
    val schemaVersion: Int
}

data class ValidationErrorDto(
    val message: String,
)

data class PositionDto(
    val line: Int,
    val charPosition: Int,
)

data class LintWarningDto(
    val message: String,
    val positionDto: PositionDto,
)

data class ValidateJobResponse(
    override val jobId: String,
    val isValid: Boolean,
    val errors: List<ValidationErrorDto> = emptyList(),
    override val schemaVersion: Int = 1,
) : JobResponse

data class ExecuteJobResponse(
    override val jobId: String,
    val outputs: List<String> = emptyList(),
    override val schemaVersion: Int = 1,
) : JobResponse

data class TestJobResponse(
    override val jobId: String,
    val success: Boolean,
    val message: String? = null,
    override val schemaVersion: Int = 1,
) : JobResponse

data class LintJobResponse(
    override val jobId: String,
    val warnings: List<LintWarningDto> = emptyList(),
    override val schemaVersion: Int = 1,
) : JobResponse

data class FormatJobResponse(
    override val jobId: String,
    override val schemaVersion: Int = 1,
) : JobResponse

data class SystemErrorJobResponse(
    override val jobId: String,
    val reason: String,
    override val schemaVersion: Int = 1,
) : JobResponse
