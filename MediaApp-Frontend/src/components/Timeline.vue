<template>
  <div>
    <h2>Apparition Timeline</h2>
    <p style="color: red;" v-if="error">Error: {{ error }}</p>
    <p v-if="sites.length === 0 && !error && !loading">No apparitions found...</p>
    <p v-if="loading">Loading timeline...</p>
    <div class="timeline">
      <div v-for="site in sortedSites" :key="site.id" class="timeline-item">
        <div class="timeline-date">{{ site.date }}</div>
        <div class="timeline-content">
          <h3>{{ site.name }}</h3>
          <p><strong>Location:</strong> {{ site.location }}</p>
          <p><strong>Status:</strong> {{ site.approvalStatus }}</p>
          <p><strong>Description:</strong> {{ site.description }}</p>
          <p v-if="site.imageUrl && !imageErrors[site.id]">
            <strong>Image:</strong><br>
            <img
              :src="site.imageUrl"
              alt="Apparition Image"
              class="timeline-image"
              @error="handleImageError(site.id)"
              @load="imageLoaded[site.id] = true"
              :class="{ 'image-loading': !imageLoaded[site.id] }"
            />
            <span v-if="!imageLoaded[site.id] && !imageErrors[site.id]" class="image-placeholder">Loading image...</span>
          </p>
          <p v-else><strong>Image:</strong> No image available</p>
          <p v-if="site.location">
            <strong>Map:</strong><br>
            <a
              :href="`https://www.openstreetmap.org/search?query=${encodeURIComponent(site.location)}`"
              target="_blank"
              class="map-link"
            >View {{ site.location }} on OpenStreetMap</a>
          </p>
          <p v-else><strong>Map:</strong> No location available</p>
        </div>
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
      loading: true,
      imageErrors: {},
      imageLoaded: {}
    };
  },
  computed: {
    sortedSites() {
      return [...this.sites].sort((a, b) => {
        const dateA = new Date(a.date);
        const dateB = new Date(b.date);
        if (!dateA.getTime() || !dateB.getTime()) return 0;
        return dateA - dateB;
      });
    }
  },
  created() {
    this.loading = true;
    axios.get('http://localhost:8080/api/apparitions')
      .then(response => {
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
    handleImageError(id) {
      this.imageErrors[id] = true;
      this.imageLoaded[id] = false;
    }
  }
};
</script>
<style>
h2 {
  color: #1e3a8a;
  font-family: Arial, sans-serif;
  text-align: center;
  margin-bottom: 20px;
}
.timeline {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px 0;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 120px;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, #1e3a8a, #3b82f6);
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
}
.timeline-item {
  display: flex;
  margin-bottom: 20px;
  position: relative;
  opacity: 0;
  animation: fadeIn 0.5s ease-in forwards;
}
.timeline-item:nth-child(1) { animation-delay: 0.1s; }
.timeline-item:nth-child(2) { animation-delay: 0.2s; }
.timeline-item:nth-child(3) { animation-delay: 0.3s; }
.timeline-item::before {
  content: '';
  position: absolute;
  left: 116px;
  top: 10px;
  width: 12px;
  height: 12px;
  background: #1e3a8a;
  border: 2px solid #fff;
  border-radius: 50%;
  z-index: 1;
  transition: transform 0.3s ease;
}
.timeline-item:hover::before {
  transform: scale(1.2);
}
.timeline-content {
  flex: 1;
  background: #f9fafb;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: Arial, sans-serif;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.timeline-content:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
.timeline-content h3 {
  color: #1e3a8a;
  margin-top: 0;
}
.timeline-content p {
  margin: 10px 0;
}
.timeline-image {
  max-width: 100%;
  width: 300px;
  height: auto;
  border-radius: 4px;
  margin-top: 10px;
  display: block;
  border: 1px solid #e0e0e0;
  opacity: 1;
  transition: opacity 0.3s ease-in-out;
}
.image-loading {
  opacity: 0;
}
.image-placeholder {
  color: #666;
  font-style: italic;
}
.map-link {
  color: #1e3a8a;
  text-decoration: none;
  font-weight: bold;
}
.map-link:hover {
  color: #3b82f6;
  text-decoration: underline;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>