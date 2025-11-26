package com.madhav.pokedex.pokemon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PokemonService {

    private static final String BASE_URL = "https://pokeapi.co/api/v2";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final InMemoryCache<String, PokemonResponse> cache;

    public PokemonService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        // TTL = 10 minutes, maxSize = 100 entries (you can tweak)
        this.cache = new InMemoryCache<>(10 * 60 * 1000L, 100);
    }

    public PokemonResponse getPokemon(String rawName) {
        String name = rawName.trim().toLowerCase(Locale.ROOT);

        // Try cache
        PokemonResponse cached = cache.get(name);
        if (cached != null) {
            return cached;
        }

        try {
            // Fetch main pokemon endpoint
            ResponseEntity<String> pokemonResp =
                    restTemplate.getForEntity(BASE_URL + "/pokemon/" + name, String.class);
            JsonNode pokemonJson = objectMapper.readTree(pokemonResp.getBody());

            // Fetch species endpoint for extra info
            ResponseEntity<String> speciesResp =
                    restTemplate.getForEntity(BASE_URL + "/pokemon-species/" + name, String.class);
            JsonNode speciesJson = objectMapper.readTree(speciesResp.getBody());

            PokemonResponse dto = mapToPokemonResponse(pokemonJson, speciesJson);

            cache.put(name, dto);
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            // 404 from PokeAPI: rethrow, will be handled by controller
            throw e;
        } catch (Exception e) {
            // For any other error, wrap and rethrow
            throw new RuntimeException("Error while fetching Pokemon data", e);
        }
    }

    private PokemonResponse mapToPokemonResponse(JsonNode pokemonJson, JsonNode speciesJson) {
        PokemonResponse dto = new PokemonResponse();

        dto.setId(pokemonJson.get("id").asInt());
        dto.setName(capitalize(pokemonJson.get("name").asText()));
        dto.setHeight(pokemonJson.get("height").asInt());
        dto.setWeight(pokemonJson.get("weight").asInt());
        dto.setBaseExperience(pokemonJson.get("base_experience").asInt());

        // Types
        List<String> types = new ArrayList<>();
        for (JsonNode typeNode : pokemonJson.get("types")) {
            String typeName = typeNode.get("type").get("name").asText();
            types.add(capitalize(typeName));
        }
        dto.setTypes(types);

        // Abilities
        List<String> abilities = new ArrayList<>();
        for (JsonNode abilityNode : pokemonJson.get("abilities")) {
            String abilityName = abilityNode.get("ability").get("name").asText();
            abilities.add(capitalize(abilityName));
        }
        dto.setAbilities(abilities);

        // Stats
        Map<String, Integer> stats = new LinkedHashMap<>();
        for (JsonNode statNode : pokemonJson.get("stats")) {
            String statName = statNode.get("stat").get("name").asText();
            int baseStat = statNode.get("base_stat").asInt();
            stats.put(formatStatName(statName), baseStat);
        }
        dto.setStats(stats);

        // Sprite
        JsonNode sprites = pokemonJson.get("sprites");
        if (sprites != null && sprites.get("front_default") != null) {
            dto.setSpriteUrl(sprites.get("front_default").asText());
        }

        // Species data
        dto.setLegendary(speciesJson.get("is_legendary").asBoolean());
        dto.setMythical(speciesJson.get("is_mythical").asBoolean());

        JsonNode habitatNode = speciesJson.get("habitat");
        dto.setHabitat(habitatNode != null && !habitatNode.isNull()
                ? capitalize(habitatNode.get("name").asText())
                : "Unknown");

        // Pick first English flavor text
        String flavorText = extractEnglishFlavorText(speciesJson);
        dto.setFlavorText(flavorText);

        return dto;
    }

    private String extractEnglishFlavorText(JsonNode speciesJson) {
        JsonNode flavorEntries = speciesJson.get("flavor_text_entries");
        if (flavorEntries == null || !flavorEntries.isArray()) {
            return "No description available.";
        }

        for (JsonNode entry : flavorEntries) {
            JsonNode language = entry.get("language");
            if (language != null && "en".equals(language.get("name").asText())) {
                String raw = entry.get("flavor_text").asText();
                // Clean up weird whitespace
                return raw.replace('\n', ' ')
                          .replace('\f', ' ')
                          .trim();
            }
        }
        return "No English description available.";
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }

    private String formatStatName(String statName) {
        // e.g. "special-attack" -> "Special Attack"
        String[] parts = statName.split("-");
        List<String> formatted = new ArrayList<>();
        for (String p : parts) {
            formatted.add(capitalize(p));
        }
        return String.join(" ", formatted);
    }
}
