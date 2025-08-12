<template>
  <div>
    <h2>Apparition Sites</h2>
    <p style="color: red;" v-if="error">Error: {{ error }}</p>
    <p v-if="sites.length === 0 && !error">Loading or no data...</p>
    <table border="1" width="100%">
      <tr>
        <th>Name</th>
        <th>Location</th>
        <th>Date</th>
        <th>Status</th>
        <th>Description</th>
      </tr>
      <tr v-for="site in sites" :key="site.id">
        <td>{{ site.name }}</td>
        <td>{{ site.location }}</td>
        <td>{{ site.date }}</td>
        <td>{{ site.approvalStatus }}</td>
        <td>{{ site.description }}</td>
      </tr>
    </table>
  </div>
</template>
<script>
import axios from 'axios';
export default {
  data() {
    return { sites: [], error: null };
  },
  created() {
    axios.get('http://localhost:8080/api/apparitions')
      .then(response => {
        this.sites = response.data;
      })
      .catch(error => {
        this.error = error.message;
        console.error('Error fetching apparitions:', error);
      });
  }
};
</script>
<style>
table { margin: 20px 0; }
th, td { padding: 8px; text-align: left; }
th { background-color: #f2f2f2; }
</style>