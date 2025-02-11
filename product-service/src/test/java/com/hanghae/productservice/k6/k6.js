import http from 'k6/http';
import { check, sleep } from 'k6';
export let options = {
    stages: [
        { duration: '1m', target: 500 }, // 1분 동안 10,00명까지 증가
        { duration: '10s', target: 0 },     // 10 동안 종료
    ],
};
export default function () {
    const searchParam = encodeURIComponent('블랙');
    // 1. HTTP 요청 전송
    // const url=`http://localhost:9003/api/products?search=${searchParam}&size=10&cursor=1`;
    const url=`http://localhost:9003/api/products/1`;

    const payload = JSON.stringify({
        // search: '블랙',
        // size: 10,
        // cursor: 1,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    const res = http.get(url, payload);
    // 2. 응답 상태 확인
    check(res, {
        '응답 코드가 200이다': (r) => r.status === 200,
        // '응답 시간이 200ms 이하이다': (r) => r.timings.duration < 200,
    });
    // 3. 요청 사이 딜레이 추가
    sleep(1); // 1초 대기
}