<script lang="ts">
	import { onMount } from 'svelte';
	import { loadNaverMapScript } from '$lib/naverMapLoader';

	export let lat: number;
	export let lng: number;
	export let hospitals: {
		hospital_code: string;
		name: string;
		address: string;
		latitude: number;
		longitude: number;
	}[];

	let map: naver.maps.Map;
	let mapContainer: HTMLDivElement;

	onMount(async () => {
		await loadNaverMapScript();

		// 지도 초기화 (사용자 위치 기준)
		map = new naver.maps.Map(mapContainer, {
			center: new naver.maps.LatLng(lat, lng),
			zoom: 14
		});

		// ✅ 사용자 위치 마커
		new naver.maps.Marker({
			position: new naver.maps.LatLng(lat, lng),
			map,
			title: '내 위치',
			icon: {
				content:
					'<div style="background:#2563eb;width:14px;height:14px;border-radius:50%;border:2px solid white"></div>'
			}
		});

		// ✅ 병원 마커들
		hospitals.forEach((h) => {
			new naver.maps.Marker({
				position: new naver.maps.LatLng(h.latitude, h.longitude),
				map,
				title: h.name
			});
		});
	});
</script>

<div bind:this={mapContainer} style="width: 100%; height: 500px;" class="rounded-lg shadow-md" />
