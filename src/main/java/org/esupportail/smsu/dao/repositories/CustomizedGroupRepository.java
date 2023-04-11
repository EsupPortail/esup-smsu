package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface CustomizedGroupRepository extends JpaRepository<CustomizedGroup, Integer> {
	@Nullable
	CustomizedGroup findByLabel(String label);
	
	@Nullable
	CustomizedGroup findByLabelAndIdNot(String label, Integer id);
	
	boolean existsByRole(Role role);
}
