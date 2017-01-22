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

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

@Provider
@Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
public class NffgValidatingReader implements MessageBodyReader<Nffg> {

	final String jaxbPackage = "it.polito.dp2.NFFG.sol3.service.jaxb";
	Unmarshaller unmarshaller;

	public NffgValidatingReader() {
		try {
			InputStream schemaStream = NffgValidatingReader.class.getResourceAsStream("/xsd/nffgVerifier.xsd");
			if (schemaStream == null) {
				throw new IOException("schema file not found");
			}
			JAXBContext jc = JAXBContext.newInstance(jaxbPackage);
			unmarshaller = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new StreamSource(schemaStream));
			unmarshaller.setSchema(schema);

		} catch (SAXException | JAXBException | IOException se) {
			// TODO
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Nffg.class.equals(type) && jaxbPackage.equals(type.getPackage().getName())
				&& (mediaType.equals(MediaType.APPLICATION_XML) || mediaType.equals(MediaType.APPLICATION_JSON));
	}

	@Override
	public Nffg readFrom(Class<Nffg> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		if (unmarshaller == null) {
			throw new InternalServerErrorException("unmarshaler null");
		}
		try {
			return (Nffg) unmarshaller.unmarshal(entityStream);
		} catch (JAXBException ex) {
			Throwable linked = ex.getLinkedException();
			String validationErrorMessage = "Validation error";
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMessage += ": " + linked.getMessage();
			throw new ValidationFailedException(validationErrorMessage);
		}
	}

}
