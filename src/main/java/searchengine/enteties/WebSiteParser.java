package searchengine.enteties;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.models.IndexModel;
import searchengine.models.LemmaModel;
import searchengine.models.PageModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;

import javax.persistence.NonUniqueResultException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;

public class WebSiteParser extends RecursiveAction {
    public static Set<String> links = Collections.synchronizedSet(new HashSet<>());
    private final int webSiteId;
    private final String webSiteName;
    private final String suffix;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public WebSiteParser(int webSiteId,
                         String webSiteName,
                         String suffix,
                         PageRepository pageRepository,
                         LemmaRepository lemmaRepository,
                         IndexRepository indexRepository
    ) {
        if (webSiteName.endsWith("/")) {
            webSiteName = webSiteName.substring(0, webSiteName.length() - 1);
        }

        this.webSiteId = webSiteId;
        this.webSiteName = webSiteName.replaceAll("//www\\.", "//");
        this.suffix = suffix;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public void compute() {
//        List<HtmlInfo> htmlInfoList = htmlRepository.findAll();
//        try {
//            for (HtmlInfo htmlInfo : htmlInfoList) {
//                String text = htmlInfo.getHtmlText();
//                saveLemmas(text);
//            }
//
//            List<LemmaModel> lemmaModelList = lemmaRepository.findAll();
//
//            lemmaModelList.forEach(System.out::println);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            System.out.println(webSiteName.concat(suffix));

            List<WebSiteParser> taskList = getTaskList();

            int taskListSize = taskList.size() - 1;
            for (int i = taskListSize; i >= 0; i--) {
//                System.out.println(i);
                WebSiteParser parser = taskList.get(i);
                parser.join();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Elements getLinkElements(Document document) {
        return document.select("a");
    }

    private List<WebSiteParser> getTaskList() throws IOException {
        List<WebSiteParser> taskList = new ArrayList<>();

        Document document = getDocument();
        Elements elements = getLinkElements(document);
        String content = document.text();

        if (!content.isBlank()) {
            PageModel pageModel = new PageModel(webSiteId, suffix, 200, content);
            PageModel savedPageModel = pageRepository.saveAndFlush(pageModel);
            saveLemmas(content, savedPageModel.getId());

            // TODO : Можно ли использовать просто save ???
            //  Почему то он не сохраняет все страницы
        }

        for (Element element : elements) {
            String currSuffix = changeReservedCharacters(element.attr("href"));
            String fullLink = webSiteName.concat(currSuffix);
            if (linkSuffixFilter(currSuffix) && !links.contains(fullLink)) {
                links.add(fullLink);

                WebSiteParser parser = new WebSiteParser(
                        webSiteId,
                        webSiteName,
                        currSuffix,
                        pageRepository,
                        lemmaRepository,
                        indexRepository
                );
                parser.fork();
                taskList.add(parser);
            }
        }

        return taskList;
    }

    private void saveLemmas(String text, int pageId) throws IOException {
        LuceneMorphology luceneMorphRus = new RussianLuceneMorphology();
        LuceneMorphology luceneMorphEng = new RussianLuceneMorphology();

        String[] words = text.replaceAll("[^а-яА-ЯёЁa-zA-Z\\s]", " ").split("\\s+");
        Set<String> wordSet = new HashSet<>(List.of(words));

        HashMap<String, Integer> lemmaToCnt = new HashMap<>();
        List<String> lemmas = new ArrayList<>();
        for (String word : wordSet) {
            if (luceneMorphEng.checkString(word)) {
                lemmas = luceneMorphEng.getNormalForms(word);
            } else if (luceneMorphRus.checkString(word)) {
                lemmas = luceneMorphRus.getNormalForms(word);
            }

            lemmas.forEach(lemma -> lemmaToCnt.put(
                    lemma,
                    lemmaToCnt.containsKey(lemma) ? lemmaToCnt.get(lemma) + 1 : 1
            ));
        }

        try {
            for (Map.Entry<String, Integer> entry : lemmaToCnt.entrySet()) {
                String lemma = entry.getKey();
                Integer cnt = entry.getValue();

                Optional<LemmaModel> lemmaModelOptional = lemmaRepository.findByLemma(lemma);
                int lemmaId;
                if (lemmaModelOptional.isPresent()) {
                    lemmaId = lemmaModelOptional.get().getId();
                    lemmaRepository.updateFrequencyById(lemmaId);
                } else {
                    LemmaModel saveLemmaModel = lemmaRepository.saveAndFlush(
                            new LemmaModel(webSiteId, lemma, 1)
                    );
                    lemmaId = saveLemmaModel.getId();
                }

                indexRepository.saveAndFlush(new IndexModel(pageId, lemmaId, cnt));
            }
        } catch (NonUniqueResultException e) {
            e.printStackTrace();
        }
    }

    private Document getDocument() throws IOException {
        return Jsoup.connect(webSiteName.concat(suffix))
                .ignoreHttpErrors(true)
                .followRedirects(true)
                .ignoreContentType(true)
                .timeout(10_000)
                .get();
    }

    private boolean linkSuffixFilter(String suffix) {
        return suffix.startsWith("/") && !suffix.equals("/") && !links.contains(suffix);
    }

    private String changeReservedCharacters(String link) {
        return link
                .replaceAll("%20", " ")
                .replaceAll("%21", "!")
                .replaceAll("%22", "\"")
                .replaceAll("%23", "#")
                .replaceAll("%24", "$")
                .replaceAll("%25", "%")
                .replaceAll("%26", "&")
                .replaceAll("%27", "'")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%2A", "*")
                .replaceAll("%2B", "+")
                .replaceAll("%2C", ",")
                .replaceAll("%2F", "/")
                .replaceAll("%3A", ":")
                .replaceAll("%3B", ";")
                .replaceAll("%3D", "=")
                .replaceAll("%3F", "?")
                .replaceAll("%40", "@")
                .replaceAll("%5B", "[")
                .replaceAll("%5D", "]");
    }
}
