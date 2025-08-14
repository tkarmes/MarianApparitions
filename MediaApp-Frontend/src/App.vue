<template>
  <div>
    <header>
      <h1>Marian Apparitions</h1>
    </header>
    <nav>
      <router-link to="/">Apparition List</router-link> |
      <router-link to="/timeline">Timeline</router-link>
    </nav>
    <main>
      <router-view />
    </main>
    <footer>
      <p>"Be original, not a photocopy." - Blessed Carlo Acutis</p>
    </footer>
  </div>
</template>
<script>
export default {}
</script>
<style>
header, footer {
  text-align: center;
  padding: 20px;
  background: #1e3a8a;
  color: white;
}
nav {
  text-align: center;
  margin: 10px 0;
}
nav a {
  color: #1e3a8a;
  text-decoration: none;
  margin: 0 10px;
  font-weight: bold;
}
nav a:hover {
  color: #3b82f6;
  text-decoration: underline;
}
main {
  margin: 0 auto;
  max-width: 1200px;
}
</style>