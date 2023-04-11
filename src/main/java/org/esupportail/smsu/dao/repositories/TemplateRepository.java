package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface TemplateRepository extends JpaRepository<Template, Integer> {
	@Nullable
	Template findByLabel(String label);
}
