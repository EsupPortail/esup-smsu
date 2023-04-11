package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.BasicGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface BasicGroupRepository extends JpaRepository<BasicGroup, Integer> {
	@Nullable
	BasicGroup findByLabel(String label);

	@Modifying
	@Query("""
			DELETE FROM #{#entityName} bg
			WHERE bg.id NOT IN (
				SELECT m.groupSender.id
				FROM Message m
				WHERE m.groupSender.id IS NOT NULL
			) AND bg.id NOT IN (
				SELECT m.groupRecipient.id
				FROM Message m
				WHERE m.groupRecipient.id IS NOT NULL
			)
			""")
	int deleteOrphanBasicGroup();
}
