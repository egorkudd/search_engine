package searchengine.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "html_info")
public class HtmlInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String link;

    @Column(name = "html_text")
    private String htmlText;

    public HtmlInfo(String link, String htmlText) {
        this.link = link;
        this.htmlText = htmlText;
    }
}
