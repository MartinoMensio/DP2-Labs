package it.polito.dp2.NFFG.sol3.service.validation;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import javax.xml.bind.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 * @param <T>
 */
@Provider
@Consumes({ MediaType.APPLICATION_XML })
public class ValidatingReader<T> implements MessageBodyReader<T> {

	final String jaxbPackage = "it.polito.dp2.NFFG.sol3.service.jaxb";
	private JAXBContext context = null;
	private Schema schema = null;

	public ValidatingReader() {
		try {
			InputStream schemaStream = ValidatingReader.class.getResourceAsStream("/xsd/nffgVerifier.xsd");
			if (schemaStream == null) {
				throw new IOException("schema file not found");
			}
			context = JAXBContext.newInstance(jaxbPackage);
			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = sf.newSchema(new StreamSource(schemaStream));

		} catch (SAXException | JAXBException | IOException se) {
			// set unmarshaller to null so that readFrom can find the error
			context = null;
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return (Nffg.class.equals(type) || Policy.class.equals(type))
				&& jaxbPackage.equals(type.getPackage().getName());
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		// the unmarshaller is created locally to this method because it is not
		// thread-safe
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
		} catch (Exception e) {
			// throw a RuntimeException so that the custom mapper won't catch
			// it, but will display the problem
			throw new RuntimeException(e);
		}

		try {
			return castToGeneric(unmarshaller.unmarshal(entityStream));
		} catch (JAXBException ex) {
			Throwable linked = ex.getLinkedException();
			String validationErrorMessage = "Validation error";
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMessage += ": " + linked.getMessage();
			throw new BadRequestException(validationErrorMessage);
		}
	}

	@SuppressWarnings("unchecked")
	private T castToGeneric(Object o) {
		try {
			return (T) o;
		} catch (ClassCastException e) {
			throw new InternalServerErrorException();
		}
	}

}
