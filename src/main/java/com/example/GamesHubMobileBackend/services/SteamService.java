package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.ResponseModels.SteamAppListItem;
import com.example.GamesHubMobileBackend.ResponseModels.SteamAppListResponse;
import com.example.GamesHubMobileBackend.enums.ImageType;
import com.example.GamesHubMobileBackend.models.Game;
import com.example.GamesHubMobileBackend.models.GameImage;
import com.example.GamesHubMobileBackend.models.SchedulerConfig;
import com.example.GamesHubMobileBackend.models.SteamGame;
import com.example.GamesHubMobileBackend.repositories.GameRepository;
import com.example.GamesHubMobileBackend.repositories.SchedulerConfigRepository;
import com.example.GamesHubMobileBackend.repositories.SteamGameRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SteamService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicInteger imageIdCounter = new AtomicInteger(1);
    private final SteamGameRepository steamGameRepository;
    private final GameRepository gameRepository;
    private final SchedulerConfigRepository schedulerConfigRepository;
    private int processedRecordCount = 0;
    @Value("${steam.api.key}")
    private String steamApiKey;

    public SteamService(SteamGameRepository steamGameRepository, GameRepository gameRepository, SchedulerConfigRepository schedulerConfigRepository) {
        this.steamGameRepository = steamGameRepository;
        this.gameRepository = gameRepository;
        this.schedulerConfigRepository = schedulerConfigRepository;
    }

    public Game fetchGameFromSteam(int steamAppId, String countryCode) {
        String url = "https://store.steampowered.com/api/appdetails?appids=" + steamAppId + "&cc=" + countryCode;

        Map response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            return null;
        }

        Map wrapper = (Map) response.get(String.valueOf(steamAppId));
        if (wrapper == null || !(Boolean) wrapper.get("success")) {
            return null;
        }

        Map data = (Map) wrapper.get("data");
        if (data == null) {
            return null;
        }

        Game game = new Game();
        game.setSteamGameId(steamAppId);
        game.setTitle((String) data.get("name"));
        game.setDescription((String) data.get("short_description"));

        // price (check is_free == true?? )
        Boolean isFree = (Boolean) data.get("is_free");
        if (isFree != null && isFree) {
            game.setPrice(0.0);
        } else {
            Map priceOverview = (Map) data.get("price_overview");
            if (priceOverview != null) {
                Integer finalPrice = (Integer) priceOverview.get("final");
                if (finalPrice != null) {
                    game.setPrice(finalPrice / 100.0);
                }
            }
        }

        // main img
        GameImage image = new GameImage();
        image.setId(imageIdCounter.getAndIncrement());
        image.setName((String) data.get("name"));
        image.setUrl((String) data.get("header_image"));
        image.setType(ImageType.JPG);
        game.setCoverImage(image);

        // side img
        List<GameImage> screenshots = new ArrayList<>();
        List<Map> screenshotData = (List<Map>) data.get("screenshots");
        if (screenshotData != null) {
            for (Map shot : screenshotData) {
                GameImage screenshot = new GameImage();
                screenshot.setId(imageIdCounter.getAndIncrement());
                screenshot.setName((String) data.get("name") + " Screenshot");
                screenshot.setUrl((String) shot.get("path_full"));
                screenshot.setType(ImageType.JPG);
                screenshots.add(screenshot);
            }
        }
        game.setScreenshots(screenshots);

        // trailer
        List<Map> movies = (List<Map>) data.get("movies");
        if (movies != null && !movies.isEmpty()) {
            Map firstMovie = movies.get(0);
            String trailerUrl = (String) firstMovie.get("hls_h264");
            game.setTrailerUrl(trailerUrl);
        }

        return game;
    }

    public SteamAppListResponse fetchAppList() {
        String url = "https://api.steampowered.com/IStoreService/GetAppList/v1/?key=" + steamApiKey + "&max_results=50000";
        return restTemplate.getForObject(url, SteamAppListResponse.class);
    }

    public int saveAppListToMongo() {
        SteamAppListResponse response = fetchAppList();
        if (response == null || response.getResponse() == null || response.getResponse().getApps() == null) {
            return 0;
        }

        List<SteamAppListItem> apps = response.getResponse().getApps();

        List<SteamGame> games = new ArrayList<>();
        for (SteamAppListItem item : apps) {
            boolean exists = steamGameRepository.existsByAppid(item.getAppid());
            if (!exists) {
                SteamGame game = new SteamGame();
                game.setAppid(item.getAppid());
                game.setName(item.getName());
                game.setProcessed(false);
                games.add(game);
            }
        }
        steamGameRepository.saveAll(games);
        return games.size();
    }


    //Scheduled 1
    @Scheduled(cron = "0 0 */12 * * *")// 12 hour
    public void scheduledSync() {
        int count = saveAppListToMongo();
        System.out.println("Scheduled sync completed: " + count + " games synced.");
    }


    // Scheduled 2
    @Scheduled(cron = "0 */2 * * * *") // 2 minit
    public void processUnprocessedGames() {
        List<SteamGame> unprocessed = steamGameRepository.findByProcessedFalse();

        if (unprocessed.isEmpty()) {
            System.out.println("No unprocessed games found.");
            resetProgress("processUnprocessedGames");
            return;
        }

        int processedRecordCount = getProgress("processUnprocessedGames");

        if (processedRecordCount >= unprocessed.size()) {
            processedRecordCount = 0;
        }

        int limit = Math.min(50, unprocessed.size() - processedRecordCount);
        int successCount = 0;

        for (int i = processedRecordCount; i < processedRecordCount + limit; i++) {
            SteamGame sg = unprocessed.get(i);

            Game fullGame = fetchGameFromSteam(sg.getAppid(), "my");

            if (fullGame != null) {
                gameRepository.save(fullGame);
                successCount++;
            }

            sg.setProcessed(true);
            steamGameRepository.save(sg);
        }

        saveProgress("processUnprocessedGames", processedRecordCount + limit);

        System.out.println("Processed " + limit + " games, " + successCount + " successfully fetched full data.");
    }

    // read progress
    private int getProgress(String schedulerName) {
        SchedulerConfig config = schedulerConfigRepository.findBySchedulerName(schedulerName);
        return config != null ? config.getLastProcessedIndex() : 0;
    }

    // save progress
    private void saveProgress(String schedulerName, int index) {
        SchedulerConfig config = schedulerConfigRepository.findBySchedulerName(schedulerName);
        if (config == null) {
            config = new SchedulerConfig();
            config.setSchedulerName(schedulerName);
        }
        config.setLastProcessedIndex(index);
        schedulerConfigRepository.save(config);
    }

    // reset progress
    private void resetProgress(String schedulerName) {
        saveProgress(schedulerName, 0);
    }


    // scheduled 3
    @Scheduled(cron = "0 */2 * * * TUE-WED")   // every tue and wed every 2 minit check (insert and update)
    public void refreshExistingGames() {
        List<SteamGame> processedGames = steamGameRepository.findByProcessedTrue();

        if (processedGames.isEmpty()) {
            System.out.println("No processed games to refresh.");
            resetProgress("refreshExistingGames");
            return;
        }

        int processedRecordCount = getProgress("refreshExistingGames");

        if (processedRecordCount >= processedGames.size()) {
            processedRecordCount = 0;
        }

        int limit = Math.min(50, processedGames.size() - processedRecordCount);
        int updateCount = 0;
        int insertCount = 0;

        for (int i = processedRecordCount; i < processedRecordCount + limit; i++) {
            SteamGame sg = processedGames.get(i);

            Game freshData = fetchGameFromSteam(sg.getAppid(), "my");
            if (freshData == null) {
                continue;
            }

            Game existing = gameRepository.findBySteamGameId(sg.getAppid());

            if (existing != null) {
                existing.setTitle(freshData.getTitle());
                existing.setDescription(freshData.getDescription());
                existing.setPrice(freshData.getPrice());
                existing.setCoverImage(freshData.getCoverImage());
                existing.setScreenshots(freshData.getScreenshots());
                existing.setTrailerUrl(freshData.getTrailerUrl());
                updateCount++;
            } else {
                gameRepository.save(freshData);
                insertCount++;
            }
        }

        saveProgress("refreshExistingGames", processedRecordCount + limit);

        System.out.println("Refreshed " + limit + " games: " + updateCount + " updated, " + insertCount + " inserted.");
    }
}
