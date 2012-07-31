package de.typology.rdb.connectors;

public interface IRDBSessionConnector {

	/**
	 * Check if given developer key is a valid one
	 * @return if greater zero key is valid (dlfnr), if -1 key is invalid
	 */
	public abstract int checkDeveloperKey(String key);

	/**
	 * Gets ulfnr with specified keys. Creates new user if not existing.
	 * 
	 * @param develkey Developerkey
	 * @param uid developer user id
	 * @return ulfnr of inserted row or existing user specified by params
	 */
	public abstract int getOrCreateUlfnr(int dlfnr, String uid, String userpass);

}