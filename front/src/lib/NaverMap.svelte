<script>
	import { onMount } from 'svelte';

	let map;

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