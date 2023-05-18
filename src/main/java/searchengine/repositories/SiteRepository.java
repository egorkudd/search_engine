package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import searchengine.models.SiteModel;

public interface SiteRepository extends JpaRepository<SiteModel, Integer> {
    @Transactional
    @Modifying
    @Query(value = "update SiteModel set status = 'INDEXED' where id = :id")
    int updateStatusById(@Param("id") Integer id);
}
