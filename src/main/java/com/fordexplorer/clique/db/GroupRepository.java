package com.fordexplorer.clique.db;

import com.fordexplorer.clique.data.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {
}
