package org.esupportail.smsu.dao.repositories;

import org.esupportail.smsu.dao.beans.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	@Nullable
	Account findByLabel(String label);
}
