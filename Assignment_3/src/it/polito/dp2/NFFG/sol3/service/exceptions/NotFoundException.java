package it.polito.dp2.NFFG.sol3.service.exceptions;

/**
 * Simply adding a prefix to the message string
 * 
 * @author Martino Mensio
 *
 */
public class NotFoundException extends javax.ws.rs.NotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException(String string) {
		super("Resource not found: " + string);
	}

}
