package age.of.printscript.jobs

object QueueNames {
    const val REQUESTS_STREAM = "jobs:requests"
    const val RESPONSES_STREAM = "jobs:responses"

    const val PRINTSCRIPT_WORKER_GROUP = "printscript-workers"
    const val SNIPPET_SERVICE_GROUP = "snippet-service-workers"
}

const val PAYLOAD_FIELD = "payload"
