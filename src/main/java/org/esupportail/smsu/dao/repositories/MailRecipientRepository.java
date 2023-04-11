package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.MailRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface MailRecipientRepository extends JpaRepository<MailRecipient, Integer> {
	@Nullable
	MailRecipient findByAddress(String address);

	@Modifying
	@Query("""
			DELETE FROM #{#entityName} mr
			WHERE mr.id NOT IN (
				SELECT tmr.mailRecipient.id
				FROM ToMailRecipient tmr
				WHERE tmr.mailRecipient.id IS NOT NULL
			)
			""")
	int deleteOrphanMailRecipient();
}
