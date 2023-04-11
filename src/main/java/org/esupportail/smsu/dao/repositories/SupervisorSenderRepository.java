package org.esupportail.smsu.dao.repositories;

import java.util.Date;

import org.esupportail.smsu.dao.beans.SupervisorSender;
import org.esupportail.smsu.dao.beans.idClass.SupervisorSenderPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisorSenderRepository extends JpaRepository<SupervisorSender, SupervisorSenderPk> {
	int deleteByMsg_DateLessThan(Date messageDate);
}
