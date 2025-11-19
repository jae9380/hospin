<script lang="ts">
	import { onMount, tick } from 'svelte';
	import { page } from '$app/stores';
	import FixedMap from '$lib/FixedMap.svelte';
	import type { HospitalInfoResponse } from '$lib/types/hospital/info';
	import { asmGrdMap } from '$lib/constants/asmGradeMap';
	import { deptCodeMap } from '$lib/constants/deptCodeMap';
	import { au } from '$lib/au/au';
	import toast, { Toaster } from 'svelte-5-french-toast';

	// --- 상태 ---
	let loading = true;
	let errorMsg = '';
	let hospital: HospitalInfoResponse | null = null;

	let id: string = '';

	// 기본 정보(로딩 중 표시용)
	let safeBaseInfo = {
		name: '로딩 중…',
		address: '불러오는 중…',
		callNumber: '-'
	};

	// 탭 상태 (원하면 'details'로 변경 가능)
	let activeTab: 'map' | 'details' = 'map';

	// 지도 좌표
	let lat: number | null = null;
	let lng: number | null = null;

	// 지도 리마운트 키
	let mapMountKey = 0;

	async function switchTab(tab: 'map' | 'details') {
		activeTab = tab;
		if (tab === 'map') {
			await tick();
			mapMountKey += 1;
		}
	}

	onMount(async () => {
		id = $page.params.id;
		try {
			const { data, error } = await au.api().GET('/api/hospital/{id}', {
				params: { path: { id } }
			});

			hospital = data.data;

			// 기본 정보 바인드
			safeBaseInfo = {
				name: data.data.baseInfo.name,
				address: data.data.baseInfo.address,
				callNumber: data.data.baseInfo.callNumber
			};

			// 좌표 number 변환 (NaN 방지)
			const pLat = Number.parseFloat(data.data.baseInfo.latitude);
			const pLng = Number.parseFloat(data.data.baseInfo.longitude);
			lat = Number.isFinite(pLat) ? pLat : null;
			lng = Number.isFinite(pLng) ? pLng : null;

			loading = false;

			// 기본 탭이 지도라면 보정
			if (activeTab === 'map') {
				await tick();
				mapMountKey += 1;
			}
		} catch (e: any) {
			loading = false;
			errorMsg = e?.message ?? '데이터 로드 실패';
			toast.error('데이터 로드 실패 ❌');
		}
	});

	type VisibleGrade = { key: string; label: string; value: number };

	// hospital 로드 이후 자동 계산
	$: visibleGrades = hospital?.gradeInfo?.grades
		? Object.entries(hospital.gradeInfo.grades)
				// 값 정규화(문자 → 숫자)
				.map(([k, v]) => [k, typeof v === 'string' ? Number(v) : v] as [string, number])
				// "없는 데이터" 제거: null/undefined/NaN/0 제거 (0도 숨기고 싶지 않다면 이 줄에서 v !== 0 조건을 지우세요)
				.filter(([, v]) => v != null && Number.isFinite(v) && v !== 0)
				// 매핑에 없는 키는 숨김 (원하면 남기고 라벨을 k로 대체해도 됨)
				.filter(([k]) => Boolean(asmGrdMap[k]))
				// 렌더링용 형태로 변환
				.map(([k, v]) => ({ key: k, label: asmGrdMap[k], value: v }) satisfies VisibleGrade)
		: [];

	$: visibleDepartments = hospital?.detailInfo?.departmentCodes
		? hospital.detailInfo.departmentCodes
				.filter((code) => Boolean(deptCodeMap[code])) // 매핑 존재하는 코드만
				.map((code) => ({
					key: code,
					label: deptCodeMap[code] // 진료과 이름
				}))
		: [];
</script>

<svelte:head>
	<title>{safeBaseInfo.name}</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-white p-6">
	<!-- 병원 기본 정보 -->
	<div class="mb-6 w-full max-w-4xl rounded-lg p-4 shadow-md">
		<h2 class="mb-4 text-xl font-semibold">병원 기본 정보</h2>

		{#if loading}
			<div class="mb-2 h-6 w-48 skeleton"></div>
			<div class="mb-2 h-4 w-80 skeleton"></div>
			<div class="h-4 w-40 skeleton"></div>
		{:else}
			<div class="mb-1 text-lg font-bold">{safeBaseInfo.name}</div>
			<div class="mb-1">{safeBaseInfo.address}</div>
			<div>전화 번호 : <span>{safeBaseInfo.callNumber}</span></div>
		{/if}

		{#if errorMsg}
			<div class="mt-3 alert alert-error">
				<span>불러오기에 실패했습니다: {errorMsg}</span>
			</div>
		{/if}
	</div>

	<!-- 탭 박스 -->
	<div class="w-full max-w-4xl">
		<div class="tabs-lifted tabs">
			<button
				class="tab btn flex-1 btn-outline btn-accent"
				class:btn-active={activeTab === 'map'}
				on:click={() => switchTab('map')}
			>
				지도
			</button>
			<button
				class="tab btn flex-1 btn-outline btn-accent"
				class:btn-active={activeTab === 'details'}
				on:click={() => switchTab('details')}
			>
				상세 정보
			</button>
		</div>

		<div class="rounded-tr-box rounded-b-box border border-base-300 bg-base-100 p-4 shadow-md">
			{#if activeTab === 'map'}
				<div class="flex aspect-video w-full">
					{#if !loading && lat != null && lng != null}
						<FixedMap key={mapMountKey} {lat} {lng} title={safeBaseInfo.name} />
					{:else}
						<div class="flex w-full items-center justify-center">
							<span class="loading loading-lg loading-spinner"></span>
						</div>
					{/if}
				</div>
			{:else if loading}
				<div class="mb-3 h-6 w-40 skeleton"></div>
				<div class="mb-2 h-4 w-full skeleton"></div>
				<div class="h-4 w-5/6 skeleton"></div>
			{:else if hospital}
				<div class="flex flex-col gap-6">
					<div class="flex gap-6">
						<div class="flex-1">
							<h3 class="mb-2 font-semibold">진료 안내</h3>
							<ul class="list-inside list-disc space-y-1">
								<li>접수(평일): {hospital.detailInfo.receptionWeekday || '-'}</li>
								<li>접수(토): {hospital.detailInfo.receptionSaturday || '-'}</li>
								<li>점심(평일): {hospital.detailInfo.lunchWeekday || '-'}</li>
								<li>점심(토): {hospital.detailInfo.lunchSaturday || '-'}</li>
								<li>
									일/공휴일 휴무: {hospital.detailInfo.closedSunday}/{hospital.detailInfo
										.closedHoliday}
								</li>
							</ul>
						</div>
						<div class="flex-1">
							<h3 class="mb-2 font-semibold">진료 시간</h3>
							<ul class="list-inside list-disc space-y-1">
								<li>월: {hospital.detailInfo.treatMonStart} ~ {hospital.detailInfo.treatMonEnd}</li>
								<li>화: {hospital.detailInfo.treatTueStart} ~ {hospital.detailInfo.treatTueEnd}</li>
								<li>수: {hospital.detailInfo.treatWedStart} ~ {hospital.detailInfo.treatWedEnd}</li>
								<li>목: {hospital.detailInfo.treatThuStart} ~ {hospital.detailInfo.treatThuEnd}</li>
								<li>금: {hospital.detailInfo.treatFriStart} ~ {hospital.detailInfo.treatFriEnd}</li>
								<li>토: {hospital.detailInfo.treatSatStart} ~ {hospital.detailInfo.treatSatEnd}</li>
								<li>일: {hospital.detailInfo.treatSunStart} ~ {hospital.detailInfo.treatSunEnd}</li>
							</ul>
						</div>
					</div>

					<div class="flex gap-6">
						<div class="flex-1">
							<h3 class="mb-2 font-semibold">응급</h3>
							<ul class="list-inside list-disc space-y-1">
								<li>
									주간: {hospital.detailInfo.emergencyDayYn} / {hospital.detailInfo
										.emergencyDayPhone1}
									{hospital.detailInfo.emergencyDayPhone2}
								</li>
								<li>
									야간: {hospital.detailInfo.emergencyNightYn} / {hospital.detailInfo
										.emergencyNightPhone1}
									{hospital.detailInfo.emergencyNightPhone2}
								</li>
							</ul>
						</div>
						<div class="flex-1">
							<h3 class="mb-2 font-semibold">진료과</h3>
							<div class="flex flex-wrap gap-2">
								{#each visibleDepartments as dept (dept.key)}
									<span class="badge badge-outline">{dept.label}</span>
								{/each}
							</div>
						</div>
					</div>

					{#if visibleGrades.length > 0}
						<div>
							<h3 class="mb-2 font-semibold">평가/등급</h3>
							<div class="flex flex-wrap gap-2">
								{#each visibleGrades as g (g.key)}
									<div class="badge badge-accent">{g.label}: {g.value}<span>등급</span></div>
								{/each}
							</div>
						</div>
					{/if}
				</div>
			{:else}
				<div class="text-sm text-gray-500">상세 정보를 불러올 수 없습니다.</div>
			{/if}
		</div>
	</div>
</div>
