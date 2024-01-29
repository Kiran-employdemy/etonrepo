package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.Objects;

/**
 * The Class ModificationOperationsResult.
 *
 * @author Jaroslav Rassadin
 */
public class ModificationOperationsResult {

	private BackupResult backup;
	private ReplicationResult replication;

	/**
	 * Gets the backup.
	 *
	 * @return the backup
	 */
	public BackupResult getBackup() {
		return this.backup;
	}

	/**
	 * Sets the backup.
	 *
	 * @param backup
	 *            the backup to set
	 */
	public void setBackup(final BackupResult backup) {
		this.backup = backup;
	}

	/**
	 * Gets replication result
	 * 
	 * @return replicationResult
	 */
	public ReplicationResult getReplication() {
		return replication;
	}

	/**
	 * Sets replication result
	 * 
	 * @param replication the replication result
	 */
	public void setReplication(ReplicationResult replication) {
		this.replication = replication;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(backup, replication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModificationOperationsResult other = (ModificationOperationsResult) obj;
		return Objects.equals(backup, other.backup) && Objects.equals(replication, other.replication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ModificationOperationsResult [backup=" + backup + ", replication=" + replication + "]";
	}
}
