package searchengine.controllers;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.config.SitesList;
import searchengine.dto.statistics.BoolResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.models.HtmlInfo;
import searchengine.models.LemmaModel;
import searchengine.repositories.HtmlRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.interfaces.StatisticsService;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private SitesList sitesList;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private HtmlRepository htmlRepository;
    @Autowired
    private LemmaRepository lemmaRepository;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<BoolResponse> startIndexing() {
//        long start = System.currentTimeMillis();
//
//        lemmaRepository.deleteAll();
//        List<HtmlInfo> htmlInfoList = htmlRepository.findAll();
//        try {
//            for (HtmlInfo htmlInfo : htmlInfoList) {
//                String text = htmlInfo.getHtmlText();
//                System.out.println("====================== " + htmlInfo.getLink() + " ===============") ;
//                saveLemmas(text);
//
//                break;
//            }
//
//            List<LemmaModel> lemmaModelList = lemmaRepository.findAll();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        siteRepository.deleteAll();
//
//        List<Site> sites = sitesList.getSites();
//        sites.forEach(site -> {
//            SiteModel siteModel = new SiteModel(
//                    SiteStatus.INDEXING, Date.valueOf(LocalDate.now()), site.getUrl(), site.getName()
//            );
//            SiteModel savedSiteModel = siteRepository.saveAndFlush(siteModel);
//
//            new ForkJoinPool(Runtime.getRuntime().availableProcessors())
//                    .submit(new WebSiteParser(savedSiteModel.getId() ,site.getUrl(), ""))
//                    .join();
//
//             Save to siteRepository (INDEXED, now, null, url, name)
//        });
//
//        System.out.println((double) (System.currentTimeMillis() - start) / 1000);

        return ResponseEntity.ok(new BoolResponse());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<BoolResponse> stopIndexing() {
        return ResponseEntity.ok(new BoolResponse());
    }

    @GetMapping("/indexPage")
    public ResponseEntity<BoolResponse> indexPage() {
        return ResponseEntity.ok(new BoolResponse());
    }

//    private void saveLemmas(String text) throws IOException {
//        LuceneMorphology luceneMorphRus = new RussianLuceneMorphology();
//        LuceneMorphology luceneMorphEng = new RussianLuceneMorphology();
//
//        String[] words = text.replaceAll("[^а-яА-ЯёЁa-zA-Z\\s]", " ").split("\\s+");
//        Set<String> wordSet = new HashSet<>(List.of(words));
//
//        System.out.println(wordSet);
//
//        HashSet<String> lemmasSet = new HashSet<>();
//        for (String word : wordSet) {
//            if (luceneMorphEng.checkString(word)) {
//                List<String> lemmas = luceneMorphEng.getNormalForms(word);
//                lemmasSet.addAll(lemmas);
//            } else if (luceneMorphRus.checkString(word)) {
//                List<String> lemmas = luceneMorphRus.getNormalForms(word);
//                lemmasSet.addAll(lemmas);
//            }
//        }
//
//        for (String lemma : lemmasSet) {
//            Optional<LemmaModel> lemmaModelOptional = lemmaRepository.findByLemma(lemma);
//            if (lemmaModelOptional.isPresent()) {
//                lemmaRepository.updateFrequencyById(lemmaModelOptional.get().getId());
//            } else {
//                lemmaRepository.saveAndFlush(new LemmaModel(1, lemma, 1));
//            }
//        }
//    }
}
