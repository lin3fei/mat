package com.cmcciot.mat.pushapi.cmobile.object;

import com.simpleteam.SimpleSequence;
import com.simpleteam.connection.protocol.cmpp.CMPP3Connection;
import com.simpleteam.transactor.authenticate.CMPPAuthenticate;


public final class SingleCmppObject {
	/**
	 * Whether authenticated.
	 */
	private boolean authenticated;

	/**
	 * Simple sequence.
	 */
	private SimpleSequence sequence;

	/**
	 * Connection.
	 */
	private CMPP3Connection connection;
	/**
	 * Authenticate.
	 */
	private CMPPAuthenticate authenticate;

	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * @param authenticated
	 *            the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * @return the sequence
	 */
	public SimpleSequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(SimpleSequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the connection
	 */
	public CMPP3Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(CMPP3Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return the authenticate
	 */
	public CMPPAuthenticate getAuthenticate() {
		return authenticate;
	}

	/**
	 * @param authenticate
	 *            the authenticate to set
	 */
	public void setAuthenticate(CMPPAuthenticate authenticate) {
		this.authenticate = authenticate;
	}
}
