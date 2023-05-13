package searchengine.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import searchengine.models.enums.SiteStatus;

import javax.persistence.*;
import java.sql.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sites")
public class SiteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private SiteStatus status;
    @Column(name = "status_time")
    private Date statusTime;
    @Column(name = "last_error")
    private String lastError;
    private String url;
    private String name;

    public SiteModel(SiteStatus status, Date statusTime, String url, String name) {
        this.status = status;
        this.statusTime = statusTime;
        this.url = url;
        this.name = name;
    }
}
