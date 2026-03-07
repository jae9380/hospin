<script lang="ts">
	import '../app.css';
	import { au } from '$lib/au/au';
	import { onMount } from 'svelte';
	import toast, { Toaster } from 'svelte-5-french-toast';

	import type { HospitalListResponse } from '$lib/types/hospital/list';
	import type { ScheduleList } from '$lib/types/schedule/scheduleList';
	import type { ApiResponse } from '$lib/types/apiResponse/apiResponse';

	let nearSchedules: ScheduleList[] = [];
	let hospitals: HospitalListResponse[] = [];
	let error: string | null = null;
	let isLogin: boolean | null = false;
	let lat: number | null = null;
	let lng: number | null = null;
	let hospitalName = '';

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

		if (!lat || !lng) return;

		try {
			const res = await au.api().GET('/api/hospital/6nearby', {
				params: {
					query: {
						latitude: String(lat),
						longitude: String(lng)
					}
				}
			});

			const apiResponse = res.data as ApiResponse<HospitalListResponse[]>;

			if (apiResponse.resultType !== 'SUCCESS') {
				toast.error(apiResponse.message);
				return;
			}

			hospitals = apiResponse.data ?? [];
		} catch (e) {
			toast.error('데이터 로드 실패 ❌');
		}

		if (au?.isLogin) {
			try {
				const res = await au.api().GET('/api/schedule/getClosestSchedule');

				const apiResponse = res.data as ApiResponse<any[]>;

				if (apiResponse.resultType !== 'SUCCESS') {
					toast.error(apiResponse.message);
					nearSchedules = [];
					return;
				}

				const schedules = apiResponse.data ?? [];

				nearSchedules = schedules.map((s) => ({
					id: s.id,
					title: s.title,
					startTime: new Date(s.startDatetime).toLocaleString()
				}));
			} catch (e) {
				console.error('일정 조회 실패:', e);
				nearSchedules = [];
			}
		}
	});

	const handleClick = (url: string) => {
		au?.goTo(url);
	};
</script>

<svelte:head>
	<title>HosPin</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<div class="mb-6 w-full max-w-3xl">
		<div class="rounded-xl bg-white p-6 shadow-lg">
			<h2 class="mb-3 text-xl font-bold">병원 이름으로 빠른 검색</h2>

			<input
				type="text"
				bind:value={hospitalName}
				placeholder="병원 이름 입력"
				class="input-bordered input w-full"
			/>

			<button
				class="btn mt-3 w-full btn-primary"
				on:click={() => {
					if (hospitalName.trim() !== '') {
						// Au 클래스의 goTo() 메서드 사용
						au?.goTo(`/search?name=${encodeURIComponent(hospitalName)}`);
					}
				}}
			>
				검색하기
			</button>
		</div>
	</div>

	<div class="grid w-full max-w-3xl grid-cols-2 gap-4">
		<div
			class="cursor-pointer rounded-xl bg-white p-6 shadow-lg transition hover:shadow-xl"
			on:click={() => handleClick('/search')}
		>
			병원 상세 검색
		</div>

		<div
			class="cursor-pointer rounded-xl bg-white p-6 shadow-lg transition hover:shadow-xl"
			on:click={() => handleClick('/map')}
		>
			근처 병원 찾기
		</div>

		<div
			class="cursor-pointer rounded-xl bg-white p-6 shadow-lg transition hover:shadow-xl"
			on:click={() => handleClick('/schedule')}
		>
			일정 관리
		</div>

		<div
			class="cursor-pointer rounded-xl bg-white p-6 shadow-lg transition hover:shadow-xl"
			on:click={() => handleClick('/symptomCheck')}
		>
			증상 기반 진료과 안내
		</div>
	</div>

	<div class="mt-8 w-full max-w-3xl space-y-6">
		<!-- 상단 제목 -->
		<h2 class="text-2xl font-bold text-gray-800">Quick Menu</h2>

		<!-- Quick Hospitals -->
		<div class="rounded-xl bg-white p-6 shadow-lg">
			<h3 class="mb-2 text-sm font-semibold text-gray-600">Quick Hospitals</h3>
			<div class="grid grid-cols-1 gap-2 sm:grid-cols-3">
				{#each hospitals as hospital}
					<div
						class="flex cursor-pointer flex-col justify-center rounded border bg-white px-3 py-2 text-sm transition hover:bg-gray-100"
						on:click={() => handleClick(`/hospital/${hospital.hospital_code}`)}
					>
						<p class="font-medium text-gray-800">{hospital.name}</p>
						<p class="text-xs text-gray-500">{hospital.address}</p>
					</div>
				{/each}
			</div>
		</div>

		<!-- Quick Schedules -->
		<div class="rounded-xl bg-white p-6 shadow-lg">
			<h3 class="mb-2 text-sm font-semibold text-gray-600">Quick Schedules</h3>
			<div class="grid grid-cols-1 gap-2 sm:grid-cols-3">
				{#if nearSchedules.length === 0}
					<p class="text-sm text-gray-500">작성된 최근 일정이 없습니다.</p>
				{:else}
					{#each nearSchedules as schedule}
						<div
							class="flex cursor-pointer flex-col justify-center rounded border bg-white px-3 py-2 text-sm transition hover:bg-gray-100"
							on:click={() => handleClick('/schedule')}
						>
							<p class="font-medium text-gray-800">{schedule.title}</p>
							<p class="text-xs text-gray-500">{schedule.startTime}</p>
						</div>
					{/each}
				{/if}
			</div>
		</div>
	</div>
</div>
