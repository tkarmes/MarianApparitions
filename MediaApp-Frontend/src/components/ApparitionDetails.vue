<template>
  <div class="details">
    <h3>{{ apparition.name }}</h3>
    <p><strong>Location:</strong> {{ apparition.location }}</p>
    <p><strong>Date:</strong> {{ apparition.date }}</p>
    <p><strong>Status:</strong> {{ apparition.approvalStatus }}</p>
    <p><strong>Description:</strong> {{ apparition.description }}</p>
    <button @click="goBack">Back to List</button>
  </div>
</template>
<script>
import axios from 'axios';
export default {
  data() {
    return {
      apparition: {},
      error: null
    };
  },
  created() {
    const id = this.$route.params.id;
    axios.get(`http://localhost:8080/api/apparitions/${id}`)
      .then(response => {
        this.apparition = response.data;
      })
      .catch(error => {
        this.error = error.message;
        console.error('Error fetching apparition:', error);
      });
  },
  methods: {
    goBack() {
      this.$router.push('/');
    }
  }
};
</script>
<style>
.details {
  margin: 20px auto;
  padding: 20px;
  max-width: 600px;
  font-family: Arial, sans-serif;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}
h3 {
  color: #1e3a8a;
  text-align: center;
}
p {
  margin: 10px 0;
}
button {
  padding: 8px 16px;
  background-color: #1e3a8a;
  color: white;
  border: none;
  cursor: pointer;
  font-family: Arial, sans-serif;
}
button:hover {
  background-color: #3b82f6;
}
</style>