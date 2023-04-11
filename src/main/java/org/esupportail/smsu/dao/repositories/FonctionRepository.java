package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Fonction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface FonctionRepository extends JpaRepository<Fonction, Integer> {
	@Nullable
	Fonction findByName(String name);
}
