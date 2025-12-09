<script lang="ts">
	import { onMount } from 'svelte';
	import { loadNaverMapScript } from '$lib/naverMapLoader';

	let map: naver.maps.Map;
	let mapContainer: HTMLDivElement;

	onMount(async () => {
		await loadNaverMapScript();

		navigator.geolocation.getCurrentPosition(
			(pos) => {
				const userLat = pos.coords.latitude;
				const userLng = pos.coords.longitude;
				console.log(userLat, userLng);

				// 지도 초기화
				map = new naver.maps.Map(mapContainer, {
					center: new naver.maps.LatLng(userLat, userLng),
					zoom: 14
				});

				// 사용자 위치 마커
				new naver.maps.Marker({
					position: new naver.maps.LatLng(userLat, userLng),
					map,
					title: '내 위치',
					icon: {
						content: '<div style="background:#00f;width:12px;height:12px;border-radius:50%"></div>'
					}
				});

				// 지도 위에 버튼 추가
				const controlDiv = document.createElement('div');
				controlDiv.style.margin = '10px';

				// 버튼 HTML
				controlDiv.innerHTML = `
        <button id="btnDefault" style="
            padding: 5px 10px;
            background: #1e40af;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        ">원래 위치</button>
    `;

				// 지도 오른쪽 상단에 추가
				map.controls[naver.maps.Position.TOP_RIGHT].push(controlDiv);
			},
			(err) => {
				// 테스트를 위해 작성
				console.warn('위치 정보를 가져올 수 없습니다.', err);

				// 위치 가져오기 실패 시 기본 좌표 사용
				const defaultLat = 37.3595704;
				const defaultLng = 127.105399;

				map = new naver.maps.Map(mapContainer, {
					center: new naver.maps.LatLng(defaultLat, defaultLng),
					zoom: 14
				});
			}
		);
	});
</script>

<div bind:this={mapContainer} style="width: 100%; height: 500px;"></div>
