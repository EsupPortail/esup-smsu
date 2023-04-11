package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface RecipientRepository extends JpaRepository<Recipient, Integer> {
	@Nullable
	Recipient findByPhoneAndLogin(String phone, String login);
	
	@Modifying
	@Query("""
			DELETE FROM #{#entityName} r
			WHERE r.id NOT IN (
				SELECT tr.rcp.id 
				FROM ToRecipient tr
				WHERE tr.rcp.id IS NOT NULL
			)
			""")
	int deleteOrphanRecipient();
}
