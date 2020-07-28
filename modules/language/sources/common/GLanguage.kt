package io.fluidsonic.graphql


// https://graphql.github.io/graphql-spec/June2018/
object GLanguage {

	const val defaultQueryTypeName = "Query"
	const val defaultMutationTypeName = "Mutation"
	const val defaultSubscriptionTypeName = "Subscription"

	private const val introspectionNamePrefix = "__"
	private val nameRegex = Regex("[_A-Za-z][_0-9A-Za-z]*")


	// FIXME add default directives to schemas
	val defaultDeprecatedDirective = GDirectiveDefinition(
		name = "deprecated",
		argumentDefinitions = listOf(
			GDirectiveArgumentDefinition(
				name = "reason",
				type = GStringTypeRef,
				description = "Explains why this element was deprecated, usually also including a suggestion for how to access supported similar data. " +
					"Formatted using the Markdown syntax (as specified by [CommonMark](https://commonmark.org/).",
				defaultValue = GStringValue("No longer supported")
			)
		),
		description = "Marks an element of a GraphQL schema as no longer supported.",
		locations = setOf(
			GDirectiveLocation.ENUM_VALUE,
			GDirectiveLocation.FIELD_DEFINITION
		)
	)


	val defaultIncludeDirective = GDirectiveDefinition(
		name = "include",
		argumentDefinitions = listOf(
			GDirectiveArgumentDefinition(
				name = "if",
				type = GBooleanTypeRef.nonNullableRef,
				description = "Included when true."
			)
		),
		description = "Directs the executor to include this field or fragment only when the `if` argument is true.",
		locations = setOf(
			GDirectiveLocation.FIELD,
			GDirectiveLocation.FRAGMENT_SPREAD,
			GDirectiveLocation.INLINE_FRAGMENT
		)
	)


	val defaultSkipDirective = GDirectiveDefinition(
		name = "skip",
		argumentDefinitions = listOf(
			GDirectiveArgumentDefinition(
				name = "if",
				type = GBooleanTypeRef.nonNullableRef,
				description = "Skipped when true."
			)
		),
		description = "Directs the executor to skip this field or fragment when the `if` argument is true.",
		locations = setOf(
			GDirectiveLocation.FIELD,
			GDirectiveLocation.FRAGMENT_SPREAD,
			GDirectiveLocation.INLINE_FRAGMENT
		)
	)


	// FIXME move to raptor
//	fun isRepresentable(clazz: KClass<*>): Boolean =
//		when (clazz) {
//			Any::class, Nothing::class -> false
//			else -> clazz.typeParameters.isEmpty() // FIXME avoid kotlin-reflect
//		}
//
//
//	fun isRepresentable(type: KType): Boolean =
//		when (val classifier = type.classifier) {
//			Collection::class, List::class, Set::class -> isRepresentable(type.arguments.first())
//			is KClass<*> -> isRepresentable(classifier)
//			else -> false
//		}
//
//
//	fun isRepresentable(typeProjection: KTypeProjection): Boolean =
//		when (val type = typeProjection.type) {
//			null -> false
//			else -> isRepresentable(type)
//		}


	// https://graphql.github.io/graphql-spec/June2018/#sec-Enum-Value
	// https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names
	fun isValidEnumValue(name: String) =
		!name.startsWith(introspectionNamePrefix) &&
			name != "true" &&
			name != "false" &&
			name != "null" &&
			isValidName(name)


	// https://graphql.github.io/graphql-spec/June2018/#FieldDefinition
	// https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names
	fun isValidFieldName(name: String) =
		!name.startsWith(introspectionNamePrefix) &&
			isValidName(name)


	// https://graphql.github.io/graphql-spec/June2018/#InputValueDefinition
	// https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names
	fun isValidInputValueName(name: String) =
		!name.startsWith(introspectionNamePrefix) &&
			isValidName(name)


	// https://graphql.github.io/graphql-spec/June2018/#Name
	// https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names
	fun isValidIntrospectionName(name: String) =
		name.startsWith(introspectionNamePrefix) &&
			isValidName(name)


	// https://graphql.github.io/graphql-spec/June2018/#Name
	// https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names
	fun isValidName(name: String) =
		nameRegex matches name


	// https://graphql.github.io/graphql-spec/June2018/#TypeDefinition
	// https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names
	fun isValidTypeName(name: String) =
		!name.startsWith(introspectionNamePrefix) &&
			isValidName(name)
}