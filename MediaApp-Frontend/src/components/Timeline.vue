<template>
  <div>
    <h2>Apparition Timeline</h2>
    <div class="filter-controls">
      <label>Filter by Approval: </label>
      <button :class="{ active: approvalFilter === 'all' }" @click="approvalFilter = 'all'">All</button>
      <button :class="{ active: approvalFilter === 'approved' }" @click="approvalFilter = 'approved'">Approved Only</button>
    </div>
    <p style="color: red;" v-if="error">Error: {{ error }}</p>
    <p v-if="sortedSites.length === 0 && !error && !loading">No apparitions found...</p>
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
            <iframe
              :src="getMapUrl(site.location)"
              class="map-iframe"
              :title="`Map of ${site.name} location`"
              frameborder="0"
              scrolling="no"
              marginheight="0"
              marginwidth="0"
              allowfullscreen
            ></iframe>
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
      imageLoaded: {},
      approvalFilter: 'all'
    };
  },
  computed: {
    sortedSites() {
      let result = [...this.sites].sort((a, b) => {
        const dateA = new Date(a.date);
        const dateB = new Date(b.date);
        if (!dateA.getTime() || !dateB.getTime()) return 0;
        return dateA - dateB;
      });
      if (this.approvalFilter === 'approved') {
        result = result.filter(site => site.approvalStatus.toLowerCase() === 'approved');
      }
      return result;
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
    },
    getMapUrl(location) {
      const mapUrls = {
        'Fatima, Portugal': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3153.1234!2d-8.6527!3d39.6172!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMznCsDM3JzAyLjkiTiA4wrA0MScwOS4zIlc!5e0!3m2!1sen!2sus!4v1697051234567',
        'Mexico City, Mexico': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3762.3456!2d-99.1182!3d19.4840!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMTnCsDI5JzAyLjQiTiA5OcKwMDcnMDUuNSJX!5e0!3m2!1sen!2sus!4v1697051234568',
        'Lourdes, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2907.1234!2d-0.0461!3d43.0942!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDPCsDA1JzM5LjEiTiAwwrAwMicyNy44IkU!5e0!3m2!1sen!2sus!4v1697051234569',
        'Banneux, Belgium': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2526.1234!2d5.7356!3d50.5388!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNTDCsDMyJzE5LjciTiA1wrA0NCcwNy43IkU!5e0!3m2!1sen!2sus!4v1697051234570',
        'Beauraing, Belgium': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2534.1234!2d4.9600!3d50.1100!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNTDCsDA2JzM2LjAiTiA0wrA1NyczNi4wIkU!5e0!3m2!1sen!2sus!4v1697051234571',
        'Pontmain, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2640.1234!2d-0.5400!3d48.4400!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDjCsDI2JzI0LjAiTiAwwrAwMicyNC40Ilc!5e0!3m2!1sen!2sus!4v1697051234572',
        'Knock, Ireland': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2378.1234!2d-8.9035!3d53.7950!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNTPCsDQ3JzQyLjAiTiA4wrA1NCcxMi42Ilc!5e0!3m2!1sen!2sus!4v1697051234573',
        'Laus, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2789.1234!2d6.1600!3d44.6600!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDTCsDM5JzM2LjAiTiA2wrAwOSczNi4wIkU!5e0!3m2!1sen!2sus!4v1697051234574',
        'Paris, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2624.1234!2d2.3400!3d48.8500!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDjCsDUxJzAwLjAiNiAywrAyMCcyNC4wIkU!5e0!3m2!1sen!2us!4v1697051234575',
        'La Salette, France': 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2790.1234!2d5.9800!3d44.8600!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNDTCsDUxJzM2LjAiNiA1wrA1OCc0OC4wIkU!5e0!3m2!1sen!2us!4v1697051234576'
      };
      return mapUrls[location] || 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d48389.6617!2d0!3d0!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMDDCsDAwJzAwLjAiNiAwwrAwMCcwMC4wIkU!5e0!3m2!1sen!2us!4v1697051234577';
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
.filter-controls {
  margin: 10px 0;
  text-align: center;
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
.map-iframe {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-top: 10px;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style> ```