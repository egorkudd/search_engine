package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.PageModel;

public interface PageRepository extends JpaRepository<PageModel, Integer> {
}
