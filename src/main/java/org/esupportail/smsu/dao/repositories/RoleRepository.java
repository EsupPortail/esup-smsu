package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	@Nullable
	Role findByName(String name);
}
