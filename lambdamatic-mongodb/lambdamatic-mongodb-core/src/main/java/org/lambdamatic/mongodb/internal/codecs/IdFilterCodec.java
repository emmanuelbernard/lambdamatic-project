/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Field;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.internal.IdFilter;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class IdFilterCodec implements Codec<IdFilter<?>> {

	/** The binding service. */
	private final BindingService bindingService;

	/**
	 * Constructor
	 * 
	 * @param bindingService
	 *            the {@link BindingService}.
	 */
	public IdFilterCodec(final BindingService bindingService) {
		this.bindingService = bindingService;
	}

	/**
	 * @see org.bson.codecs.Encoder#encode(org.bson.BsonWriter, java.lang.Object, org.bson.codecs.EncoderContext)
	 */
	@Override
	public void encode(final BsonWriter writer, final IdFilter<?> idFilter, final EncoderContext encoderContext) {
		final Object id = findId(idFilter.getDomainObject());
		writer.writeStartDocument();
		if (id instanceof ObjectId) {
			writer.writeObjectId(DocumentEncoder.MONGOBD_DOCUMENT_ID, (ObjectId) id);
		} else {
			writer.writeString(DocumentEncoder.MONGOBD_DOCUMENT_ID, id.toString());
		}
		writer.writeEndDocument();

	}

	/**
	 * Finds the value of the attribute annotated with {@link DocumentId}.
	 * 
	 * @param domainObject
	 *            the domain object to analyze
	 * @return the value of the id
	 * @throws ConversionException
	 *             if no value could be find.
	 */
	private Object findId(final Object domainObject) {
		final Map<String, Field> bindings = bindingService.getBindings(domainObject.getClass());
		final Field idField = bindings.get(DocumentEncoder.MONGOBD_DOCUMENT_ID);
		if (idField != null) {
			idField.setAccessible(true);
			try {
				return idField.get(domainObject);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ConversionException("Failed to retrieve id for instance of domain class '"
						+ domainObject.getClass().getName() + "'", e);
			}
		}
		return new ConversionException("Failed to retrieve id for instance of domain class '"
				+ domainObject.getClass().getName() + "': no field annotated with @Document ?");
	}

	/**
	 * @see org.bson.codecs.Encoder#getEncoderClass()
	 */
	@Override
	public Class<IdFilter<?>> getEncoderClass() {
		return null;
	}

	/**
	 * @see org.bson.codecs.Decoder#decode(org.bson.BsonReader, org.bson.codecs.DecoderContext)
	 */
	@Override
	public IdFilter<?> decode(BsonReader reader, DecoderContext decoderContext) {
		throw new UnsupportedOperationException("Unsupported operation: decoding an IdFilter");
	}

}
