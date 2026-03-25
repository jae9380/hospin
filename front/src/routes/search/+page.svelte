<script lang="ts">
	import { onMount } from 'svelte';
	import { au } from '$lib/au/au';
	import { normalizeHospitalListItem } from '$lib/types/hospital/list';
	import { regionOptions } from '$lib/constants/regions';
	import { categoryOptions } from '$lib/constants/categories';
	import type { PageResult, HospitalListItemRaw, HospitalListItem } from '$lib/types/hospital/list';
	import { allDistrictOptions, getDistrictsByRegion } from '$lib/constants/districts';
	import type { DistrictOption } from '$lib/types/geo';
	import { browser } from '$app/environment';

	let name = '';
	let address = '';
	let selectedRegion = '';
	let selectedDistrict = '';
	let postalCode = '';
	let categoryCode = '';

	let page = 0;
	let totalPages = 0;

	let results: HospitalListItem[] = [];
	let hasSearched = false;
	let errorMsg = '';

	let districtOptions: DistrictOption[] = [];
	$: districtOptions = selectedRegion ? getDistrictsByRegion(selectedRegion) : [];

	onMount(() => {
		if (!browser) return;

		const params = new URLSearchParams(window.location.search);

		// 🔹 URL에서 모든 검색 조건 복원
		name = params.get('name') ?? '';
		categoryCode = params.get('categoryCode') ?? '';
		selectedRegion = params.get('regionCode') ?? '';
		selectedDistrict = params.get('districtCode') ?? '';
		postalCode = params.get('postalCode') ?? '';
		address = params.get('address') ?? '';
		page = Number(params.get('page') ?? 0);

		// 🔹 URL에 조건이 하나라도 있으면 검색 실행
		if (name || categoryCode || selectedRegion || selectedDistrict || postalCode || address) {
			handleSearch(page);
		}
	});

	async function handleSearch(targetPage: number, pageSize: number = 10) {
		hasSearched = true;
		errorMsg = '';
		page = Math.max(0, targetPage);

		try {
			const { data, error } = await au?.api().GET('/api/hospital/search', {
				params: {
					query: {
						name,
						categoryCode,
						regionCode: selectedRegion,
						districtCode: selectedDistrict,
						postalCode,
						address,
						page: String(page),
						size: String(pageSize)
					}
				}
			});

			if (error || !data?.data) {
				console.log(error);
				throw new Error(error?.message ?? 'API 응답 오류');
			}

			results = data.data.content?.map(normalizeHospitalListItem) ?? [];
			totalPages = data.data.totalPages ?? 0;

			if (results.length === 0) {
				errorMsg = '검색 결과가 없습니다.';
			} else {
				errorMsg = '';
			}

			updateUrlWithSearchParams();
		} catch (err: any) {
			results = [];
			totalPages = 0;
			errorMsg = '검색 중 서버에 문제가 발생했습니다.';
		}
	}
	$: pageNumbers = (() => {
		if (!totalPages) return [];
		const start = Math.max(0, page - 3);
		const end = Math.min(totalPages - 1, page + 3);

		// slice가 아니라 범위 직접 생성
		const pages = [];
		for (let p = start; p <= end; p++) pages.push(p);

		return pages;
	})();

	function updateUrlWithSearchParams() {
		if (!browser) return;

		const params = new URLSearchParams();
		if (name) params.set('name', name);
		if (categoryCode) params.set('categoryCode', categoryCode);
		if (selectedRegion) params.set('regionCode', selectedRegion);
		if (selectedDistrict) params.set('districtCode', selectedDistrict);
		if (postalCode) params.set('postalCode', postalCode);
		if (address) params.set('address', address);
		params.set('page', String(page));

		au?.goTo(`/search?${params.toString()}`, { replaceState: true });
	}
</script>

<svelte:head>
	<title>병원 검색</title>
</svelte:head>

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<div class="w-full max-w-3xl space-y-4 rounded-xl bg-white p-6 shadow-lg">
		<h2 class="text-center text-2xl font-bold">🔍 병원 검색</h2>

		<!-- 병원 이름 -->
		<input
			type="text"
			placeholder="병원 이름"
			bind:value={name}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
		/>

		<!-- 시도 -->
		<select
			bind:value={selectedRegion}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
		>
			<option value="">- 지역 선택 -</option>
			{#each regionOptions as opt (opt.code)}
				<option value={opt.code}>{opt.label}</option>
			{/each}
		</select>

		<!-- 시군구 -->
		<select
			bind:value={selectedDistrict}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
			disabled={!selectedRegion}
		>
			<option value="">- 상세 지역 선택 -</option>
			{#each districtOptions as opt (opt.code)}
				<option value={opt.code}>{opt.label}</option>
			{/each}
		</select>

		<!-- 병원 카테고리 -->
		<select
			bind:value={categoryCode}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
		>
			<option value="">- 의료기관 분류 -</option>
			{#each categoryOptions as opt (opt.code)}
				<option value={opt.code}>{opt.label}</option>
			{/each}
		</select>

		<!-- 검색 버튼 -->
		<button
			on:click={() => handleSearch(0)}
			class="w-full rounded bg-blue-600 py-2 text-white transition hover:bg-blue-700"
		>
			검색
		</button>

		{#if errorMsg}
			<div class="mt-2 alert alert-error">
				<span>{errorMsg}</span>
			</div>
		{/if}
	</div>

	<!-- 검색 결과 -->
	<div class="mt-8 w-full max-w-3xl space-y-4" aria-live="polite">
		{#if hasSearched && results.length === 0}
			<p class="mt-6 mb-4 text-center text-lg font-semibold text-gray-500">검색 결과가 없습니다.</p>
		{:else if results.length > 0}
			{#each results as hospital (hospital.hospitalCode)}
				<!-- 상세 라우트가 /hospital/[id]일 때 -->
				<a
					href={`/hospital/${encodeURIComponent(hospital.hospitalCode)}`}
					class="block cursor-pointer rounded-lg border bg-white p-4 shadow-sm transition hover:shadow-md"
					sveltekit:prefetch
					aria-label={`${hospital.name} 상세 페이지로 이동`}
				>
					<p class="text-lg font-semibold">{hospital.name}</p>
					<p class="text-sm text-gray-600">{hospital.address}</p>
					<p class="text-sm text-gray-600">{hospital.callNumber ?? ''}</p>
				</a>
			{/each}
		{/if}

		<!-- 페이지네이션 -->
		{#if totalPages > 0}
			<div class="mt-4 flex items-center justify-center gap-2">
				<!-- 이전 -->
				<button on:click={() => handleSearch(page - 1)} disabled={page <= 0} class="btn btn-sm">
					◀
				</button>

				<!-- 페이지 번호 목록 -->
				{#each pageNumbers as p}
					<button
						on:click={() => handleSearch(p)}
						class={`btn btn-sm ${p === page ? 'text-white btn-primary' : 'btn-ghost'}`}
					>
						{p + 1}
					</button>
				{/each}

				<!-- 다음 -->
				<button
					on:click={() => handleSearch(page + 1)}
					disabled={page + 1 >= totalPages}
					class="btn btn-sm"
				>
					▶
				</button>
			</div>
		{/if}
	</div>
</div>
