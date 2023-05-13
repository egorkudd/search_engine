package searchengine.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "indexes")
public class IndexModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "page_id")
    private int pageId;
    @Column(name = "lemma_id")
    private int lemmaId;
    @Column(name = "index_rank")
    private float rank;
}
