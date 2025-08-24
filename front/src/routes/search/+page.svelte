<script lang="ts">
  // 타입
  import type {
    PageResult,
    HospitalListItemRaw,
    HospitalListItem
  } from '$lib/types/hospital/list';

  // 표준화 유틸
  import { normalizeHospitalListItem } from '$lib/types/hospital/list';

  // 셀렉트 상수
  import { regionOptions } from '$lib/constants/regions';
  import {
    allDistrictOptions,
    getDistrictsByRegion
  } from '$lib/constants/districts';
  import { categoryOptions } from '$lib/constants/categories';

  // =====================================
  // 상태 변수들 (bind:value는 반드시 let!)
  // =====================================
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

  // 시군구 옵션: 선택된 시도 코드에 따라 파생
  let districtOptions: { code: string; parent: string; label: string }[] = [];
  $: districtOptions = selectedRegion
    ? getDistrictsByRegion(selectedRegion)
    : [];

  // =====================================
  // API 베이스 URL
  // - Vite 스타일(VITE_*)과 SvelteKit 스타일(PUBLIC_*) 둘 다 지원
  // =====================================
  const API_BASE_RAW =
    (import.meta.env.VITE_CORE_API_BASE_URL as string | undefined) ??
    (PUBLIC_CORE_API_BASE_URL as string | undefined) ??
    '';

  // 끝 슬래시 정리 (// 방지)
  const API_BASE = API_BASE_RAW.replace(/\/+$/, '');

  // =====================================
  // 검색
  // =====================================
  async function handleSearch(targetPage: number, pageSize: number = 10) {
    hasSearched = true;
    errorMsg = '';
    page = Math.max(0, targetPage); // 음수 방지

    const params = new URLSearchParams({
      name: name ?? '',
      categoryCode: categoryCode ?? '',
      regionCode: selectedRegion ?? '',
      districtCode: selectedDistrict ?? '',
      postalCode: postalCode ?? '',
      address: address ?? '',
      page: String(page),
      size: String(pageSize)
    });

    try {
      const url = `${API_BASE}/api/hospital/search?${params.toString()}`;
      const response = await fetch(url, { method: 'GET' });

      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const data: PageResult<HospitalListItemRaw> = await response.json();

      // 표준화 → 항상 hospitalCode 사용 가능
      results = data.content.map(normalizeHospitalListItem);
      totalPages = data.totalPages ?? 0;

      console.log('검색 결과:', data);
      console.log('쿼리:', params.toString());
    } catch (err: any) {
      console.error('검색 실패:', err);
      results = [];
      totalPages = 0;
      errorMsg = err?.message ?? '검색 중 오류가 발생했습니다.';
      console.log('쿼리:', params.toString());
    }
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
      <div class="alert alert-error mt-2">
        <span>{errorMsg}</span>
      </div>
    {/if}
  </div>

  <!-- 검색 결과 -->
  <div class="mt-8 w-full max-w-3xl space-y-4" aria-live="polite">
    {#if hasSearched && results.length === 0}
      <p class="text-gray-500">검색 결과가 없습니다.</p>
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
      <div class="mt-4 flex items-center justify-center gap-4">
        <button
          on:click={() => handleSearch(page - 1)}
          disabled={page <= 0}
          class="rounded bg-gray-200 px-4 py-1 hover:bg-gray-300 disabled:opacity-50"
        >
          ◀ 이전
        </button>
        <span>
          페이지 {page + 1} / {totalPages}
        </span>
        <button
          on:click={() => handleSearch(page + 1)}
          disabled={page + 1 >= totalPages}
          class="rounded bg-gray-200 px-4 py-1 hover:bg-gray-300 disabled:opacity-50"
        >
          다음 ▶
        </button>
      </div>
    {/if}
  </div>
</div>