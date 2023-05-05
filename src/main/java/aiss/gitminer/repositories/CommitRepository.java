package aiss.gitminer.repositories;

import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<Commit, String> {
}
