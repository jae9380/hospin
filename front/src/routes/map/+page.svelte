<script lang="ts">
	import { onMount } from 'svelte';
	import { au } from '$lib/au/au';
	import toast, { Toaster } from 'svelte-5-french-toast';
	import MapMyLocation from '$lib/MapMyLocation.svelte';

	let viewMode: 'map' | 'list' = 'map';

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

	let page = 0;
	const pageSize = 6;
	const MAX_PAGE_BUTTONS = 7;

	let keyword = '';
	let sortType: 'name' = 'name';

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
					resolve();
				},
				(err) => {
					error = '위치 정보를 가져오는데 실패했습니다: ' + err.message;
					resolve();
				}
			);
		});

		try {
			const { data } = await au.api().GET('/api/hospital/nearby', {
				params: {
					query: {
						latitude: String(lat),
						longitude: String(lng)
					}
				}
			});

			hospitals = data?.data ?? [];
		} catch {
			toast.error('데이터 로드 실패 ❌');
		}
	});

	$: filteredHospitals = hospitals.filter(
		(h) => h.name.includes(keyword) || h.address.includes(keyword)
	);

	$: sortedHospitals = [...filteredHospitals].sort((a, b) => a.name.localeCompare(b.name));

	$: totalPages = Math.ceil(sortedHospitals.length / pageSize);

	$: pagedHospitals = sortedHospitals.slice(page * pageSize, (page + 1) * pageSize);

	let startPage = 0;
	let endPage = 0;
	let visiblePageNumbers: number[] = [];

	$: {
		const half = Math.floor(MAX_PAGE_BUTTONS / 2);

		let start = Math.max(0, page - half);
		let end = Math.min(totalPages - 1, start + MAX_PAGE_BUTTONS - 1);

		start = Math.max(0, end - MAX_PAGE_BUTTONS + 1);

		startPage = start;
		endPage = end;

		visiblePageNumbers = Array.from({ length: end - start + 1 }, (_, i) => start + i);
	}

	function handleSearch(nextPage: number) {
		if (nextPage < 0 || nextPage >= totalPages) return;
		page = nextPage;
	}
</script>

<svelte:head>
	<title>근처 병원 찾기</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<div class="mb-6 flex justify-center gap-2">
		<button
			class="btn btn-sm"
			class:btn-primary={viewMode === 'map'}
			on:click={() => (viewMode = 'map')}
		>
			🗺️ 지도
		</button>

		<button
			class="btn btn-sm"
			class:btn-primary={viewMode === 'list'}
			on:click={() => (viewMode = 'list')}
		>
			📄 리스트
		</button>
	</div>

	{#if viewMode === 'map' && lat && lng}
		<MapMyLocation {lat} {lng} {hospitals} />
	{/if}

	{#if viewMode === 'list'}
		<div class="mb-4 flex justify-between gap-2">
			<input
				class="input-bordered input input-sm w-full max-w-xs"
				placeholder="병원명 또는 주소 검색"
				bind:value={keyword}
				on:input={() => (page = 0)}
			/>
		</div>

		{#if pagedHospitals.length > 0}
			<div class="space-y-3">
				{#each pagedHospitals as h}
					<div
						class="flex h-[96px] flex-col justify-between rounded-lg border bg-white p-4 shadow-sm"
					>
						<div class="line-clamp-1 text-lg font-bold">
							{h.name}
						</div>

						<div class="line-clamp-2 text-sm text-gray-600">
							{h.address}
						</div>
					</div>
				{/each}
			</div>
		{:else}
			<p class="mt-6 text-center text-gray-500">검색 결과가 없습니다.</p>
		{/if}

		{#if totalPages > 1}
			<div class="mt-6 flex items-center justify-center gap-1">
				<button class="btn btn-sm" disabled={page === 0} on:click={() => handleSearch(page - 1)}>
					◀
				</button>

				{#each visiblePageNumbers as p}
					<button
						class={`btn btn-sm ${p === page ? 'font-bold text-white btn-primary' : 'btn-ghost'}`}
						on:click={() => handleSearch(p)}
					>
						{p + 1}
					</button>
				{/each}

				<button
					class="btn btn-sm"
					disabled={page === totalPages - 1}
					on:click={() => handleSearch(page + 1)}
				>
					▶
				</button>
			</div>
		{/if}
	{/if}

	{#if error}
		<p class="mt-6 text-center text-red-500">{error}</p>
	{/if}
</div>
