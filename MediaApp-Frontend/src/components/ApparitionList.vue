<template>
  <div>
    <h2>Marian Apparition Sites</h2>
    <p style="color: red;" v-if="error">Error: {{ error }}</p>
    <p v-if="filteredSites.length === 0 && !error && !loading">No matching apparitions found...</p>
    <p v-if="loading">Loading apparitions...</p>
    <div class="controls">
      <div class="search">
        <label>Search: </label>
        <input v-model="searchQuery" placeholder="Search by name or location" />
      </div>
      <div class="filter-controls">
        <label>Filter by Approval: </label>
        <button :class="{ active: approvalFilter === 'all' }" @click="setApprovalFilter('all')">All</button>
        <button :class="{ active: approvalFilter === 'approved' }" @click="setApprovalFilter('approved')">Approved Only</button>
        <button :class="{ active: approvalFilter === 'nihil-obstat' }" @click="setApprovalFilter('nihil-obstat')">Nihil Obstat</button>
      </div>
      <div class="sort-controls">
        <label>Sort by: </label>
        <button :class="{ active: sortKey === 'name' }" @click="setSort('name')">Name</button>
        <button :class="{ active: sortKey === 'date' }" @click="setSort('date')">Date</button>
        <label>Order: </label>
        <button :class="{ active: sortOrder === 'asc' }" @click="setSortOrder('asc')">Ascending</button>
        <button :class="{ active: sortOrder === 'desc' }" @click="setSortOrder('desc')">Descending</button>
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
    <div v-if="selectedSite" class="modal" :class="{ 'fade-in': selectedSite }">
      <div class="modal-content">
        <h3>{{ selectedSite.name }}</h3>
        <p><strong>Location:</strong> {{ selectedSite.location }}</p>
        <p><strong>Date:</strong> {{ selectedSite.date }}</p>
        <p><strong>Status:</strong> {{ selectedSite.approvalStatus }}</p>
        <p><strong>Description:</strong> {{ selectedSite.description }}</p>
        <p v-if="selectedSite.imageUrl && !imageError">
          <strong>Image:</strong><br>
          <img
            :src="selectedSite.imageUrl"
            :alt="`Image of ${selectedSite.name}`"
            class="apparition-image"
            @error="handleImageError"
            @load="imageLoaded = true"
            :class="{ 'image-loading': !imageLoaded }"
            loading="lazy"
          />
          <span v-if="!imageLoaded && !imageError" class="image-placeholder">Loading image...</span>
        </p>
        <p v-else>
          <strong>Image:</strong><br>
          <img
            src="https://placehold.co/400x300?text=No+Image"
            :alt="`No image for ${selectedSite.name}`"
            class="apparition-image"
          />
          <span v-if="!selectedSite.imageUrl" class="image-placeholder">No image URL provided</span>
          <span v-if="imageError" class="image-placeholder">Failed to load image</span>
        </p>
        <p v-if="selectedSite.location">
          <strong>Map:</strong><br>
          <iframe
            :src="getMapUrl(selectedSite.location)"
            class="map-iframe"
            :title="`Map of ${selectedSite.name} location`"
            @load="mapLoaded = true"
            allowfullscreen
          ></iframe>
          <span v-if="!mapLoaded" class="map-placeholder">Loading map...</span>
        </p>
        <p v-else><strong>Map:</strong> No location available</p>
        <button class="close-button" @click="closeModal">Close</button>
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
      selectedSite: null,
      loading: true,
      imageError: false,
      imageLoaded: false,
      mapLoaded: false,
      approvalFilter: 'all'
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
      let result = this.sortedSites;
      if (this.approvalFilter === 'approved') {
        result = result.filter(site => {
          const status = site.approvalStatus ? site.approvalStatus.toLowerCase() : '';
          const matches = status.includes('approved') || status.includes('vatican-approved');
          console.log(`Filter - Site: ${site.name}, Status: ${status}, Matches: ${matches}`);
          return matches;
        });
      } else if (this.approvalFilter === 'nihil-obstat') {
        result = result.filter(site => {
          const status = site.approvalStatus ? site.approvalStatus.toLowerCase() : '';
          const matches = status.includes('nihil obstat');
          console.log(`Filter - Site: ${site.name}, Status: ${status}, Matches: ${matches}`);
          return matches;
        });
      }
      const query = this.searchQuery.toLowerCase().trim();
      if (!query) return result;
      return result.filter(site =>
        site.name.toLowerCase().includes(query) ||
        site.location.toLowerCase().includes(query)
      );
    }
  },
  created() {
    this.loading = true;
    axios.get('http://localhost:8080/api/apparitions')
      .then(response => {
        console.log('API response:', response.data);
        this.sites = response.data;
        this.loading = false;
      })
      .catch(error => {
        this.error = error.message;
        this.loading = false;
        console.error('Error fetching apparitions:', error);
      });
  },
  methods: {
    setSort(key) {
      this.sortKey = key;
    },
    setSortOrder(order) {
      this.sortOrder = order;
    },
    setApprovalFilter(value) {
      console.log('Setting approvalFilter to:', value);
      this.approvalFilter = value;
    },
    showDetails(site) {
      this.selectedSite = { ...site };
      this.imageError = false;
      this.imageLoaded = false;
      this.mapLoaded = false;
      console.log('Selected site:', this.selectedSite);
      console.log('Image URL:', this.selectedSite.imageUrl);
      document.body.classList.add('modal-open');
    },
    closeModal() {
      this.selectedSite = null;
      this.imageError = false;
      this.imageLoaded = false;
      this.mapLoaded = false;
      document.body.classList.remove('modal-open');
    },
    handleImageError() {
      this.imageError = true;
      this.imageLoaded = false;
      console.error('Image failed to load:', this.selectedSite.imageUrl);
    },
    getMapUrl(location) {
      console.log('Location:', location);
      const mapUrls = {
        'Fatima, Portugal': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3153.1234!2d-8.6527!3d39.6172!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMznCsDM3JzAyLjkiTiA4wrA0MScwOS4zIlc!5e0!3m2!1sen!2sus!4v1697051234567',
        'Mexico City, Mexico': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3762.3456!2d-99.1182!3d19.4840!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMTnCsDI5JzAyLjQiTiA5OcKwMDcnMDUuNSJX!5e0!3m2!1sen!2sus!4v1697051234568',
        'Lourdes, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2907.1234!2d-0.0461!3d43.0942!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDPCsDA1JzM5LjEiTiAwwrAwMicyNy44IkU!5e0!3m2!1sen!2sus!4v1697051234569',
        'Banneux, Belgium': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2526.1234!2d5.7356!3d50.5388!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNTDCsDMyJzE5LjciTiA1wrA0NCcwNy43IkU!5e0!3m2!1sen!2sus!4v1697051234570',
        'Beauraing, Belgium': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2534.1234!2d4.9600!3d50.1100!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNTDCsDA2JzM2LjAiTiA0wrA1NyczNi4wIkU!5e0!3m2!1sen!2sus!4v1697051234571',
        'Pontmain, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2640.1234!2d-0.5400!3d48.4400!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDjCsDI2JzI0LjAiTiAwwrAwMicyNC40Ilc!5e0!3m2!1sen!2sus!4v1697051234572',
        'Knock, Ireland': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2378.1234!2d-8.9035!3d53.7950!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNTPCsDQ3JzQyLjAiTiA4wrA1NCcxMi42Ilc!5e0!3m2!1sen!2sus!4v1697051234573',
        'Laus, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2789.1234!2d6.1600!3d44.6600!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDTCsDM5JzM2LjAiTiA2wrAwOSczNi4wIkU!5e0!3m2!1sen!2sus!4v1697051234574',
        'Paris, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2624.1234!2d2.3400!3d48.8500!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDjCsDUxJzAwLjAiTiAywrAyMCcyNC4wIkU!5e0!3m2!1sen!2sus!4v1697051234575',
        'La Salette, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2790.1234!2d5.9800!3d44.8600!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDTCsDUxJzM2LjAiTiA1wrA1OCc0OC4wIkU!5e0!3m2!1sen!2sus!4v1697051234576',
        'Medjugorje, Bosnia and Herzegovina': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2878.1234!2d17.6778!3d43.1900!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDPCsDExJzI0LjAiTiAxN8KwNDAnNDAuMSJF!5e0!3m2!1sen!2sus!4v1697051234578',
        'Akita, Japan': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3120.1234!2d140.1497!3d39.7596!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMznCsDQ1JzM0LjYiTiAxNDDCsDA4JzU4LjkiRQ!5e0!3m2!1sen!2sus!4v1697051234579'
      };
      return mapUrls[location] || 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d48389.6617!2d0!3d0!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMDDCsDAwJzAwLjAiTiAwwrAwMCcwMC4wIkU!5e0!3m2!1sen!2sus!4v1697051234577';
    }
  }
};
</script>
<style>
body.modal-open {
  overflow: hidden;
}
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
.filter-controls {
  margin: 10px 0;
}
.filter-controls button {
  margin: 0 5px;
  padding: 6px 12px;
  background-color: #e5e7eb;
  border: none;
  cursor: pointer;
  font-family: Arial, sans-serif;
}
.filter-controls button.active {
  background-color: #1e3a8a;
  color: white;
}
.filter-controls button:hover {
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
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
  overflow-y: auto;
  padding: 20px;
}
.modal.fade-in {
  opacity: 1;
}
.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  font-family: Arial, sans-serif;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
  box-sizing: border-box;
}
.modal-content h3 {
  color: #1e3a8a;
  text-align: center;
  margin: 0 0 10px;
}
.modal-content p {
  margin: 10px 0;
}
.modal-content button.close-button {
  position: sticky;
  bottom: 10px;
  align-self: center;
  padding: 8px 16px;
  background-color: #1e3a8a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  z-index: 10;
}
.modal-content button.close-button:hover {
  background-color: #3b82f6;
}
.modal-content button.close-button:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}
.apparition-image {
  max-width: 100%;
  width: auto;
  max-height: 300px;
  height: auto;
  border-radius: 4px;
  margin: 10px auto;
  display: block;
  border: 1px solid #e0e0e0;
  opacity: 1;
  transition: opacity 0.3s ease-in-out;
  object-fit: contain;
}
.image-loading {
  opacity: 0;
}
.image-placeholder {
  color: #666;
  font-style: italic;
  text-align: center;
  margin: 10px 0;
}
.map-iframe {
  width: 100%;
  max-width: 400px;
  height: 300px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin: 10px auto;
  display: block;
  aspect-ratio: 4 / 3;
}
.map-placeholder {
  color: #666;
  font-style: italic;
  text-align: center;
  margin: 10px 0;
}
@media (max-width: 600px) {
  .modal {
    padding: 10px;
  }
  .modal-content {
    width: 95%;
    max-height: 85vh;
    padding: 15px;
  }
  .apparition-image {
    max-height: 200px;
    width: auto;
  }
  .map-iframe {
    max-width: 100%;
    height: 200px;
    aspect-ratio: 4 / 3;
  }
}
</style>