<template>
  <div>
    <h2>Marian Apparition Sites</h2>
    <p style="color: red;" v-if="error">Error: {{ error }}</p>
    <p v-if="filteredSites.length === 0 && !error">No matching apparitions found...</p>
    <div class="controls">
      <div class="search">
        <label>Search: </label>
        <input v-model="searchQuery" placeholder="Search by name or location" />
      </div>
      <div class="sort-controls">
        <label>Sort by: </label>
        <button :class="{ active: sortKey === 'name' }" @click="setSort('name')">Name</button>
        <button :class="{ active: sortKey === 'date' }" @click="setSort('date')">Date</button>
        <label>Order: </label>
        <button :class="{ active: sortOrder === 'asc' }" @click="setOrder('asc')">Ascending</button>
        <button :class="{ active: sortOrder === 'desc' }" @click="setOrder('desc')">Descending</button>
      </div>
    </div>
    <table>
      <tr>
        <th>Name</th>
        <th>Location</th>
        <th>Date</th>
        <th>Status</th>
        <th>Description</th>
      </tr>
      <tr v-for="site in filteredSites" :key="site.id">
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
    return {
      sites: [],
      error: null,
      sortKey: 'name',
      sortOrder: 'asc',
      searchQuery: ''
    };
  },
  computed: {
    sortedSites() {
      return [...this.sites].sort((a, b) => {
        if (this.sortKey === 'date') {
          const dateA = new Date(a.date);
          const dateB = new Date(b.date);
          if (!dateA.getTime() || !dateB.getTime()) return 0;
          return this.sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
        } else {
          const keyA = a[this.sortKey].toLowerCase();
          const keyB = b[this.sortKey].toLowerCase();
          return this.sortOrder === 'asc' ? keyA.localeCompare(keyB) : keyB.localeCompare(keyA);
        }
      });
    },
    filteredSites() {
      const query = this.searchQuery.toLowerCase().trim();
      if (!query) return this.sortedSites;
      return this.sortedSites.filter(site =>
        site.name.toLowerCase().includes(query) ||
        site.location.toLowerCase().includes(query)
      );
    }
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
  },
  methods: {
    setSort(key) {
      this.sortKey = key;
    },
    setOrder(order) {
      this.sortOrder = order;
    }
  }
};
</script>
<style>
table {
  margin: 20px auto;
  border-collapse: collapse;
  width: 90%;
  font-family: Arial, sans-serif;
}
th, td {
  padding: 12px;
  text-align: left;
  border: 1px solid #e0e0e0;
  word-wrap: break-word;
}
th {
  background-color: #1e3a8a;
  color: white;
  font-weight: bold;
}
tr:nth-child(even) {
  background-color: #f9fafb;
}
tr:hover {
  background-color: #e5e7eb;
}
h2 {
  color: #1e3a8a;
  font-family: Arial, sans-serif;
  text-align: center;
  margin-bottom: 20px;
}
td:nth-child(5) {
  max-width: 300px;
}
.controls {
  text-align: center;
  margin: 10px 0;
}
.sort-controls {
  margin-top: 10px;
}
.sort-controls button {
  margin: 0 5px;
  padding: 6px 12px;
  background-color: #e5e7eb;
  border: none;
  cursor: pointer;
  font-family: Arial, sans-serif;
}
.sort-controls button.active {
  background-color: #1e3a8a;
  color: white;
}
.sort-controls button:hover {
  background-color: #3b82f6;
  color: white;
}
.search input {
  padding: 6px 12px;
  width: 200px;
  font-family: Arial, sans-serif;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
</style>