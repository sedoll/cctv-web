import { defineStore } from 'pinia';

export const useTrafficStore = defineStore('traffic', () => {
    const events = ref([]);

    const fetchEvents = async () => {
        try {
            const res = await fetch('/api/events');
            if (res.ok) {
                const data = await res.json();
                if (data.length > 0) {
                    events.value = data;
                    return;
                }
            }
            throw new Error("No Data");
        } catch (e) {
            console.log("⚠️ 더미 돌발 정보를 로드합니다.");
            events.value = [
                { eventId: '1', type: '사고', message: '강남대로 신논현역 부근 3중 추돌 사고 발생. 2개 차로 차단됨.', coordX: 127.0, coordY: 37.5, updateTime: new Date() },
                { eventId: '2', type: '공사', message: '올림픽대로 잠실 방향 노면 보수 공사 중. 서행 운전 요망.', coordX: 127.1, coordY: 37.5, updateTime: new Date(Date.now() - 3600000) },
                { eventId: '3', type: '정체', message: '한남대교 북단 차량 증가로 인한 극심한 정체.', coordX: 127.0, coordY: 37.5, updateTime: new Date(Date.now() - 7200000) },
                { eventId: '4', type: '장해', message: '내부순환로 성산대교 방향 낙하물 발생 주의.', coordX: 126.9, coordY: 37.5, updateTime: new Date(Date.now() - 10000000) },
                { eventId: '5', type: '행사', message: '광화문 광장 집회로 인한 주변 도로 통제.', coordX: 126.9, coordY: 37.5, updateTime: new Date(Date.now() - 12000000) }
            ];
        }
    };

    return { events, fetchEvents };
});