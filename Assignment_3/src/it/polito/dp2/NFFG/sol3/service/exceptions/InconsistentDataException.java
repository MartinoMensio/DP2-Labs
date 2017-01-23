package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.InternalServerErrorException;

/**
 * This exception should never occur, it is thrown when something unexpected
 * happens but the logic of the service should make this impossible to occur
 * 
 * @author Martino Mensio
 *
 */
public class InconsistentDataException extends InternalServerErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InconsistentDataException(String string) {
		super("The data stored inside the service are inconsistent: " + string);
	}

}
