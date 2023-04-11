package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Supervisor;
import org.esupportail.smsu.dao.beans.idClass.SupervisorPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisorRepository extends JpaRepository<Supervisor, SupervisorPk> {
	boolean existsByPerson(Person person);
}
