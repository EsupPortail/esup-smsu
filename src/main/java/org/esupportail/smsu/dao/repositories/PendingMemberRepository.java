package org.esupportail.smsu.dao.repositories;

import java.util.Date;

import org.esupportail.smsu.dao.beans.PendingMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingMemberRepository extends JpaRepository<PendingMember, String> {
	int deleteByDateSubscriptionLessThan(Date date);
}
