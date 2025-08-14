import { createRouter, createWebHistory } from 'vue-router';
import ApparitionList from '../components/ApparitionList.vue';
import Timeline from '../components/Timeline.vue';

const routes = [
  { path: '/', component: ApparitionList },
  { path: '/timeline', component: Timeline }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;