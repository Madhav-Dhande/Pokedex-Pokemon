package com.madhav.pokedex.pokemon;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin (origins = "http://localhost:3000")// allow CORS if you later host frontend separately
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getPokemon(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name must not be empty");
        }

        try {
            PokemonResponse response = pokemonService.getPokemon(name);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Pokemon not found: " + name);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                 .body("Failed to fetch data from upstream API");
        }
    }
}
