package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.HtmlInfo;

public interface HtmlRepository extends JpaRepository<HtmlInfo, Integer> {
}
