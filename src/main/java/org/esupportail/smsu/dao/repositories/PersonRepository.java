package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface PersonRepository extends JpaRepository<Person, Integer> {
	@Nullable
	Person findByLogin(String login);
	
	@Modifying
	@Query("""
			DELETE FROM #{#entityName} p
			WHERE p.id NOT IN (
				SELECT supervisorSender.supervisor.id
				FROM SupervisorSender supervisorSender
				WHERE supervisorSender.supervisor.id IS NOT NULL
			) AND p.id NOT IN (
				SELECT m.sender.id
				FROM Message m
				WHERE m.sender.id IS NOT NULL
			) AND p.id NOT IN (
				SELECT supervisor.person.id
				FROM Supervisor supervisor
				WHERE supervisor.person.id IS NOT NULL
			)
			""")
	int deleteOrphanPerson();
}
