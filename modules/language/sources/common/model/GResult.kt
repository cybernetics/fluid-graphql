package io.fluidsonic.graphql


sealed class GResult<out Value>(
	val errors: List<GError>
) {

	abstract override fun equals(other: Any?): Boolean
	abstract override fun hashCode(): Int
	abstract override fun toString(): String


	fun valueOrNull(): Value? = when (this) {
		is Success -> value
		is Failure -> null
	}


	fun valueOrThrow(): Value = when (this) {
		is Success -> value
		is Failure -> throw GErrorException(errors)
	}


	fun valueWithoutErrorsOrNull(): Value? = when (this) {
		is Success -> if (errors.isEmpty()) value else null
		is Failure -> null
	}


	fun valueWithoutErrorsOrThrow(): Value = when (this) { // FIXME refactor
		is Success -> if (errors.isEmpty()) value else throw GErrorException(errors)
		is Failure -> throw GErrorException(errors)
	}


	companion object {

		private val nullResult = Success(null)


		inline fun <Value> catch(action: () -> Value): GResult<Value> =
			try {
				Success(action())
			}
			catch (error: GError) {
				Failure(error)
			}


		fun failure(errors: List<GError> = emptyList()): GResult<Nothing> =
			Failure(errors)


		fun failure(error: GError): GResult<Nothing> =
			Failure(error)


		fun success(): GResult<Nothing?> =
			success(value = null)


		@Suppress("UNCHECKED_CAST")
		fun <Value> success(value: Value, errors: List<GError> = emptyList()): GResult<Value> =
			when {
				(value as Any?) == null && errors.isEmpty() -> nullResult as Success<Value>
				else -> Success(value = value, errors = errors)
			}
	}


	@PublishedApi
	internal class Failure(
		errors: List<GError>
	) : GResult<Nothing>(errors = errors) {

		init {
			require(errors.isNotEmpty()) { "'errors' must not be empty in the failure case." }
		}


		constructor(error: GError) :
			this(listOf(error))


		override fun equals(other: Any?) =
			this === other || (other is Failure && errors == other.errors)


		override fun hashCode() =
			errors.hashCode()


		override fun toString() =
			"Failure($errors)"
	}


	@PublishedApi
	internal class Success<Value>(
		val value: Value,
		errors: List<GError> = emptyList()
	) : GResult<Value>(errors = errors) {

		init {
			require(value !is GResult<*>) { "A GResult cannot be the value of another GResult." }
		}


		override fun equals(other: Any?) =
			this === other || (other is Success<*> && value == other.value && errors == other.errors)


		override fun hashCode() =
			value.hashCode() xor errors.hashCode()


		override fun toString() = buildString {
			append("Success(")
			append(value)

			if (errors.isNotEmpty()) {
				append(", errors = ")
				append(errors)
			}

			append(")")
		}
	}
}


inline fun <Value> GResult<Value>.ifErrors(action: (result: List<GError>) -> Nothing): Value =
	when {
		errors.isNotEmpty() -> action(errors)
		else -> (this as GResult.Success).value
	}


inline fun <Value> GResult<Value>.flatMapErrors(action: (errors: List<GError>) -> GResult<Value>): GResult<Value> =
	when (this) {
		is GResult.Failure -> action(errors)
		is GResult.Success -> this
	}


inline fun <Value, TransformedValue> GResult<Value>.flatMapValue(action: (value: Value) -> GResult<TransformedValue>): GResult<TransformedValue> =
	when (this) {
		is GResult.Success -> {
			val transformed = action(value)
			when {
				errors.isNotEmpty() -> transformed.mapErrors { transformedErrors ->
					when {
						transformedErrors.isNotEmpty() -> errors + transformedErrors
						else -> errors
					}
				}
				else -> transformed
			}
		}
		is GResult.Failure -> this
	}


inline fun <Value> GResult<Value>.mapErrors(action: (errors: List<GError>) -> List<GError>): GResult<Value> =
	when (this) {
		is GResult.Failure -> GResult.failure(action(errors))
		is GResult.Success -> GResult.success(value = value, errors = action(errors))
	}


inline fun <Value, TransformedValue> GResult<Value>.mapValue(action: (value: Value) -> TransformedValue): GResult<TransformedValue> =
	when (this) {
		is GResult.Success -> GResult.success(value = action(value), errors = errors)
		is GResult.Failure -> GResult.failure(errors)
	}


fun <Value> Iterable<GResult<Value>>.flatten(): GResult<List<Value>> {
	val errors = mutableListOf<GError>()
	val values = mutableListOf<Value>()
	var isFailure = false

	forEach { result ->
		errors += result.errors

		when (result) {
			is GResult.Success -> values += result.value
			is GResult.Failure -> isFailure = true
		}
	}

	return when (isFailure) {
		true -> GResult.failure(errors)
		false -> GResult.success(value = values, errors = errors)
	}
}


fun <Key, Value> Map<Key, GResult<Value>>.flatten(): GResult<Map<Key, Value>> {
	val errors = mutableListOf<GError>()
	val values = mutableMapOf<Key, Value>()
	var isFailure = false

	forEach { (key, result) ->
		errors += result.errors

		when (result) {
			is GResult.Success -> values[key] = result.value
			is GResult.Failure -> isFailure = true
		}
	}

	return when (isFailure) {
		true -> GResult.failure(errors)
		false -> GResult.success(value = values, errors = errors)
	}
}
