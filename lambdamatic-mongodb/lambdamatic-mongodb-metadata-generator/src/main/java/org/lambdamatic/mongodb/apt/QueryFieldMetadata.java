package org.lambdamatic.mongodb.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.ext.QStringArray;

/**
 * Information about a given field that should be generated in a {@link QueryMetadata} class.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class QueryFieldMetadata extends BaseFieldMetadata {

	/**
	 * Creates a {@link QueryFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException
	 */
	public QueryFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation)
			throws MetadataGenerationException {
		super(getMetadataFieldName(variableElement), getMetadataFieldType(variableElement.asType()), MONGOBD_DOCUMENT_ID);
	}

	/**
	 * Creates a {@link QueryFieldMetadata} from a field optionally annotated with {@link DocumentField}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentFieldAnnotation
	 *            the optional {@link DocumentField} annotation
	 * @throws MetadataGenerationException
	 */
	public QueryFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation)
			throws MetadataGenerationException {
		super(getMetadataFieldName(variableElement), getMetadataFieldType(variableElement.asType()), getDocumentFieldName(documentFieldAnnotation));
	}

	/**
	 * Returns the fully qualified name of the type of the given {@link VariableElement}, or {@code null} if it was not
	 * a known or supported type.
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return
	 * @throws MetadataGenerationException
	 */
	protected static FieldType getMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException {
		if (variableType instanceof PrimitiveType) {
			return new FieldType(QueryField.class, getSimilarDeclaredType((PrimitiveType) variableType).getName());
		} else if (variableType instanceof DeclaredType) {
			final DeclaredType declaredType = (DeclaredType) variableType;
			final Element declaredElement = declaredType.asElement();
				// embedded documents
				if (declaredElement.getAnnotation(EmbeddedDocument.class) != null) {
					return new FieldType(getQueryMetadataType(declaredElement.toString()));
				} 
				// collections (list/set)
				else if (isCollection(declaredElement)) {
					final TypeMirror typeArgument = declaredType.getTypeArguments().get(0);
					return new FieldType(getQueryArrayMetadataType(typeArgument.toString()));
				} else {
					switch (variableType.toString()) {
					case "org.lambdamatic.mongodb.types.geospatial.Location":
						return new FieldType(LocationField.class);
					default:
						return new FieldType(QueryField.class, variableType.toString());
					}
				}
		} else if (variableType.getKind() == TypeKind.ARRAY) {
			final TypeMirror componentType = ((ArrayType) variableType).getComponentType();
			final Element variableTypeElement = ((DeclaredType) componentType).asElement();
			if (variableTypeElement.getKind() == ElementKind.ENUM) {
				return new FieldType(QueryArray.class, componentType.toString());
			} else if (componentType.getAnnotation(EmbeddedDocument.class) != null) {
				throw new MetadataGenerationException("Unsupported EmbeddedDocument type: " + variableType);
				// return null; //generateQueryMetadataType(variableTypeElement);
			} else {
				return new FieldType(QueryArray.class, componentType.toString());
			}
		}
		throw new MetadataGenerationException("Unexpected variable type: " + variableType);
	}

	/**
	 * @return the fully qualified name of the {@link QueryMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	public static String getQueryMetadataType(final String elementTypeName) {
		final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
		final String shortClassName = Constants.QUERY_METADATA_CLASSNAME_PREFIX
				+ ClassUtils.getShortClassName(elementTypeName.toString());
		return packageName + '.' + shortClassName;
	}

	/**
	 * @return the fully qualified name of the {@link QueryMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	public static String getQueryArrayMetadataType(final String elementTypeName) {
		switch (elementTypeName) {
		//FIXME: implements other base types
		case "java.lang.String":
			return QStringArray.class.getName();
		default:
			final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
			final String shortClassName = Constants.QUERY_METADATA_CLASSNAME_PREFIX
					+ ClassUtils.getShortClassName(elementTypeName.toString()) + Constants.QUERY_ARRAY_METADATA_CLASSNAME_SUFFIX;
			return packageName + '.' + shortClassName;
		}
	}

	private static Class<?> getSimilarDeclaredType(final PrimitiveType variableType) {
		final TypeKind variableTypeKind = variableType.getKind();
		switch (variableTypeKind) {
		case BOOLEAN:
			return Boolean.class;
		case BYTE:
			return Byte.class;
		case SHORT:
			return Short.class;
		case INT:
			return Integer.class;
		case LONG:
			return Long.class;
		case FLOAT:
			return Float.class;
		case DOUBLE:
			return Double.class;
		case CHAR:
			return Character.class;
		default:
			throw new ConversionException(
					"Failed to provide a declared type equivalent to '" + variableTypeKind.name().toLowerCase() + "'");
		}
	}

}