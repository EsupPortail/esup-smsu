package org.esupportail.smsu.dao.repositories;

import java.util.List;

import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MailRepository extends JpaRepository<Mail, Integer> {
	List<Mail> findByTemplate(Template template);

	boolean existsByTemplate(Template template);
	
	@Modifying
	@Query("""
			DELETE FROM #{#entityName} mail
			WHERE mail.id NOT IN (
				SELECT m.mail.id
				FROM Message m
				WHERE m.mail.id IS NOT NULL
			)
			""")
	int deleteOrphanMail();
}
