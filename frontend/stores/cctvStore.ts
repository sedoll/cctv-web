import { defineStore } from 'pinia';

export const useCctvStore = defineStore('cctv', () => {
    const cctvList = ref([]);

    const fetchCctvs = async () => {
        try {
            // 1. 실제 API 호출 시도
            const res = await fetch('/api/cctv');
            if (res.ok) {
                const data = await res.json();
                if (data.length > 0) {
                    cctvList.value = data;
                    return;
                }
            }
            throw new Error("No Data"); // 데이터 없으면 캐치로 이동
        } catch (e) {
            // 2. 실패하거나 개발 모드일 때 더미 데이터 사용
            console.log("⚠️ API 연결 실패/개발 모드: 더미 데이터를 사용합니다.");
            cctvList.value = [
                { name: '강남대로 (신논현)', url: 'https://cctvsec.ktict.co.kr/2/3f0i8O+Ww==', status: '원활' },
                { name: '올림픽대로 (여의도)', url: '', status: '지체' },
                { name: '경부고속도로 (양재)', url: '', status: '정체' },
                { name: '강변북로 (반포)', url: '', status: '원활' }
            ];
        }
    };

    return { cctvList, fetchCctvs };
});