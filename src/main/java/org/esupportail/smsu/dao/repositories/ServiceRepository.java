package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
	@Nullable
	Service findByKey(String key);
	
	@Nullable
	Service findByName(String name);
}
