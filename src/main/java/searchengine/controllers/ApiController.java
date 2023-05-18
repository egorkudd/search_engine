package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.BoolResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.enteties.WebSiteParser;
import searchengine.models.SiteModel;
import searchengine.models.enums.SiteStatus;
import searchengine.services.interfaces.IndexingService;
import searchengine.services.interfaces.StatisticsService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static searchengine.models.enums.SiteStatus.INDEXED;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private IndexingService indexingService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<BoolResponse> startIndexing() {
        long start = System.currentTimeMillis();

        indexingService.startIndexing();

        System.out.println((double) (System.currentTimeMillis() - start) / 1000);

        return ResponseEntity.ok(new BoolResponse());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<BoolResponse> stopIndexing() {
        indexingService.stopIndexing();
        return ResponseEntity.ok(new BoolResponse());
    }

    @GetMapping("/indexPage")
    public ResponseEntity<BoolResponse> indexPage() {
        return ResponseEntity.ok(new BoolResponse());
    }
}
