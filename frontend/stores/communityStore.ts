import { defineStore } from 'pinia';

export const useCommunityStore = defineStore('community', () => {
    const posts = ref([]);

    const fetchPosts = async () => {
        try {
            const res = await fetch('/api/community/posts');
            if (res.ok) {
                const data = await res.json();
                if (data.length > 0) {
                    posts.value = data;
                    return;
                }
            }
            throw new Error("No Data");
        } catch (e) {
            console.log("⚠️ 더미 게시글을 로드합니다.");
            posts.value = [
                { id: 1, content: '오늘 강변북로 출근길 진짜 헬이네요 ㅠㅠ 다들 조심하세요', member: { nickname: '김출근' }, createdAt: new Date() },
                { id: 2, content: '하이패스 단말기 추천 부탁드립니다. 인식이 잘 안되네요.', member: { nickname: '초보운전' }, createdAt: new Date(Date.now() - 86400000) },
                { id: 3, content: '[제보] 자유로 파주 방향 포트홀 조심하세요! 타이어 찢어질 뻔', member: { nickname: '도로지킴이' }, createdAt: new Date(Date.now() - 172800000) },
                { id: 4, content: '주말에 강원도 가려고 하는데 몇 시 출발이 좋을까요?', member: { nickname: '여행러' }, createdAt: new Date(Date.now() - 200000000) },
                { id: 5, content: '사고 목격자를 찾습니다. 어제 밤 10시경 사당역 사거리.', member: { nickname: '간절함' }, createdAt: new Date(Date.now() - 300000000) },
            ];
        }
    };

    return { posts, fetchPosts };
});