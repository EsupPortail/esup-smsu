package org.esupportail.smsu.dao.repositories;

import java.util.Date;

import org.esupportail.smsu.dao.beans.ToRecipient;
import org.esupportail.smsu.dao.beans.idClass.ToRecipientPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToRecipientRepository extends JpaRepository<ToRecipient, ToRecipientPk> {
	int deleteByMsg_DateLessThan(Date messageDate);
}
