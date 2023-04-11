package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.ToMailRecipient;
import org.esupportail.smsu.dao.beans.idClass.ToMailRecipientPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ToMailRecipientRepository extends JpaRepository<ToMailRecipient, ToMailRecipientPk> {
	@Modifying
	@Query("""
			DELETE FROM #{#entityName} tmr
			WHERE tmr.id NOT IN (
				SELECT m.mail.id
				FROM Message m
				WHERE m.mail.id IS NOT NULL
			)
			""")
	int deleteOrphanToMailRecipient();
}
