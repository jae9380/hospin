<script lang="ts">
	import { onMount } from 'svelte';
	import { loadNaverMapScript } from '$lib/naverMapLoader';

	export let lat: number;
	export let lng: number;
	export let minZoom: number = 10;
	export let maxZoom: number = 17;
	export let hospitals: {
		hospital_code: string;
		name: string;
		address: string;
		latitude: number;
		longitude: number;
	}[];

	const zoomPresets = [
		{ level: 13, label: '약 3km 보기', radius: 3000 },
		{ level: 14, label: '약 1km 보기', radius: 1000 },
		{ level: 15, label: '약 500m 보기', radius: 500 },
		{ level: 16, label: '약 200m 보기', radius: 200 }
	];

	let map: naver.maps.Map;
	let mapContainer: HTMLDivElement;
	let rangeCircle: naver.maps.Circle | null = null;
	let currentInfoWindow: naver.maps.InfoWindow | null = null;
	let hospitalMarkers: naver.maps.Marker[] = [];

	onMount(async () => {
		await loadNaverMapScript();

		map = new naver.maps.Map(mapContainer, {
			center: new naver.maps.LatLng(lat, lng),
			zoom: 14,
			minZoom,
			maxZoom
		});

		rangeCircle = new naver.maps.Circle({
			map,
			center: new naver.maps.LatLng(lat, lng),
			radius: 3000, // 3km
			strokeColor: '#2563eb',
			strokeOpacity: 0.4,
			strokeWeight: 2,
			fillColor: '#2563eb',
			fillOpacity: 0.12
		});

		naver.maps.Event.addListener(map, 'click', () => {
			if (currentInfoWindow) {
				currentInfoWindow.close();
				currentInfoWindow = null;
			}
		});

		naver.maps.Event.once(map, 'init', () => {
			const wrapper = document.createElement('div');
			wrapper.style.marginRight = '12px';
			wrapper.style.marginBottom = '12px';

			const btn = document.createElement('div');
			btn.innerText = '현재 위치로';
			btn.style.padding = '10px 14px';
			btn.style.background = 'rgba(255, 255, 255, 0.85)';
			btn.style.border = '1px solid #aaa';
			btn.style.borderRadius = '8px';
			btn.style.cursor = 'pointer';
			btn.style.fontSize = '14px';
			btn.style.fontWeight = '600';
			btn.style.boxShadow = '0 2px 6px rgba(0,0,0,0.25)';
			btn.style.userSelect = 'none';
			btn.style.opacity = '0.9';
			btn.style.transition = 'opacity 0.15s';

			btn.addEventListener('mouseenter', () => (btn.style.opacity = '1'));
			btn.addEventListener('mouseleave', () => (btn.style.opacity = '0.9'));

			naver.maps.Event.addDOMListener(btn, 'click', () => {
				map.panTo(new naver.maps.LatLng(lat, lng));
			});

			wrapper.appendChild(btn);
			map.controls[naver.maps.Position.BOTTOM_CENTER].push(wrapper);
		});

		// 줌 버튼
		naver.maps.Event.once(map, 'init', () => {
			const wrapper = document.createElement('div');
			wrapper.style.marginTop = '12px';
			wrapper.style.marginLeft = '12px';
			wrapper.style.display = 'flex';
			wrapper.style.flexDirection = 'column';
			wrapper.style.gap = '8px';

			zoomPresets.forEach(({ level, label, radius }) => {
				const btn = document.createElement('div');
				btn.innerText = label;
				btn.style.padding = '8px 14px';
				btn.style.background = 'rgba(255,255,255,0.75)';
				btn.style.backdropFilter = 'blur(6px)';
				btn.style.border = '1px solid rgba(0,0,0,0.15)';
				btn.style.borderRadius = '9999px';
				btn.style.cursor = 'pointer';
				btn.style.fontSize = '13px';
				btn.style.fontWeight = '600';
				btn.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
				btn.style.userSelect = 'none';
				btn.style.transition = 'all 0.15s ease';

				btn.addEventListener('mouseenter', () => (btn.style.background = 'rgba(255,255,255,0.9)'));
				btn.addEventListener('mouseleave', () => (btn.style.background = 'rgba(255,255,255,0.75)'));

				naver.maps.Event.addDOMListener(btn, 'click', () => {
					map.setZoom(level);
					map.panTo(new naver.maps.LatLng(lat, lng));

					if (rangeCircle) rangeCircle.setMap(null);

					rangeCircle = new naver.maps.Circle({
						map,
						center: new naver.maps.LatLng(lat, lng),
						radius,
						strokeColor: '#2563eb',
						strokeOpacity: 0.6,
						strokeWeight: 2,
						fillColor: '#2563eb',
						fillOpacity: 0.15
					});
				});

				wrapper.appendChild(btn);
			});

			map.controls[naver.maps.Position.TOP_LEFT].push(wrapper);
		});

		// 사용자 위치 마커
		new naver.maps.Marker({
			position: new naver.maps.LatLng(lat, lng),
			map,
			title: '내 위치',
			icon: {
				content:
					'<div style="background:#2563eb;width:14px;height:14px;border-radius:50%;border:2px solid white"></div>'
			}
		});
	});

	// 🔧 수정: hospitals를 좌표 기준으로 그룹핑
	$: if (map && hospitals?.length) {
		// 기존 마커 제거
		hospitalMarkers.forEach((m) => m.setMap(null));
		hospitalMarkers = [];

		// 🔧 수정: 위도/경도 기준 그룹핑
		const grouped = new Map<string, typeof hospitals>();

		hospitals.forEach((h) => {
			const key = `${h.latitude}_${h.longitude}`;
			if (!grouped.has(key)) grouped.set(key, []);
			grouped.get(key)!.push(h);
		});

		// 🔧 수정: 그룹 단위로 마커 생성
		grouped.forEach((group) => {
			const { latitude, longitude } = group[0];

			const marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(latitude, longitude),
				map,
				title: group.map((h) => h.name).join(', '),
				icon: {
					content:
						'<div style="background:#f00;width:14px;height:14px;border-radius:50%;border:2px solid white"></div>'
				}
			});

			// 🔧 수정: InfoWindow에 병원 리스트 렌더링
			const infoWindow = new naver.maps.InfoWindow({
				content: `
					<div style="max-width:240px; padding:8px; font-size:13px; position:relative;">
						<button class="close-btn" style="
							position:absolute;
							top:4px;
							right:4px;
							background:none;
							border:none;
							font-size:16px;
							cursor:pointer;
						">&times;</button>

						${group
							.map(
								(h) => `
								<div style="margin-bottom:8px;">
									<strong>${h.name}</strong>
									<div style="font-size:12px; color:#555;">${h.address}</div>
									<button
										style="margin-top:4px; width:100%; padding:4px 0;
										background:#2563eb; color:white; border:none;
										border-radius:4px; font-size:12px; cursor:pointer;"
										onclick="window.location.href='/hospital/${h.hospital_code}'">
										상세 페이지
									</button>
								</div>
							`
							)
							.join('<hr/>')}
					</div>
				`
			});

			naver.maps.Event.addListener(marker, 'click', () => {
				if (currentInfoWindow) currentInfoWindow.close();

				infoWindow.open(map, marker);
				currentInfoWindow = infoWindow;
				map.panTo(marker.getPosition());

				// 🔧 수정: 닫기 버튼 이벤트 연결
				const el = infoWindow.getElement();
				const closeBtn = el.querySelector('.close-btn');
				closeBtn?.addEventListener('click', () => {
					infoWindow.close();
					currentInfoWindow = null;
				});
			});

			hospitalMarkers.push(marker);
		});
	}

	export function moveTo(lat: number, lng: number) {
		if (!map) return;
		map.panTo(new naver.maps.LatLng(lat, lng));
	}
</script>

<div bind:this={mapContainer} style="width: 100%; height: 500px;" class="rounded-lg shadow-md" />
