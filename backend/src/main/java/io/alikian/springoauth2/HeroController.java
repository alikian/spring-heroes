package io.alikian.springoauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HeroController {
    HeroService heroService;

    public HeroController(@Autowired HeroService heroService) {
        this.heroService = heroService;
        heroService.saveHero(new Hero("Ironman"));
        heroService.saveHero(new Hero("Hulk"));
        heroService.saveHero(new Hero("SpiderMan"));
    }

    @GetMapping("/api/heroes/{id}")
    public Hero getHAlleroes(@PathVariable String id) {
        return heroService.getHero(id);
    }

    @GetMapping("/api/heroes")
    public List<Hero> getAllHeroes() {
        return heroService.getAllHeroes();
    }

    @GetMapping("/api/heroes/")
    public List<Hero> getHeroes(@RequestParam(required = false) String name) {
        if (!StringUtils.hasLength(name)) {
            return getAllHeroes();
        }
        List<Hero> heroesFound = new ArrayList<>();
        for (Hero hero : getAllHeroes()) {
            if (hero.getName().toLowerCase().startsWith(name)) {
                heroesFound.add(hero);
            }
        }
        return heroesFound;
    }

    @PostMapping("/api/heroes")
    public Hero addHeroes(@RequestBody Hero hero) {
        return heroService.saveHero(hero);
    }

    @PutMapping("/api/heroes")
    public Hero updateHeroes(@RequestBody Hero hero) {
        heroService.updateHero(hero);
        return hero;
    }


    @DeleteMapping("/api/heroes/{id}")
    public void deleteHeroes(@PathVariable String id) {
        heroService.deleteHero(id);
    }
}
