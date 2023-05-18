package searchengine.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.enteties.WebSiteParser;
import searchengine.models.SiteModel;
import searchengine.models.enums.SiteStatus;
import searchengine.repositories.*;
import searchengine.services.interfaces.IndexingService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
public class IndexingServiceImpl implements IndexingService {
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private HtmlRepository htmlRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private IndexRepository indexRepository;
    private boolean isAbleToIndex;
    @Autowired
    private SitesList sitesList;

    @Override
    public boolean stopIndexing() {
        isAbleToIndex = false;
        return true;
    }

    @Override
    public void startIndexing() {
        isAbleToIndex = true;
        List<Site> sites = sitesList.getSites();
        for (Site site : sites) {
            if (!isAbleToIndex) {
                break;
            }

            SiteModel siteModel = new SiteModel(
                    SiteStatus.INDEXING, Date.valueOf(LocalDate.now()), site.getUrl(), site.getName()
            );
            SiteModel savedSiteModel = siteRepository.saveAndFlush(siteModel);

             new ForkJoinPool(Runtime.getRuntime().availableProcessors())
                    .submit(new WebSiteParser(
                            savedSiteModel.getId(),
                            site.getUrl(),
                            "",
                            pageRepository,
                            lemmaRepository,
                            indexRepository
                    )).join();

            siteRepository.updateStatusById(siteModel.getId());
        }
    }


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
//    }

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
