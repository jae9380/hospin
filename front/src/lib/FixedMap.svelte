<script lang="ts">
	import { onMount } from 'svelte';
	import { loadNaverMapScript } from '$lib/naverMapLoader';

	export let lat: number; // (필수)
	export let lng: number; // (필수)
	export let title: string = '고정 위치';

	// 줌 범위 (원하는 값으로 조정)
	export let minZoom: number = 15;
	export let maxZoom: number = 17;

	// 이동 제한 영역: 남서(southWest) / 북동(northEast) 좌표
	// 필요에 따라 props로 주입하거나, lat/lng 기준으로 적당히 구성
	export let boundsSW = { lat: lat - 0.005, lng: lng - 0.005 };
	export let boundsNE = { lat: lat + 0.005, lng: lng + 0.005 };

	let map: naver.maps.Map;

	// 좌표를 주어진 사각형 영역으로 클램프
	function clampCenterToBounds(center: naver.maps.LatLng, bounds: naver.maps.LatLngBounds) {
		const south = bounds.getSW().y;
		const west = bounds.getSW().x;
		const north = bounds.getNE().y;
		const east = bounds.getNE().x;

		const clampedLat = Math.min(Math.max(center.y, south), north);
		const clampedLng = Math.min(Math.max(center.x, west), east);
		return new naver.maps.LatLng(clampedLat, clampedLng);
	}

	onMount(async () => {
		await loadNaverMapScript();

		const MAX_BOUNDS = new naver.maps.LatLngBounds(
			new naver.maps.LatLng(boundsSW.lat, boundsSW.lng),
			new naver.maps.LatLng(boundsNE.lat, boundsNE.lng)
		);

		map = new naver.maps.Map('fixed-map', {
			center: new naver.maps.LatLng(lat, lng),
			zoom: 15,
			minZoom, // ← 줌 최소
			maxZoom // ← 줌 최대
			// 필요하다면 제스처 일부 비활성화 가능
			// scrollWheel: false,
			// pinchZoom: false,
			// draggable: true,
			// disableDoubleClickZoom: true,
		});
		naver.maps.Event.once(map, 'init', () => {
			// 컨테이너(div)를 하나 더 만들어 margin을 설정
			const wrapper = document.createElement('div');
			wrapper.style.marginRight = '12px';
			wrapper.style.marginBottom = '12px';

			const btn = document.createElement('div');
			btn.innerText = '병원으로';
			btn.style.padding = '10px 14px';
			btn.style.background = 'rgba(255, 255, 255, 0.85)'; // ← 반투명
			btn.style.border = '1px solid #aaa';
			btn.style.borderRadius = '8px';
			btn.style.cursor = 'pointer';
			btn.style.fontSize = '14px';
			btn.style.fontWeight = '600';
			btn.style.boxShadow = '0 2px 6px rgba(0,0,0,0.25)';
			btn.style.userSelect = 'none';
			btn.style.transition = 'opacity 0.15s';

			// hover 시 더 명확하게 보이도록 효과
			btn.addEventListener('mouseenter', () => {
				btn.style.opacity = '1';
			});
			btn.addEventListener('mouseleave', () => {
				btn.style.opacity = '0.9';
			});

			btn.style.opacity = '0.9';

			// wrapper 안에 버튼 삽입
			wrapper.appendChild(btn);

			// 우측 하단 위치 + 여백
			map.controls[naver.maps.Position.BOTTOM_RIGHT].push(wrapper);

			// 클릭 이벤트
			naver.maps.Event.addDOMListener(btn, 'click', () => {
				map.panTo(new naver.maps.LatLng(lat, lng));
			});
		});
		// 줌 변경 시 범위 강제
		naver.maps.Event.addListener(map, 'zoom_changed', (z: number) => {
			if (z < minZoom) map.setZoom(minZoom);
			if (z > maxZoom) map.setZoom(maxZoom);
		});

		// 이동 제한: idle(드래그/줌 후 지도 안정 시점)에서 중심을 영역 안으로 되돌림
		naver.maps.Event.addListener(map, 'idle', () => {
			const center = map.getCenter();
			if (!MAX_BOUNDS.hasLatLng(center)) {
				const fixed = clampCenterToBounds(center, MAX_BOUNDS);
				map.setCenter(fixed);
			}
		});

		// 마커
		new naver.maps.Marker({
			position: new naver.maps.LatLng(lat, lng),
			map,
			title,
			icon: {
				content: '<div style="background:#f00;width:12px;height:12px;border-radius:50%"></div>'
			}
		});
	});
</script>

<div id="fixed-map" style="width: 100%; height: 400px;"></div>
