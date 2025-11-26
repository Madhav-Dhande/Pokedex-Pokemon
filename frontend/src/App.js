import React, { useState } from "react";

function App() {
  const [name, setName] = useState("");
  const [pokemon, setPokemon] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const search = async (e) => {
    e.preventDefault();
    if (!name.trim()) return;

    setError("");
    setPokemon(null);
    setLoading(true);

    try {
      const res = await fetch(`/api/pokemon/${name.trim().toLowerCase()}`);
      const json = await res.json();

      // backend returns either PokemonResponse or { error: "..." }
      if (json.error) {
        setError(json.error);
        setPokemon(null);
      } else {
        setPokemon(json);
      }
    } catch (err) {
      setError("Pokemon not found.");
    } finally {
      setLoading(false);
    }
  };

  // Convert "stats" object → array of {name, value}
  const statsArray =
    pokemon && pokemon.stats
      ? Object.entries(pokemon.stats).map(([key, value]) => ({
          name: key,
          value,
        }))
      : [];

  return (
    <div className="container">
      <h1>Pokedex</h1>

      <form onSubmit={search}>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Enter Pokémon"
        />
        <button type="submit">Search</button>
      </form>

      {loading && <p>Loading...</p>}
      {error && <p className="error">{error}</p>}

      {pokemon && (
        <div className="card">
          {/* LEFT SIDE: name, image, basic info */}
          <div className="left">
            <h2 className="pokemon-name">
              {pokemon.name} <span>#{pokemon.id}</span>
            </h2>

            <img
              src={pokemon.spriteUrl}
              alt={pokemon.name}
              className="pokemon-image"
            />

            <div className="info">
              <p>
                <strong>Height:</strong> {pokemon.height}
              </p>
              <p>
                <strong>Weight:</strong> {pokemon.weight}
              </p>
              <p>
                <strong>Types:</strong> {pokemon.types.join(", ")}
              </p>
              <p>
                <strong>Abilities:</strong> {pokemon.abilities.join(", ")}
              </p>
            </div>
          </div>

          {/* RIGHT SIDE: stats + flavor + meta */}
          <div className="right">
            <section>
              <h3>Stats</h3>
              <div className="stats">
                {statsArray.map((s) => (
                  <div className="stat-row" key={s.name}>
                    <span className="stat-name">{s.name}</span>
                    <span className="stat-value">{s.value}</span>
                  </div>
                ))}
              </div>
            </section>

            <section>
              <h3>Flavor Text</h3>
              <p className="flavor">{pokemon.flavorText}</p>

              <div className="badges">
                <span className="badge">Habitat: {pokemon.habitat}</span>
                <span className="badge">
                  Legendary: {pokemon.legendary ? "Yes" : "No"}
                </span>
                <span className="badge">
                  Mythical: {pokemon.mythical ? "Yes" : "No"}
                </span>
              </div>
            </section>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
