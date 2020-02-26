package io.fluidsonic.graphql

import kotlin.reflect.*


// FIXME hashCode
sealed class GNode(
	val extensions: Map<ExtensionKey<*>, *>,
	val origin: GOrigin?
) {

	fun childAt(index: Int): GNode? {
		var childIndex = 0

		forEachChild { child ->
			if (childIndex == index)
				return child

			childIndex += 1
		}

		return null
	}


	fun children(): List<GNode> {
		var list: MutableList<GNode>? = null

		forEachChild { child ->
			(list ?: mutableListOf<GNode>().also { list = it })
				.add(child)
		}

		return list.orEmpty()
	}


	fun countChildren(): Int {
		var count = 0
		forEachChild { count += 1 }

		return count
	}


	abstract fun equalsNode(other: GNode, includingOrigin: Boolean = false): Boolean


	@PublishedApi
	internal inline fun forNode(node: GNode?, block: (node: GNode) -> Unit) {
		if (node !== null)
			block(node)
	}


	@PublishedApi
	internal inline fun forNodes(nodes: List<GNode>, block: (node: GNode) -> Unit) {
		for (node in nodes)
			block(node)
	}


	inline fun forEachChild(block: (child: GNode) -> Unit) {
		@Suppress("UNUSED_VARIABLE") // Exhaustiveness check.
		val exhaustive = when (this) {
			is GArgument -> {
				forNode(nameNode, block)
				forNode(value, block)
			}

			is GArgumentDefinition -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNode(type, block)
				forNode(defaultValue, block)
				forNodes(directives, block)
			}

			is GBooleanValue ->
				Unit

			is GDirective -> {
				forNode(nameNode, block)
				forNodes(arguments, block)
			}


			is GDirectiveDefinition -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(argumentDefinitions, block)
				forNodes(locationNodes, block)
			}

			is GDocument ->
				forNodes(definitions, block)

			is GEnumType -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(directives, block)
				forNodes(values, block)
			}

			is GEnumTypeExtension -> {
				forNode(nameNode, block)
				forNodes(directives, block)
				forNodes(values, block)
			}

			is GEnumValue ->
				Unit

			is GEnumValueDefinition -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(directives, block)
			}

			is GFieldDefinition -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(argumentDefinitions, block)
				forNode(type, block)
				forNodes(directives, block)
			}

			is GFieldSelection -> {
				forNode(aliasNode, block)
				forNode(nameNode, block)
				forNodes(arguments, block)
				forNodes(directives, block)
				forNode(selectionSet, block)
			}

			is GFloatValue ->
				Unit

			is GFragmentDefinition -> {
				forNode(nameNode, block)
				forNodes(variableDefinitions, block)
				forNode(typeCondition, block)
				forNodes(directives, block)
				forNode(selectionSet, block)
			}

			is GFragmentSelection -> {
				forNode(nameNode, block)
				forNodes(directives, block)
			}

			is GInlineFragmentSelection -> {
				forNode(typeCondition, block)
				forNodes(directives, block)
				forNode(selectionSet, block)
			}

			is GInputObjectType -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(directives, block)
				forNodes(argumentDefinitions, block)
			}

			is GInputObjectTypeExtension -> {
				forNode(nameNode, block)
				forNodes(directives, block)
				forNodes(argumentDefinitions, block)
			}

			is GIntValue ->
				Unit

			is GInterfaceType -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(interfaces, block)
				forNodes(directives, block)
				forNodes(fieldDefinitions, block)
			}

			is GInterfaceTypeExtension -> {
				forNode(nameNode, block)
				forNodes(interfaces, block)
				forNodes(directives, block)
				forNodes(fieldDefinitions, block)
			}

			is GListType ->
				forNode(elementType, block)

			is GListTypeRef ->
				forNode(elementType, block)

			is GListValue ->
				forNodes(elements, block)

			is GName ->
				Unit

			is GNamedTypeRef ->
				forNode(nameNode, block)

			is GNonNullType ->
				forNode(nullableType, block)

			is GNonNullTypeRef ->
				forNode(nullableRef, block)

			is GNullValue ->
				Unit

			is GObjectType -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(interfaces, block)
				forNodes(directives, block)
				forNodes(fieldDefinitions, block)
			}

			is GObjectTypeExtension -> {
				forNode(nameNode, block)
				forNodes(interfaces, block)
				forNodes(directives, block)
				forNodes(fieldDefinitions, block)
			}

			is GObjectValue ->
				forNodes(fields, block)

			is GObjectValueField -> {
				forNode(nameNode, block)
				forNode(value, block)
			}

			is GOperationDefinition -> {
				forNode(nameNode, block)
				forNodes(variableDefinitions, block)
				forNodes(directives, block)
				forNode(selectionSet, block)
			}

			is GOperationTypeDefinition ->
				forNode(type, block)

			is GScalarType -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(directives, block)
			}

			is GScalarTypeExtension -> {
				forNode(nameNode, block)
				forNodes(directives, block)
			}

			is GSchemaDefinition -> {
				forNodes(directives, block)
				forNodes(operationTypeDefinitions, block)
			}

			is GSchemaExtension -> {
				forNodes(directives, block)
				forNodes(operationTypeDefinitions, block)
			}

			is GSelectionSet ->
				forNodes(selections, block)

			is GStringValue ->
				Unit

			is GUnionType -> {
				forNode(descriptionNode, block)
				forNode(nameNode, block)
				forNodes(directives, block)
				forNodes(possibleTypes, block)
			}

			is GUnionTypeExtension -> {
				forNode(nameNode, block)
				forNodes(directives, block)
				forNodes(possibleTypes, block)
			}

			is GVariableDefinition -> {
				forNode(nameNode, block)
				forNode(type, block)
				forNode(defaultValue, block)
				forNodes(directives, block)
			}

			is GVariableRef ->
				forNode(nameNode, block)
		}
	}


	// FIXME Is there a good way to make this.extensions type-safe without making the API too complicated?
	operator fun <Value : Any> get(extensionKey: ExtensionKey<Value>) =
		extensions[extensionKey] as Value?


	fun hasChildren(): Boolean {
		forEachChild { return true }

		return false
	}


	override fun toString() =
		print(this)


	companion object {

		fun print(node: GNode, indent: String = "\t") =
			Printer.print(node = node, indent = indent)
	}


	interface ExtensionKey<Value : Any>


	interface WithArguments {

		val arguments: List<GArgument>

		fun argument(name: String) =
			arguments.firstOrNull { it.name == name }
	}


	interface WithArgumentDefinitions {

		val argumentDefinitions: List<GArgumentDefinition>


		fun argumentDefinition(name: String) =
			argumentDefinitions.firstOrNull { it.name == name }
	}


	interface WithDirectives {

		val directives: List<GDirective>


		fun directive(name: String) =
			directives.firstOrNull { it.name == name }


		fun directives(name: String) =
			directives.filter { it.name == name }
	}


	interface WithFieldDefinitions {

		val fieldDefinitions: List<GFieldDefinition>


		fun field(name: String) =
			fieldDefinitions.firstOrNull { it.name == name }
	}


	interface WithInterfaces {

		val interfaces: List<GNamedTypeRef>
	}


	interface WithName : WithOptionalName {

		override val name
			get() = nameNode.value


		override val nameNode: GName
	}


	interface WithOperationTypeDefinitions {

		val operationTypeDefinitions: List<GOperationTypeDefinition>


		fun operationTypeDefinition(operationType: GOperationType) =
			operationTypeDefinitions.firstOrNull { it.operationType == operationType }
	}


	interface WithOptionalDeprecation : WithDirectives, WithName {

		val deprecation
			get() = directive(GSpecification.defaultDeprecatedDirective.name)


		val deprecationReason
			get() = (deprecation?.argument("reason")?.value as? GStringValue)?.value
	}


	interface WithOptionalDescription {

		val description
			get() = descriptionNode?.value


		val descriptionNode: GStringValue?
	}


	interface WithOptionalName {

		val name
			get() = nameNode?.value


		val nameNode: GName?
	}


	interface WithVariableDefinitions {

		val variableDefinitions: List<GVariableDefinition>


		fun variableDefinition(name: String) =
			variableDefinitions.firstOrNull { it.name == name }
	}
}


fun GNode?.equalsNode(other: GNode?, includingOrigin: Boolean = false) =
	this === other || (this !== null && other !== null && equalsNode(other, includingOrigin = includingOrigin))


fun List<GNode?>.equalsNode(other: List<GNode?>, includingOrigin: Boolean): Boolean {
	if (this === other)
		return true

	if (size != other.size)
		return false

	forEachIndexed { index, ast ->
		if (!ast.equalsNode(other[index], includingOrigin = includingOrigin))
			return false
	}

	return true
}


sealed class GAbstractType(
	description: GStringValue?,
	directives: List<GDirective>,
	extensions: Map<ExtensionKey<*>, *>,
	kind: Kind,
	name: GName,
	origin: GOrigin?
) : GCompositeType(
	description = description,
	directives = directives,
	extensions = extensions,
	kind = kind,
	name = name,
	origin = origin
) {

	companion object
}


class GArgument(
	name: GName,
	val value: GValue,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithName {

	override val nameNode = name


	constructor(
		name: String,
		value: GValue
	) : this(
		name = GName(name),
		value = value
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GArgument &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				value.equalsNode(other.value, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


sealed class GArgumentDefinition(
	val defaultValue: GValue?,
	description: GStringValue?,
	override val directives: List<GDirective>,
	extensions: Map<ExtensionKey<*>, *>,
	name: GName,
	origin: GOrigin?,
	val type: GTypeRef
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithName,
	GNode.WithOptionalDescription {

	override val descriptionNode = description
	override val nameNode = name


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GArgumentDefinition &&
				defaultValue.equalsNode(other.defaultValue, includingOrigin = includingOrigin) &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				type.equalsNode(other.type, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	fun isOptional() =
		!isRequired()


	fun isRequired() =
		type is GNonNullTypeRef && defaultValue === null


	companion object
}


// https://graphql.github.io/graphql-spec/draft/#sec-Boolean.Input-Coercion
object GBooleanType : GScalarType(
	name = "Boolean",
	parseValue = { it as? Boolean },
	parseValueNode = { (it as? GBooleanValue)?.value },
	serializeValue = { it as? Boolean }
)


class GBooleanValue(
	val value: Boolean,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.BOOLEAN


	override fun equals(other: Any?) =
		this === other || (other is GBooleanValue && value == other.value)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GBooleanValue &&
				value == other.value &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		value.hashCode()


	override fun unwrap() =
		value


	companion object
}


sealed class GCompositeType(
	description: GStringValue?,
	directives: List<GDirective>,
	extensions: Map<ExtensionKey<*>, *>,
	kind: Kind,
	name: GName,
	origin: GOrigin?
) : GNamedType(
	description = description,
	directives = directives,
	extensions = extensions,
	kind = kind,
	name = name,
	origin = origin
) {

	companion object
}


// https://graphql.github.io/graphql-spec/draft/#sec-Scalars.Input-Coercion
class GCustomScalarType(
	name: GName,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	parseValue: (GCoercionContext<*>.(value: Any) -> Any?)? = Any?::identity,
	parseValueNode: (GCoercionContext<*>.(value: GValue) -> Any?)? = parseValue?.let {
		{ value -> value.unwrap()?.let { parseValue(it) } }
	},
	serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)? = Any?::identity,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GScalarType(
	description = description,
	directives = directives,
	extensions = extensions,
	name = name,
	parseValue = parseValue,
	parseValueNode = parseValueNode,
	serializeValue = serializeValue,
	origin = origin
) {

	constructor(
		name: String,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		parseValue: (GCoercionContext<*>.(value: Any) -> Any?)? = Any?::identity,
		parseValueNode: (GCoercionContext<*>.(value: GValue) -> Any?)? = parseValue?.let {
			{ value -> value.unwrap()?.let { parseValue(it) } }
		},
		serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)? = Any?::identity,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		description = description?.let { GStringValue(it) },
		directives = directives,
		parseValue = parseValue,
		parseValueNode = parseValueNode,
		serializeValue = serializeValue,
		extensions = extensions
	)


	companion object
}


sealed class GDefinition(
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) : GNode(
	extensions = extensions,
	origin = origin
) {

	companion object
}


class GDirective(
	name: GName,
	override val arguments: List<GArgument> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithArguments,
	GNode.WithName {

	override val nameNode = name


	constructor(
		name: String,
		arguments: List<GArgument> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		arguments = arguments,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GDirective &&
				arguments.equalsNode(other.arguments, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GDirectiveArgumentDefinition(
	name: GName,
	type: GTypeRef,
	defaultValue: GValue? = null,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GArgumentDefinition(
	defaultValue = defaultValue,
	description = description,
	directives = directives,
	extensions = extensions,
	name = name,
	type = type,
	origin = origin
) {

	constructor(
		name: String,
		type: GTypeRef,
		defaultValue: GValue? = null,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		type = type,
		defaultValue = defaultValue,
		description = description?.let { GStringValue(it) },
		directives = directives,
		extensions = extensions
	)

	companion object
}


class GDirectiveDefinition(
	name: GName,
	locations: List<GName>,
	val isRepeatable: Boolean = false,
	override val argumentDefinitions: List<GDirectiveArgumentDefinition> = emptyList(),
	description: GStringValue? = null,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GTypeSystemDefinition(
		extensions = extensions,
		origin = origin
	),
	GNode.WithArgumentDefinitions,
	GNode.WithName,
	GNode.WithOptionalDescription {

	val locations: Set<GDirectiveLocation> = locations.mapNotNullTo(mutableSetOf()) { node ->
		GDirectiveLocation.values().firstOrNull { it.name == node.value }
	}
	val locationNodes = locations

	override val descriptionNode = description
	override val nameNode = name


	constructor(
		name: String,
		locations: Set<GDirectiveLocation>,
		isRepeatable: Boolean = false,
		argumentDefinitions: List<GDirectiveArgumentDefinition> = emptyList(),
		description: String? = null,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		locations = locations.map { GName(it.name) },
		isRepeatable = isRepeatable,
		argumentDefinitions = argumentDefinitions,
		description = description?.let { GStringValue(it) },
		extensions = extensions
	)


	override fun argumentDefinition(name: String) =
		argumentDefinitions.firstOrNull { it.name == name }


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GDirectiveDefinition &&
				argumentDefinitions.equalsNode(other.argumentDefinitions, includingOrigin = includingOrigin) &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				isRepeatable == other.isRepeatable &&
				locationNodes.equalsNode(other.locationNodes, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GDocument(
	val definitions: List<GDefinition>,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GNode(
	extensions = extensions,
	origin = origin
) {

	// FIXME this is confusing. It may indicate that this is the schema related to this document instead of representing the
	// type definitions within this document.
	val schema = GSchema(document = this) // FIXME check if cyclic reference is OK here


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GDocument &&
				definitions.equalsNode(other.definitions, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	// FIXME move to extension
	// FIXME add a way to execute and returning either data or errors rather than a response containing serialized errors
	suspend fun <Environment : Any> execute(
		schema: GSchema,
		rootResolver: GRootResolver<Environment>,
		environment: Environment,
		operationName: String? = null,
		variableValues: Map<String, Any?> = emptyMap(),
		defaultResolver: GFieldResolver<Environment, Any>? = null,
		nodeInputCoercion: GNodeInputCoercion<Environment> = GNodeInputCoercion.default()
	) =
		Executor.create(
				schema = schema,
				document = this,
				environment = environment,
				rootResolver = rootResolver,
				operationName = operationName,
				variableValues = variableValues,
				defaultResolver = defaultResolver,
				nodeInputCoercion = nodeInputCoercion
			)
			.consumeErrors { throw it.errors.first() } // FIXME ??
			.execute()


	// FIXME move to extension
	// FIXME add a way to execute and returning either data or errors rather than a response containing serialized errors
	suspend fun execute(
		schema: GSchema,
		rootResolver: GRootResolver<Unit>,
		operationName: String? = null,
		variableValues: Map<String, Any?> = emptyMap(),
		defaultResolver: GFieldResolver<Unit, Any>? = null,
		nodeInputCoercion: GNodeInputCoercion<Unit> = GNodeInputCoercion.default()
	) =
		execute(
			schema = schema,
			rootResolver = rootResolver,
			environment = Unit,
			operationName = operationName,
			variableValues = variableValues,
			defaultResolver = defaultResolver,
			nodeInputCoercion = nodeInputCoercion
		)


	fun fragment(name: String): GFragmentDefinition? {
		for (definition in definitions)
			if (definition is GFragmentDefinition && definition.name == name)
				return definition

		return null
	}


	fun operation(name: String?): GOperationDefinition? {
		for (definition in definitions)
			if (definition is GOperationDefinition && definition.name == name)
				return definition

		return null
	}


	companion object {

		fun parse(source: GSource.Parsable) =
			Parser.parseDocument(source)


		fun parse(content: String, name: String = "<document>") =
			parse(GSource.of(content = content, name = name))
	}
}


// FIXME better default parsers & serializers with better errors
// https://graphql.github.io/graphql-spec/June2018/#sec-Enums
// https://graphql.github.io/graphql-spec/draft/#sec-Enums.Input-Coercion
class GEnumType(
	name: GName,
	val values: List<GEnumValueDefinition>,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	parseValue: (GCoercionContext<*>.(value: Any) -> Any?)? = { value ->
		(value as? String)?.takeIf { valueName -> values.any { it.name == valueName } }
	},
	parseValueNode: (GCoercionContext<*>.(value: GValue) -> Any?)? = parseValue?.let {
		{ value ->
			(value as? GEnumValue)
				.ifNull { error("GraphQL enum '${name.value}' expects an enum value literal but got: $value") }
				.let { parseValue(it) }
		}
	},
	serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)? = { value ->
		(value as? String)
			.ifNull { error("The default serializer for GraphQL enum '${name.value}' expects a String value but got ${value::class}: $value") }
			.also { valueName ->
				if (values.none { it.name == valueName })
					error("'$valueName' is not a valid value for GraphQL enum '${name.value}'.")
			}
	},
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GLeafType(
	description = description,
	directives = directives,
	extensions = extensions,
	kind = Kind.ENUM,
	name = name,
	parseValue = parseValue,
	parseValueNode = parseValueNode,
	serializeValue = serializeValue,
	origin = origin
) {

	// FIXME dedup parse/serialize (and in other classes)
	constructor(
		name: String,
		values: List<GEnumValueDefinition>,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		parseValue: (GCoercionContext<*>.(value: Any) -> Any?)? = { value ->
			(value as? String)?.takeIf { valueName -> values.any { it.name == valueName } }
		},
		parseValueNode: (GCoercionContext<*>.(value: GValue) -> Any?)? = parseValue?.let {
			{ value ->
				(value as? GEnumValue)
					.ifNull { error("GraphQL enum '$name' expects an enum value literal but got: $value") }
					.let { parseValue(it.name) }
			}
		},
		serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)? = { value ->
			(value as? String)
				.ifNull { error("The default serializer for GraphQL enum '$name' expects a String value but got ${value::class}: $value") }
				.also { valueName ->
					if (values.none { it.name == valueName })
						error("'$valueName' is not a valid value for GraphQL enum '$name'.")
				}
		},
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		values = values,
		description = description?.let { GStringValue(it) },
		directives = directives,
		parseValue = parseValue,
		parseValueNode = parseValueNode,
		serializeValue = serializeValue,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GEnumType &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				values.equalsNode(other.values, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun isSupertypeOf(other: GType) =
		other === this ||
			(other is GNonNullType && other.nullableType === this)


	fun value(name: String) =
		values.firstOrNull { it.name == name }


	companion object
}


class GEnumTypeExtension(
	name: GName,
	val values: List<GEnumValueDefinition>,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GTypeExtension(
	directives = directives,
	extensions = extensions,
	name = name,
	origin = origin
) {

	constructor(
		name: String,
		values: List<GEnumValueDefinition>,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		values = values,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GEnumTypeExtension &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				values.equalsNode(other.values, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	fun value(name: String) =
		values.firstOrNull { it.name == name }


	companion object
}


class GEnumValue(
	val name: String,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.ENUM


	override fun equals(other: Any?) =
		this === other || (other is GEnumValue && name == other.name)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GEnumValue &&
				name == other.name &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		name.hashCode()


	override fun unwrap() =
		name


	companion object
}


class GEnumValueDefinition(
	name: GName,
	description: GStringValue? = null,
	override val directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithOptionalDeprecation,
	GNode.WithOptionalDescription {

	override val descriptionNode = description
	override val nameNode = name


	constructor(
		name: String,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		description = description?.let { GStringValue(it) },
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GEnumValueDefinition &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


sealed class GExecutableDefinition(
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) : GDefinition(
	extensions = extensions,
	origin = origin
) {

	companion object
}


class GFieldArgumentDefinition(
	name: GName,
	type: GTypeRef,
	defaultValue: GValue? = null,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GArgumentDefinition(
	name = name,
	type = type,
	defaultValue = defaultValue,
	description = description,
	directives = directives,
	origin = origin,
	extensions = extensions
) {

	constructor(
		name: String,
		type: GTypeRef,
		defaultValue: GValue? = null,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		type = type,
		defaultValue = defaultValue,
		description = description?.let { GStringValue(it) },
		directives = directives,
		extensions = extensions
	)

	companion object
}


class GFieldDefinition(
	name: GName,
	val type: GTypeRef,
	override val argumentDefinitions: List<GFieldArgumentDefinition> = emptyList(),
	description: GStringValue? = null,
	override val directives: List<GDirective> = emptyList(),
	val resolver: GFieldResolver<*, *>? = null,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithArgumentDefinitions,
	GNode.WithOptionalDescription,
	GNode.WithOptionalDeprecation {

	override val descriptionNode = description
	override val nameNode = name


	constructor(
		name: String,
		type: GTypeRef,
		argumentDefinitions: List<GFieldArgumentDefinition> = emptyList(),
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		resolver: GFieldResolver<*, *>? = null,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		type = type,
		argumentDefinitions = argumentDefinitions,
		description = description?.let { GStringValue(it) },
		directives = directives,
		resolver = resolver,
		extensions = extensions
	)


	override fun argumentDefinition(name: String) =
		argumentDefinitions.firstOrNull { it.name == name }


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GFieldDefinition &&
				argumentDefinitions.equalsNode(other.argumentDefinitions, includingOrigin = includingOrigin) &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				type.equalsNode(other.type, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GFieldSelection(
	name: GName,
	val selectionSet: GSelectionSet? = null,
	override val arguments: List<GArgument> = emptyList(),
	alias: GName? = null,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GSelection(
		directives = directives,
		extensions = extensions,
		origin = origin
	),
	GNode.WithArguments {

	val alias get() = aliasNode?.value
	val aliasNode = alias
	val name get() = nameNode.value
	val nameNode = name


	constructor(
		name: String,
		selectionSet: GSelectionSet? = null,
		arguments: List<GArgument> = emptyList(),
		alias: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		selectionSet = selectionSet,
		arguments = arguments,
		alias = alias?.let { GName(alias) },
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GFieldSelection &&
				aliasNode.equalsNode(other.aliasNode, includingOrigin = includingOrigin) &&
				arguments.equalsNode(other.arguments, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				selectionSet.equalsNode(other.selectionSet, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


// https://graphql.github.io/graphql-spec/draft/#sec-Float.Input-Coercion
object GFloatType : GScalarType(
	name = "Float",
	parseValue = { it.coerceToDoubleOrNull() },
	parseValueNode = { value ->
		when (value) {
			is GFloatValue -> value.value
			is GIntValue -> value.value.toDouble()
			else -> null
		}
	},
	serializeValue = { value ->
		when (value) {
			is Boolean -> if (value) 1.0 else 0.0
			is String -> value.toDoubleOrNull()
			else -> value.coerceToDoubleOrNull()
		}
	}
)


class GFloatValue(
	val value: Double,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.FLOAT


	override fun equals(other: Any?) =
		this === other || (other is GFloatValue && value == other.value)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GFloatValue &&
				value == other.value &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		value.hashCode()


	override fun unwrap() =
		value


	companion object
}


class GFragmentDefinition(
	name: GName,
	val typeCondition: GNamedTypeRef,
	val selectionSet: GSelectionSet,
	override val variableDefinitions: List<GVariableDefinition> = emptyList(),
	override val directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GExecutableDefinition(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithName,
	GNode.WithVariableDefinitions {

	override val nameNode = name


	constructor(
		name: String,
		typeCondition: GNamedTypeRef,
		selectionSet: GSelectionSet,
		variableDefinitions: List<GVariableDefinition> = emptyList(),
		directives: List<GDirective> = emptyList()
	) : this(
		name = GName(name),
		typeCondition = typeCondition,
		selectionSet = selectionSet,
		variableDefinitions = variableDefinitions,
		directives = directives
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GFragmentDefinition &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				selectionSet.equalsNode(other.selectionSet, includingOrigin = includingOrigin) &&
				typeCondition.equalsNode(other.typeCondition, includingOrigin = includingOrigin) &&
				variableDefinitions.equalsNode(other.variableDefinitions, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GFragmentSelection(
	name: GName,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GSelection(
		directives = directives,
		extensions = extensions,
		origin = origin
	) {

	val name get() = nameNode.value
	val nameNode = name


	constructor(
		name: String,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GFragmentSelection &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


// https://graphql.github.io/graphql-spec/draft/#sec-ID.Input-Coercion
object GIdType : GScalarType(
	name = "ID",
	parseValue = { value ->
		when (value) {
			is String -> value
			else -> value.coerceToIntOrNull()?.toString()
		}
	},
	parseValueNode = { value ->
		when (value) {
			is GIntValue -> value.value.toString()
			is GStringValue -> value.value
			else -> null
		}
	},
	serializeValue = { value ->
		when (value) {
			is String -> value
			else -> value.coerceToIntOrNull()?.toString()
		}
	}
)


class GInlineFragmentSelection(
	val selectionSet: GSelectionSet,
	val typeCondition: GNamedTypeRef?,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GSelection(
	directives = directives,
	extensions = extensions,
	origin = origin
) {

	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GInlineFragmentSelection &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				selectionSet.equalsNode(other.selectionSet, includingOrigin = includingOrigin) &&
				typeCondition.equalsNode(other.typeCondition, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GInputObjectArgumentDefinition(
	name: GName,
	type: GTypeRef,
	defaultValue: GValue? = null,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GArgumentDefinition(
	name = name,
	type = type,
	defaultValue = defaultValue,
	description = description,
	directives = directives,
	origin = origin,
	extensions = extensions
) {

	constructor(
		name: String,
		type: GTypeRef,
		defaultValue: GValue? = null,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		type = type,
		defaultValue = defaultValue,
		description = description?.let { GStringValue(it) },
		directives = directives,
		extensions = extensions
	)

	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Input-Objects
// https://graphql.github.io/graphql-spec/June2018/#sec-Input-Object
class GInputObjectType(
	name: GName,
	override val argumentDefinitions: List<GInputObjectArgumentDefinition>,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	val parseValue: (GNodeInputCoercionContext<*>.(arguments: Map<String, Any?>) -> Any?) = Any?::identity,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GCompositeType(
		description = description,
		directives = directives,
		extensions = extensions,
		kind = Kind.INPUT_OBJECT,
		name = name,
		origin = origin
	),
	GNode.WithArgumentDefinitions {

	constructor(
		name: String,
		argumentDefinitions: List<GInputObjectArgumentDefinition>,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		parseValue: (GCoercionContext<*>.(arguments: Map<String, Any?>) -> Any?) = Any?::identity,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		argumentDefinitions = argumentDefinitions,
		description = description?.let { GStringValue(it) },
		directives = directives,
		parseValue = parseValue,
		extensions = extensions
	)


	override fun argumentDefinition(name: String) =
		argumentDefinitions.firstOrNull { it.name == name }


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GInputObjectType &&
				argumentDefinitions.equalsNode(other.argumentDefinitions, includingOrigin = includingOrigin) &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun isSupertypeOf(other: GType) =
		other === this ||
			(other is GNonNullType && other.nullableType === this)


	companion object
}


class GInputObjectTypeExtension(
	name: GName,
	override val argumentDefinitions: List<GInputObjectArgumentDefinition> = emptyList(),
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GTypeExtension(
		directives = directives,
		extensions = extensions,
		name = name,
		origin = origin
	),
	GNode.WithArgumentDefinitions {

	constructor(
		name: String,
		argumentDefinitions: List<GInputObjectArgumentDefinition> = emptyList(),
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		argumentDefinitions = argumentDefinitions,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GInputObjectTypeExtension &&
				argumentDefinitions.equalsNode(other.argumentDefinitions, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


// https://graphql.github.io/graphql-spec/draft/#sec-Int.Input-Coercion
object GIntType : GScalarType(
	name = "Int",
	parseValue = { it.coerceToIntOrNull() },
	parseValueNode = { (it as? GIntValue)?.value },
	serializeValue = { value ->
		when (value) {
			is Boolean -> if (value) 1 else 0
			is String -> value.toIntOrNull()
			else -> value.coerceToIntOrNull()
		}
	}
)


class GIntValue(
	val value: Int,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.INT


	override fun equals(other: Any?) =
		this === other || (other is GIntValue && value == other.value)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GIntValue &&
				value == other.value &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		value.hashCode()


	override fun unwrap() =
		value


	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Interfaces
// https://graphql.github.io/graphql-spec/June2018/#sec-Interface
class GInterfaceType(
	name: GName,
	override val fieldDefinitions: List<GFieldDefinition>,
	override val interfaces: List<GNamedTypeRef> = emptyList(),
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GAbstractType(
		description = description,
		directives = directives,
		extensions = extensions,
		kind = Kind.INTERFACE,
		name = name,
		origin = origin
	),
	GNode.WithFieldDefinitions,
	GNode.WithInterfaces {

	constructor(
		name: String,
		fields: List<GFieldDefinition>,
		interfaces: List<GNamedTypeRef> = emptyList(),
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		fieldDefinitions = fields,
		interfaces = interfaces,
		description = description?.let { GStringValue(it) },
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GInterfaceType &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				fieldDefinitions.equalsNode(other.fieldDefinitions, includingOrigin = includingOrigin) &&
				interfaces.equalsNode(other.interfaces, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun isSupertypeOf(other: GType): Boolean =
		other === this ||
			(other is WithInterfaces && other.interfaces.any { it.name == name }) ||
			(other is GNonNullType && isSupertypeOf(other.nullableType))


	companion object
}


class GInterfaceTypeExtension(
	name: GName,
	override val fieldDefinitions: List<GFieldDefinition> = emptyList(),
	override val interfaces: List<GNamedTypeRef> = emptyList(),
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GTypeExtension(
		directives = directives,
		extensions = extensions,
		name = name,
		origin = origin
	),
	GNode.WithFieldDefinitions,
	GNode.WithInterfaces {


	constructor(
		name: String,
		fields: List<GFieldDefinition> = emptyList(),
		interfaces: List<GNamedTypeRef> = emptyList(),
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		fieldDefinitions = fields,
		interfaces = interfaces,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GInterfaceTypeExtension &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				fieldDefinitions.equalsNode(other.fieldDefinitions, includingOrigin = includingOrigin) &&
				interfaces.equalsNode(other.interfaces, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


sealed class GLeafType(
	name: GName,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	extensions: Map<ExtensionKey<*>, *>,
	kind: Kind,
	origin: GOrigin?,
	val parseValue: (GCoercionContext<*>.(value: Any) -> Any?)?,
	val parseValueNode: (GNodeInputCoercionContext<*>.(value: GValue) -> Any?)?,
	val serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)?
) : GNamedType(
	name = name,
	description = description,
	directives = directives,
	extensions = extensions,
	kind = kind,
	origin = origin
) {

	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Type-System.List
// https://graphql.github.io/graphql-spec/June2018/#sec-Type-Kinds.List
class GListType(
	elementType: GType,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GWrappingType(
	extensions = extensions,
	kind = Kind.LIST,
	wrappedType = elementType
) {

	val elementType get() = wrappedType

	override val name get() = "[${elementType.name}]"


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GListType &&
				elementType.equalsNode(other.elementType, includingOrigin = includingOrigin)
			)


	override fun isSupertypeOf(other: GType): Boolean =
		other === this ||
			(other is GListType && elementType.isSupertypeOf(other.elementType)) ||
			(other is GNonNullType && isSupertypeOf(other.nullableType))


	override fun toRef() =
		GListTypeRef(elementType.toRef())


	companion object
}


class GListTypeRef(
	val elementType: GTypeRef,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GTypeRef(
	extensions = extensions,
	origin = origin
) {

	override val underlyingName get() = elementType.underlyingName


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GListTypeRef &&
				elementType.equalsNode(other.elementType, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


fun GListTypeRef(
	name: String,
	extensions: Map<GNode.ExtensionKey<*>, *> = emptyMap<GNode.ExtensionKey<*>, Any>()
) =
	GListTypeRef(GNamedTypeRef(name, extensions = extensions), extensions = extensions)


class GListValue(
	val elements: List<GValue>,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.FLOAT


	override fun equals(other: Any?) =
		this === other || (other is GListValue && elements == other.elements)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GListValue &&
				elements.equalsNode(other.elements, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		elements.hashCode()


	override fun unwrap() =
		elements.map { it.unwrap() }


	companion object
}


class GName(
	val value: String,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GNode(
	extensions = extensions,
	origin = origin
) {

	override fun equals(other: Any?) =
		this === other || (other is GName && value == other.value)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GName &&
				value == other.value &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		value.hashCode()


	companion object
}


sealed class GNamedType(
	description: GStringValue?,
	override val directives: List<GDirective>,
	extensions: Map<ExtensionKey<*>, *>,
	kind: Kind,
	name: GName,
	origin: GOrigin?
) :
	GType(
		extensions = extensions,
		kind = kind,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithName,
	GNode.WithOptionalDescription {

	final override val descriptionNode = description
	final override val nameNode = name
	final override val underlyingNamedType get() = this


	override fun toRef() =
		GTypeRef(name)


	companion object
}


class GNamedTypeRef(
	name: GName,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GTypeRef(
	extensions = extensions,
	origin = origin
) {

	val name get() = nameNode.value
	val nameNode = name

	override val underlyingName get() = name


	constructor(
		name: String,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GNamedTypeRef &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Type-System.Non-Null
// https://graphql.github.io/graphql-spec/June2018/#sec-Type-Kinds.Non-Null
class GNonNullType(
	nullableType: GType,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GWrappingType(
	extensions = extensions,
	kind = Kind.NON_NULL,
	wrappedType = nullableType
) {

	override val name get() = "${nullableType.name}!"
	override val nonNullable get() = this
	override val nullableType get() = wrappedType


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GNonNullType &&
				nullableType.equalsNode(other.nullableType, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun isSupertypeOf(other: GType) =
		other === this ||
			(other is GNonNullType && nullableType.isSupertypeOf(other.nullableType))


	override fun toRef() =
		GNonNullTypeRef(nullableType.toRef())


	companion object
}


class GNonNullTypeRef(
	override val nullableRef: GTypeRef,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GTypeRef(
	extensions = extensions,
	origin = origin
) {

	override val nonNullableRef get() = this
	override val underlyingName get() = nullableRef.underlyingName


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GNonNullTypeRef &&
				nullableRef.equalsNode(other.nullableRef, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


fun GNonNullTypeRef(
	name: String,
	extensions: Map<GNode.ExtensionKey<*>, *> = emptyMap<GNode.ExtensionKey<*>, Any>()
) =
	GNonNullTypeRef(GNamedTypeRef(name, extensions = extensions), extensions = extensions)


class GNullValue(
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.NULL


	override fun equals(other: Any?) =
		this === other || other is GNullValue


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GNullValue &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		0


	override fun unwrap(): Nothing? =
		null


	companion object {

		val withoutOrigin = GNullValue()
	}
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Objects
// https://graphql.github.io/graphql-spec/June2018/#sec-Object
class GObjectType(
	name: GName,
	override val fieldDefinitions: List<GFieldDefinition>,
	override val interfaces: List<GNamedTypeRef> = emptyList(),
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	val kotlinType: KClass<*>? = null,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GCompositeType(
		description = description,
		directives = directives,
		extensions = extensions,
		kind = Kind.OBJECT,
		name = name,
		origin = origin
	),
	GNode.WithFieldDefinitions,
	GNode.WithInterfaces {

	constructor(
		name: String,
		fields: List<GFieldDefinition>,
		interfaces: List<GNamedTypeRef> = emptyList(),
		description: String? = null,
		kotlinType: KClass<*>? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		fieldDefinitions = fields,
		interfaces = interfaces,
		description = description?.let { GStringValue(it) },
		directives = directives,
		kotlinType = kotlinType,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GObjectType &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				fieldDefinitions.equalsNode(other.fieldDefinitions, includingOrigin = includingOrigin) &&
				interfaces.equalsNode(other.interfaces, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun isSupertypeOf(other: GType): Boolean =
		other === this ||
			(other is GNonNullType && isSupertypeOf(other.nullableType))


	companion object
}


class GObjectTypeExtension(
	name: GName,
	override val fieldDefinitions: List<GFieldDefinition> = emptyList(),
	override val interfaces: List<GNamedTypeRef> = emptyList(),
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GTypeExtension(
		directives = directives,
		extensions = extensions,
		name = name,
		origin = origin
	),
	GNode.WithFieldDefinitions,
	GNode.WithInterfaces {

	constructor(
		name: String,
		fields: List<GFieldDefinition> = emptyList(),
		interfaces: List<GNamedTypeRef> = emptyList(),
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		fieldDefinitions = fields,
		interfaces = interfaces,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GObjectTypeExtension &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				fieldDefinitions.equalsNode(other.fieldDefinitions, includingOrigin = includingOrigin) &&
				interfaces.equalsNode(other.interfaces, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GObjectValue(
	val fields: List<GObjectValueField>,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	private val fieldsByName = fields.associateBy { it.name }

	override val kind get() = Kind.OBJECT


	override fun equals(other: Any?) =
		this === other || (other is GObjectValue && fieldsByName == other.fieldsByName)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GObjectValue &&
				fields.equalsNode(other.fields, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	fun field(name: String) =
		fieldsByName[name]


	override fun hashCode() =
		fieldsByName.hashCode()


	override fun unwrap() =
		fields.associate { it.name to it.value.unwrap() }


	companion object
}


class GObjectValueField(
	name: GName,
	val value: GValue,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithName {

	override val nameNode = name


	constructor(
		name: String,
		value: GValue
	) : this(
		name = GName(name),
		value = value
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GObjectValueField &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				value.equalsNode(other.value, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GOperationDefinition(
	val type: GOperationType,
	name: GName? = null,
	val selectionSet: GSelectionSet,
	override val variableDefinitions: List<GVariableDefinition> = emptyList(),
	override val directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GExecutableDefinition(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithOptionalName,
	GNode.WithVariableDefinitions {

	override val nameNode = name


	constructor(
		type: GOperationType,
		name: String? = null,
		selectionSet: GSelectionSet,
		variableDefinitions: List<GVariableDefinition> = emptyList(),
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		type = type,
		name = name?.let { GName(it) },
		selectionSet = selectionSet,
		variableDefinitions = variableDefinitions,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GOperationDefinition &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				selectionSet.equalsNode(other.selectionSet, includingOrigin = includingOrigin) &&
				type == other.type &&
				variableDefinitions.equalsNode(other.variableDefinitions, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GOperationTypeDefinition(
	val operationType: GOperationType,
	val type: GNamedTypeRef,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GNode(
	extensions = extensions,
	origin = origin
) {

	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GOperationTypeDefinition &&
				operationType == other.operationType &&
				type.equalsNode(other.type, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Scalars
// https://graphql.github.io/graphql-spec/June2018/#sec-Scalar
sealed class GScalarType(
	name: GName,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	parseValue: (GCoercionContext<*>.(value: Any) -> Any?)?, // FIXME create types?
	parseValueNode: (GCoercionContext<*>.(value: GValue) -> Any?)?,
	serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)?,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GLeafType(
	description = description,
	directives = directives,
	extensions = extensions,
	kind = Kind.SCALAR,
	name = name,
	origin = origin,
	parseValue = parseValue,
	parseValueNode = parseValueNode,
	serializeValue = serializeValue
) {

	constructor(
		name: String,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		parseValue: (GCoercionContext<*>.(value: Any) -> Any?)?,
		parseValueNode: (GCoercionContext<*>.(value: GValue) -> Any?)?,
		serializeValue: (GCoercionContext<*>.(value: Any) -> Any?)?,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		description = description?.let { GStringValue(it) },
		directives = directives,
		parseValue = parseValue,
		parseValueNode = parseValueNode,
		serializeValue = serializeValue,
		extensions = extensions
	)


	final override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GScalarType &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	final override fun isSupertypeOf(other: GType): Boolean =
		this == other ||
			(other is GNonNullType && isSupertypeOf(other.nullableType))


	companion object
}


class GScalarTypeExtension(
	name: GName,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GTypeExtension(
	directives = directives,
	extensions = extensions,
	name = name,
	origin = origin
) {

	constructor(
		name: String,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GScalarTypeExtension &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GSchemaDefinition(
	override val operationTypeDefinitions: List<GOperationTypeDefinition>,
	override val directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GTypeSystemDefinition(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithOperationTypeDefinitions {

	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GSchemaDefinition &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				operationTypeDefinitions.equalsNode(other.operationTypeDefinitions, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GSchemaExtension(
	override val operationTypeDefinitions: List<GOperationTypeDefinition> = emptyList(),
	override val directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GTypeSystemExtension(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithOperationTypeDefinitions {

	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GSchemaExtension &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				operationTypeDefinitions.equalsNode(other.operationTypeDefinitions, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


sealed class GSelection(
	override val directives: List<GDirective>,
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives {

	companion object
}


class GSelectionSet(
	val selections: List<GSelection>,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GNode(
	extensions = extensions,
	origin = origin
) {

	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GSelectionSet &&
				selections.equalsNode(other.selections, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


// https://graphql.github.io/graphql-spec/draft/#sec-String.Input-Coercion
object GStringType : GScalarType(
	name = "String",
	parseValue = { it as? String },
	parseValueNode = { (it as? GStringValue)?.value },
	serializeValue = { value ->
		when (value) {
			is Boolean -> if (value) "true" else "false"
			is String -> value
			else -> value.coerceToIntOrNull()?.toString()
		}
	}
)


class GStringValue(
	val value: String,
	val isBlock: Boolean = false,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	override val kind get() = Kind.STRING


	override fun equals(other: Any?) =
		this === other || (other is GStringValue && value == other.value)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GStringValue &&
				value == other.value &&
				isBlock == other.isBlock &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		value.hashCode()


	override fun unwrap() =
		value


	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Wrapping-Types
// https://graphql.github.io/graphql-spec/June2018/#sec-Types
// https://graphql.github.io/graphql-spec/June2018/#sec-The-__Type-Type
sealed class GType(
	extensions: Map<ExtensionKey<*>, *>,
	val kind: Kind,
	origin: GOrigin?
) : GTypeSystemDefinition(
	extensions = extensions,
	origin = origin
) {

	abstract val name: String
	abstract val underlyingNamedType: GNamedType

	open val nonNullable get() = GNonNullType(this)
	open val nullableType get() = this


	abstract fun toRef(): GTypeRef


	// https://graphql.github.io/graphql-spec/June2018/#IsInputType()
	fun isInputType(): Boolean =
		when (this) {
			is GWrappingType -> wrappedType.isInputType()
			is GScalarType, is GEnumType, is GInputObjectType -> true
			else -> false
		}


	// https://graphql.github.io/graphql-spec/June2018/#IsOutputType()
	fun isOutputType(): Boolean =
		when (this) {
			is GWrappingType -> wrappedType.isOutputType()
			is GScalarType, is GObjectType, is GInterfaceType, is GUnionType, is GEnumType -> true
			else -> false
		}


	fun isSubtypeOf(other: GType) =
		other.isSupertypeOf(this)


	abstract fun isSupertypeOf(other: GType): Boolean


	companion object {

		val defaultTypes = setOf<GNamedType>(
			GBooleanType,
			GFloatType,
			GIdType,
			GIntType,
			GStringType
		)
	}


	// https://graphql.github.io/graphql-spec/June2018/#sec-Schema-Introspection
	// https://graphql.github.io/graphql-spec/June2018/#sec-Type-Kinds
	enum class Kind {

		ENUM,
		INPUT_OBJECT,
		INTERFACE,
		LIST,
		NON_NULL,
		OBJECT,
		SCALAR,
		UNION;


		override fun toString() =
			when (this) {
				ENUM -> "Enum"
				INPUT_OBJECT -> "Input Object"
				INTERFACE -> "Interface"
				LIST -> "List"
				NON_NULL -> "Non-Null"
				OBJECT -> "Object"
				SCALAR -> "Scalar"
				UNION -> "Union"
			}


		companion object
	}
}


sealed class GTypeExtension(
	override val directives: List<GDirective>,
	extensions: Map<ExtensionKey<*>, *>,
	name: GName,
	origin: GOrigin?
) :
	GTypeSystemExtension(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithName {

	override val nameNode = name


	companion object
}


sealed class GTypeRef(
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) : GNode(
	extensions = extensions,
	origin = origin
) {

	abstract val underlyingName: String

	open val nonNullableRef get() = GNonNullTypeRef(this)
	open val nullableRef get() = this


	companion object {

		fun parse(source: GSource.Parsable) =
			Parser.parseTypeReference(source)


		fun parse(content: String, name: String = "<type reference>") =
			parse(GSource.of(content = content, name = name))
	}
}


fun GTypeRef(
	name: String,
	extensions: Map<GNode.ExtensionKey<*>, *> = emptyMap<GNode.ExtensionKey<*>, Any>()
) =
	GNamedTypeRef(name, extensions = extensions)


val GBooleanTypeRef = GTypeRef("Boolean")
val GFloatTypeRef = GTypeRef("Float")
val GIdTypeRef = GTypeRef("ID")
val GIntTypeRef = GTypeRef("Int")
val GStringTypeRef = GTypeRef("String")


sealed class GTypeSystemDefinition(
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) : GDefinition(
	extensions = extensions,
	origin = origin
) {

	companion object
}


sealed class GTypeSystemExtension(
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) : GDefinition(
	extensions = extensions,
	origin = origin
) {

	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Unions
// https://graphql.github.io/graphql-spec/June2018/#sec-Union
class GUnionType(
	name: GName,
	val possibleTypes: List<GNamedTypeRef>,
	description: GStringValue? = null,
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GAbstractType(
	description = description,
	directives = directives,
	extensions = extensions,
	kind = Kind.UNION,
	name = name,
	origin = origin
) {

	constructor(
		name: String,
		possibleTypes: List<GNamedTypeRef>,
		description: String? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		possibleTypes = possibleTypes,
		description = description?.let { GStringValue(it) },
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GUnionType &&
				descriptionNode.equalsNode(other.descriptionNode, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				possibleTypes.equalsNode(other.possibleTypes, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun isSupertypeOf(other: GType): Boolean =
		other === this ||
			other is GObjectType && possibleTypes.any { it.name == other.name } ||
			(other is GNonNullType && isSupertypeOf(other.nullableType))


	companion object
}


class GUnionTypeExtension(
	name: GName,
	val possibleTypes: List<GNamedTypeRef> = emptyList(),
	directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GTypeExtension(
	directives = directives,
	extensions = extensions,
	name = name,
	origin = origin
) {

	constructor(
		name: String,
		possibleTypes: List<GNamedTypeRef> = emptyList(),
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		possibleTypes = possibleTypes,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GUnionTypeExtension &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				possibleTypes.equalsNode(other.possibleTypes, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


sealed class GValue(
	extensions: Map<ExtensionKey<*>, *>,
	origin: GOrigin?
) : GNode(
	extensions = extensions,
	origin = origin
) {

	abstract val kind: Kind

	abstract fun unwrap(): Any?


	companion object {

		// FIXME temporary -- improve
		fun of(value: Any?): GValue? =
			when (value) {
				null -> GNullValue.withoutOrigin
				is Boolean -> GBooleanValue(value)
				is Double -> GFloatValue(value)
				is Int -> GIntValue(value)
				is Map<*, *> -> GObjectValue(value.map { (fieldName, fieldValue) ->
					GObjectValueField(
						name = fieldName as? String ?: return null,
						value = of(fieldValue) ?: return null
					)
				})
				is Collection<*> -> GListValue(value.map { of(it) ?: return null })
				is String -> GStringValue(value)
				else -> null
			}


		fun parse(source: GSource.Parsable) =
			Parser.parseValue(source)


		fun parse(content: String, name: String = "<value>") =
			parse(GSource.of(content = content, name = name))
	}


	enum class Kind {

		BOOLEAN,
		ENUM,
		FLOAT,
		INT,
		NULL,
		OBJECT,
		STRING,
		VARIABLE;


		override fun toString() = when (this) {
			BOOLEAN -> "Boolean"
			ENUM -> "Enum"
			FLOAT -> "Float"
			INT -> "Int"
			NULL -> "Null"
			OBJECT -> "Input Object"
			STRING -> "String"
			VARIABLE -> "Variable"
		}


		companion object
	}
}


class GVariableDefinition(
	name: GName,
	val type: GTypeRef,
	val defaultValue: GValue? = null,
	override val directives: List<GDirective> = emptyList(),
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) :
	GNode(
		extensions = extensions,
		origin = origin
	),
	GNode.WithDirectives,
	GNode.WithName {

	override val nameNode = name


	constructor(
		name: String,
		type: GTypeRef,
		defaultValue: GValue? = null,
		directives: List<GDirective> = emptyList(),
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		type = type,
		defaultValue = defaultValue,
		directives = directives,
		extensions = extensions
	)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GVariableDefinition &&
				defaultValue.equalsNode(other.defaultValue, includingOrigin = includingOrigin) &&
				directives.equalsNode(other.directives, includingOrigin = includingOrigin) &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				type.equalsNode(other.type, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	companion object
}


class GVariableRef(
	name: GName,
	origin: GOrigin? = null,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GValue(
	extensions = extensions,
	origin = origin
) {

	val name get() = nameNode.value
	val nameNode = name

	override val kind get() = Kind.VARIABLE


	constructor(
		name: String,
		extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
	) : this(
		name = GName(name),
		extensions = extensions
	)


	override fun equals(other: Any?) =
		this === other || (other is GVariableRef && name == other.name)


	override fun equalsNode(other: GNode, includingOrigin: Boolean) =
		this === other || (
			other is GVariableRef &&
				nameNode.equalsNode(other.nameNode, includingOrigin = includingOrigin) &&
				(!includingOrigin || origin == other.origin)
			)


	override fun hashCode() =
		name.hashCode()


	override fun unwrap() =
		error("Cannot unwrap a GraphQL variable: $name")


	companion object
}


// https://graphql.github.io/graphql-spec/June2018/#sec-Wrapping-Types
// https://graphql.github.io/graphql-spec/June2018/#sec-Types
sealed class GWrappingType(
	kind: Kind,
	val wrappedType: GType,
	extensions: Map<ExtensionKey<*>, *> = emptyMap<ExtensionKey<*>, Any>()
) : GType(
	extensions = extensions,
	kind = kind,
	origin = null
) {

	final override val underlyingNamedType get() = wrappedType.underlyingNamedType


	override fun toString() =
		"${print(wrappedType)} <wrapped as $name>"


	companion object
}
