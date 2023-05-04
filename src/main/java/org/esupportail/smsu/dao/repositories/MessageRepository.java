package org.esupportail.smsu.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.dao.beans.Template;
import org.esupportail.smsu.domain.beans.message.MessageStatus;

public interface MessageRepository extends JpaRepository<Message, Integer> {
	List<Message> findByService(Service service);
	boolean existsByService(Service service);

	List<Message> findByTemplate(Template template);
	boolean existsByTemplate(Template template);

	List<Message> findByState(MessageStatus state);

	List<Message> findByStateOrderByIdAsc(MessageStatus state);
	
	@Modifying
	@Query("""
			UPDATE #{#entityName} m
			SET m.content = ''
			WHERE m.content <> ''
			AND m.date < :date
			""")
	int deleteContentOlderThan(@Param("date") Date date);

	int deleteByDateLessThan(Date messageDate);
}
