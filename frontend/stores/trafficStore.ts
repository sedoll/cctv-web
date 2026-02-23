import { defineStore } from 'pinia';

export const useTrafficStore = defineStore('traffic', () => {
    const events = ref([]);
    const currentPage = ref(1);
    const totalPages = ref(1);
    const pageSize = 10;

    const fetchEvents = async (page = 1) => {
        const res = await fetch(`/api/events?pageIndex=${page}&pageSize=${pageSize}`);
        if (!res.ok) {
            throw new Error('failed to fetch traffic events');
        }

        const data = await res.json();
        events.value = data.events || [];
        currentPage.value = data.currentPage || 1;
        totalPages.value = data.totalPages || 1;
    };

    return { events, currentPage, totalPages, fetchEvents };
});