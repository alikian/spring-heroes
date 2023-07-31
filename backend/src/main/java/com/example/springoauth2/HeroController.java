package com.example.springoauth2;

import jakarta.websocket.server.PathParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HeroController {
    List<Hero> heroList = new ArrayList<>();

    public HeroController() {
        heroList.add(new Hero(1, "Ironman"));
        heroList.add(new Hero(2, "Hulk"));
        heroList.add(new Hero(3, "SpiderMan"));
    }

    @GetMapping("/api/heroes/{id}")
    public Hero getHeroes(@PathVariable Integer id) {
        return heroList.get(id - 1);
    }

    @GetMapping("/api/heroes")
    public List<Hero> getHeroes() {
        return heroList;
    }

    @GetMapping("/api/heroes/")
    public List<Hero> getHeroes(@RequestParam(required = false) String name) {
        if (!StringUtils.hasLength(name)) {
            return heroList;
        }
        List<Hero> heroesFound = new ArrayList<>();
        for (Hero hero : heroList) {
            if (hero.getName().toLowerCase().startsWith(name)) {
                heroesFound.add(hero);
            }
        }
        return heroesFound;
    }

    @PostMapping("/api/heroes")
    public Hero getHeroes(@RequestBody Hero hero) {
        hero.setId(heroList.size() + 1);
        heroList.add(hero);
        return hero;
    }

    @PutMapping("/api/heroes")
    public Hero updateHeroes(@RequestBody Hero hero) {
        Hero heroFound = heroList.get(hero.getId() - 1);
        heroFound.setName(hero.getName());
        return hero;
    }


    @DeleteMapping("/api/heroes/{id}")
    public void deleteHeroes(@PathVariable Integer id) {
        heroList.remove(id - 1);
    }
}
