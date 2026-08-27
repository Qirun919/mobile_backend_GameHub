package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.models.Game;
import com.example.GamesHubMobileBackend.repositories.SchedulerConfigRepository;
import com.example.GamesHubMobileBackend.repositories.SteamGameRepository;
import com.example.GamesHubMobileBackend.services.GameService;
import com.example.GamesHubMobileBackend.services.SteamService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    private GameService gameService;
    private SteamService steamService;
    private SteamGameRepository steamGameRepository;
    private SchedulerConfigRepository schedulerConfigRepository;

    public GameController(GameService gameService, SteamService steamService, SteamGameRepository steamGameRepository, SchedulerConfigRepository schedulerConfigRepository) {
        this.gameService = gameService;
        this.steamService  = steamService;
        this.steamGameRepository = steamGameRepository;
        this.schedulerConfigRepository = schedulerConfigRepository;
    }

    @GetMapping("/games/steam-applist/check")
    public ResponseEntity<Object> checkMongoData() {
        var count = steamGameRepository.count();
        var sample = steamGameRepository.findAll();
        return ResponseEntity.ok("Count: " + count + ", First few: " +
                (sample.isEmpty() ? "none" : sample.subList(0, Math.min(3, sample.size()))));
    }

    @GetMapping(
            path = "/games"
    )
    public ResponseEntity getGames() {
        var currentGames = gameService.getGames();
        if (CollectionUtils.isEmpty(currentGames)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentGames);
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<Object> getGameById(@PathVariable String id) {
        Game game = gameService.getGameById(id);

        if (game == null) {
            return ResponseEntity.badRequest().body("Game Not Found.");
        }
        return ResponseEntity.ok(game);
    }

    @GetMapping("/games/steam-applist")
    public ResponseEntity<Object> testAppList() {
        var result = steamService.fetchAppList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/scheduler/check")
    public ResponseEntity<Object> checkSchedulerConfig() {
        return ResponseEntity.ok(schedulerConfigRepository.findAll());
    }

    @PostMapping("/games/steam-applist/sync")
    public ResponseEntity<Object> syncAppList() {
        int count = steamService.saveAppListToMongo();
        return ResponseEntity.ok("Synced " + count + " games to MongoDB.");
    }

    @DeleteMapping("/games/steam-applist/clear")
    public ResponseEntity<Object> clearSteamGames() {
        steamGameRepository.deleteAll();
        return ResponseEntity.ok("All steam games cleared.");
    }

    @PostMapping("/games")
    public ResponseEntity<Object> saveGame(@RequestBody Game game) {

        if (game.getTitle() == null || game.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Game title is required.");
        }

        if (game.getPrice() < 0) {
            return ResponseEntity.badRequest().body("Price cannot be negative.");
        }

        return ResponseEntity.ok(gameService.saveGame(game));
    }


    @PostMapping("/games/import/{steamAppId}")
    public ResponseEntity<Object> importFromSteam(
            @PathVariable int steamAppId,
            @RequestParam(defaultValue = "my") String countryCode) {
        Game game = steamService.fetchGameFromSteam(steamAppId,countryCode);
        if (game == null) {
            return ResponseEntity.badRequest().body("Could not fetch game from Steam.");
        }
        return ResponseEntity.ok(gameService.saveGame(game));
    }



    @PutMapping("/games/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable String id, @RequestBody Game game) {
        game.setId(id);
        return ResponseEntity.ok(gameService.updateGame(game));
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<Object> deleteGame(@PathVariable String id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().body("Game Have Been Delete");
    }


    @GetMapping("/games/popular")
    public ResponseEntity<Object> getPopularGames() {
        List<Game> games = gameService.getPopularGames(7);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/games/paged")
    public ResponseEntity<Object> getGamesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Game> games = gameService.getGamesPaged(page, size);
        return ResponseEntity.ok(games);
    }
}


