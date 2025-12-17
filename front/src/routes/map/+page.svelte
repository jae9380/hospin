<script lang="ts">
	import { onMount } from 'svelte';
	import { au } from '$lib/au/au';
	import toast, { Toaster } from 'svelte-5-french-toast';
	import MapMyLocation from '$lib/MapMyLocation.svelte';

	let lat: number | null = null;
	let lng: number | null = null;
	let error: string | null = null;

	type HospitalListResponse = {
		hospital_code: string;
		name: string;
		address: string;
		latitude: number;
		longitude: number;
	};

	let hospitals: HospitalListResponse[] = [];

	onMount(async () => {
		if (!navigator.geolocation) {
			error = '이 브라우저는 위치 정보를 지원하지 않습니다.';
			return;
		}

		await new Promise<void>((resolve) => {
			navigator.geolocation.getCurrentPosition(
				(pos) => {
					lat = pos.coords.latitude;
					lng = pos.coords.longitude;
					console.log('사용자 위치:', lat, lng);
					resolve();
				},
				(err) => {
					error = '위치 정보를 가져오는데 실패했습니다: ' + err.message;
					resolve();
				}
			);
		});

		try {
			const { data, error } = await au.api().GET('/api/hospital/nearby', {
				params: {
					query: {
						latitude: String(lat),
						longitude: String(lng)
					}
				}
			});

			hospitals = data?.data ?? [];
		} catch (e: any) {
			console.error('❌ 관련 데이터를 불러오지 못했습니다:');
			toast.error('데이터 로드 실패 ❌');
			return;
		}
	});
</script>

<svelte:head>
	<title>근처 찾기</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	{#if lat && lng}
		<MapMyLocation {lat} {lng} {hospitals} />
	{/if}
	{#if lat && lng}
		<p>위도: {lat}</p>
		<p>경도: {lng}</p>
	{/if}

	{#if error}
		<p style="color:red">{error}</p>
	{/if}

	{#if hospitals.length > 0}
		<div class="mt-4 space-y-3">
			{#each hospitals as h}
				<div class="rounded-lg border bg-white p-4 shadow-sm">
					<div class="text-lg font-bold">{h.name}</div>
					<div class="text-sm text-gray-600">{h.address}</div>

					<div class="mt-1 text-xs text-gray-500">
						위도: {h.latitude} / 경도: {h.longitude}
					</div>
				</div>
			{/each}
		</div>
	{:else}
		<p class="mt-4 text-center text-gray-500">근처 병원이 없습니다.</p>
	{/if}
</div>
