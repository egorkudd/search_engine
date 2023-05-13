package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.IndexModel;

public interface IndexRepository extends JpaRepository<IndexModel, Integer> {
}
