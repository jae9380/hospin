<script>
	import { onMount } from 'svelte';

	let map;

	// // 예시 병원 데이터
	// let hospitals = [
	// 	{ name: "병원A", x: 127.105399, y: 37.3595704 },
	// 	{ name: "병원B", x: 127.110000, y: 37.360000 },
	// 	{ name: "병원C", x: 127.115000, y: 37.362000 }
	// ];

	// // 두 좌표 사이 거리 계산 (km)
	// function getDistance(lat1, lon1, lat2, lon2) {
	// 	const R = 6371; // 지구 반지름 km
	// 	const dLat = ((lat2 - lat1) * Math.PI) / 180;
	// 	const dLon = ((lon2 - lon1) * Math.PI) / 180;
	// 	const a =
	// 		Math.sin(dLat / 2) ** 2 +
	// 		Math.cos((lat1 * Math.PI) / 180) *
	// 			Math.cos((lat2 * Math.PI) / 180) *
	// 			Math.sin(dLon / 2) ** 2;
	// 	const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	// 	return R * c;
	// }

	// 네이버 지도 API 동적 로드
	function loadNaverMapScript() {
		return new Promise((resolve, reject) => {
			const script = document.createElement('script');
			script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${import.meta.env.NCP_KEY_ID}`;
			script.async = true;
			script.onload = resolve;
			script.onerror = reject;
			document.head.appendChild(script);
		});
	}

	onMount(async () => {
		await loadNaverMapScript();

		// 사용자 위치 가져오기
        navigator.geolocation.getCurrentPosition(
			(pos) => {
				const userLat = pos.coords.latitude;
				const userLng = pos.coords.longitude;

				// 지도 초기화
				map = new naver.maps.Map('map', {
					center: new naver.maps.LatLng(userLat, userLng),
					zoom: 14,
				});

				// 사용자 위치 마커
				new naver.maps.Marker({
					position: new naver.maps.LatLng(userLat, userLng),
					map,
					title: "내 위치",
					icon: {
						content: '<div style="background:#00f;width:12px;height:12px;border-radius:50%"></div>',
					},
				});

				// // 반경 3km 이내 병원 마커 표시
				// const radius = 3; // km
				// hospitals.forEach((h) => {
				// 	const distance = getDistance(userLat, userLng, h.y, h.x);
				// 	if (distance <= radius) {
				// 		new naver.maps.Marker({
				// 			position: new naver.maps.LatLng(h.y, h.x),
				// 			map,
				// 			title: h.name,
				// 		});
				// 	}
				// });
			},
			(err) => {
				console.warn("위치 정보를 가져올 수 없습니다.", err);
				// 위치 가져오기 실패 시 기본 좌표 사용
				const defaultLat = 37.3595704;
				const defaultLng = 127.105399;
				map = new naver.maps.Map('map', {
					center: new naver.maps.LatLng(defaultLat, defaultLng),
					zoom: 14,
				});
			}
		);
	});
</script>

<div id="map" style="width: 100%; height: 500px;"></div>