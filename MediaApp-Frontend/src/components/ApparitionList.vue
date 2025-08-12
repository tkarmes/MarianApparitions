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
        <th>Details</th>
      </tr>
      <tr v-for="site in filteredSites" :key="site.id">
        <td>{{ site.name }}</td>
        <td>{{ site.location }}</td>
        <td>{{ site.date }}</td>
        <td>{{ site.approvalStatus }}</td>
        <td>{{ site.description }}</td>
        <td><button class="details" @click="showDetails(site)">View Details</button></td>
      </tr>
    </table>
    <div v-if="selectedSite" class="modal">
      <div class="modal-content">
        <h3>{{ selectedSite.name }}</h3>
        <p><strong>Location:</strong> {{ selectedSite.location }}</p>
        <p><strong>Date:</strong> {{ selectedSite.date }}</p>
        <p><strong>Status:</strong> {{ selectedSite.approvalStatus }}</p>
        <p><strong>Description:</strong> {{ selectedSite.description }}</p>
        <button @click="selectedSite = null">Close</button>
      </div>
    </div>
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
      searchQuery: '',
      selectedSite: null
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
    },
    showDetails(site) {
      this.selectedSite = site;
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
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  font-family: Arial, sans-serif;
}
.modal-content h3 {
  color: #1e3a8a;
  text-align: center;
}
.modal-content p {
  margin: 10px 0;
}
.modal-content button {
  padding: 8px 16px;
  background-color: #1e3a8a;
  color: white;
  border: none;
  cursor: pointer;
}
.modal-content button:hover {
  background-color: #3b82f6;
}
button.details {
  padding: 6px 12px;
  background-color: #1e3a8a;
  color: white;
  border: none;
  cursor: pointer;
}
button.details:hover {
  background-color: #3b82f6;
}
</style>