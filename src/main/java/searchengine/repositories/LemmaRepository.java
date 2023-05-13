package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.models.LemmaModel;

import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaModel, Integer> {
    Optional<LemmaModel> findByLemma(String lemma);

    @Transactional
    @Modifying
    @Query(value = "update LemmaModel set frequency = frequency + 1 where id = :id")
    int updateFrequencyById(@Param("id") Integer id);
}
