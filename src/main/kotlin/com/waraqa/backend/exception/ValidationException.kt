package com.waraqa.backend.exception

class ValidationException(val errors: Map<String, String>) : RuntimeException("Validation failed")