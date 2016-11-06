package it.polito.dp2.NFFG.lab1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import it.polito.dp2.NFFG.*;


public class NFFGInfo {
	private NffgVerifier monitor;
	private DateFormat dateFormat;

	
	/**
	 * Default constructror
	 * @throws NffgVerifierException 
	 */
	public NFFGInfo() throws NffgVerifierException {
		NffgVerifierFactory factory = NffgVerifierFactory.newInstance();
		monitor = factory.newNffgVerifier();
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}
	
	public NFFGInfo(NffgVerifier monitor) {
		super();
		this.monitor = monitor;
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NFFGInfo wf;
		try {
			wf = new NFFGInfo();
			wf.printAll();
		} catch (NffgVerifierException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void printAll() {
		printNffgs();
		printPolicies();
	}


	private void printPolicies() {

		// Get the list of policies
		Set<PolicyReader> set = monitor.getPolicies();
		
		/* Print the header of the table */
		System.out.println("#");
		System.out.println("#Number of Policies: "+set.size());
		System.out.println("#");
		String header = new String("#List of policies:");
		printHeader(header);
		
		// For each policy print related data
		for (PolicyReader pr: set) {
			System.out.println("Policy name: " + pr.getName());
			System.out.println("Policy nffg name: " + pr.getNffg().getName());
			if(pr.isPositive())
				System.out.println("Policy is positive.");
			else
				System.out.println("Policy is negative.");
			printVerificationResult(pr.getResult());
			System.out.println("#");
		}
		System.out.println("#End of Policies");
		System.out.println("#");
	}


	private void printVerificationResult(VerificationResultReader result) {
		if (result == null) {
			System.out.println("No verification result for policy");
			return;
		}
		if (result.getVerificationResult())
			System.out.println("Policy result is true");
		else
			System.out.println("Policy result is false");
		System.out.println("Verification result message: " + result.getVerificationResultMsg());
		System.out.println("Verification time (in local time zone): " + dateFormat.format(result.getVerificationTime().getTime()));
	}

	private void printNffgs() {
		// Get the list of NFFGs
		Set<NffgReader> set = monitor.getNffgs();
		
		/* Print the header of the table */
		System.out.println("#");
		System.out.println("#Number of Nffgs: "+set.size());
		System.out.println("#");
		String header = new String("#List of NFFgs:");
		printHeader(header);	
		
		// For each NFFG print related data
		for (NffgReader nffg_r: set) {
			System.out.println();
			printHeader("Data for NFFG " + nffg_r.getName());
			System.out.println();
			// Print update time
			Calendar updateTime = nffg_r.getUpdateTime();
			printHeader("Update time: "+dateFormat.format(updateTime.getTime()));

			// Print nodes
			Set<NodeReader> nodeSet = nffg_r.getNodes();
			printHeader("Number of Nodes: "+nodeSet.size(),'%');
			for (NodeReader nr: nodeSet) {
				System.out.println("Node " + nr.getName() +"\tType: "+nr.getFuncType().toString()+"\tNumber of links: "+nr.getLinks().size());
				Set<LinkReader> linkSet = nr.getLinks();
				System.out.println("List of Links for node "+nr.getName());
				printHeader("Link name \tsource \tdestination");
				for (LinkReader lr: linkSet)
					System.out.println(lr.getName()+"\t"+lr.getSourceNode().getName()+"\t"+lr.getDestinationNode().getName());
				System.out.println(makeLine('%'));;
			}
			System.out.println("#");
		}	
		System.out.println("#End of Nodes");
		System.out.println("#");
	}

	private void printHeader(String header, char c) {		
		System.out.println(header);
		System.out.println(makeLine(c));	
	}

	private StringBuffer makeLine(char c) {
		StringBuffer line = new StringBuffer(132);
		
		for (int i = 0; i < 132; ++i) {
			line.append(c);
		}
		return line;
	}

	private void printHeader(String header) {
		printHeader(header,'-');
	}
}
