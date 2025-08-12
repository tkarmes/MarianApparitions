<template>
  <div>
    <header>
      <h1>Marian Apparitions</h1>
    </header>
    <main>
      <ApparitionList />
    </main>
    <footer>
      <p>"Be original, not a photocopy." - Blessed Carlo Acutis</p>
    </footer>
  </div>
</template>
<script>
import ApparitionList from './components/ApparitionList.vue';
export default {
  components: { ApparitionList }
};
</script>
<style>
header, footer { text-align: center; padding: 20px; background: #1e3a8a; color: white; }
</style>