<script lang="ts">
	import '../app.css';
	import { au } from '$lib/au/au';

	let hospitalName = '';

	const handleClick = (url: string) => {
		au?.goTo(url);
	};

	// <!-- TODO: 사용자 기준 가까운 5개 병원 매핑, 현재 날짜 기준으로 가까운 스케줄 6개 매핑 만들어야 함
	// <!-- NOTICE: 샘플 데이터
	let nearHospitals = [
		{ name: '동의병원', address: '서울 강남구', callNumber: '02-1234-5678' },
		{ name: '미래병원', address: '서울 서초구', callNumber: '02-8765-4321' },
		{ name: '건강의원', address: '서울 강북구', callNumber: '02-5555-5555' },
		{ name: '건강의원', address: '서울 강북구', callNumber: '02-5555-5555' },
		{ name: '건강의원', address: '서울 강북구', callNumber: '02-5555-5555' }
	];

	let nearSchedules = [
		{ title: '검진 예약', date: '2025-11-25 09:00' },
		{ title: '치과 예약', date: '2025-11-25 14:00' },
		{ title: '약 처방 확인', date: '2025-11-26 10:30' },
		{ title: '물리치료', date: '2025-11-26 15:00' },
		{ title: '물리치료', date: '2025-11-26 15:00' },
		{ title: '물리치료', date: '2025-11-26 15:00' }
	];
</script>

<svelte:head>
	<title>HosPin</title>
</svelte:head>

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
			on:click={() => handleClick('/any')}
		>
			Any..
		</div>
	</div>

	<div class="mt-8 w-full max-w-3xl space-y-6">
		<!-- 상단 제목 -->
		<h2 class="text-2xl font-bold text-gray-800">Quick Menu</h2>

		<!-- Quick Hospitals -->
		<div class="rounded-xl bg-white p-6 shadow-lg">
			<h3 class="mb-2 text-sm font-semibold text-gray-600">Quick Hospitals</h3>
			<div class="grid grid-cols-1 gap-2 sm:grid-cols-3">
				{#each nearHospitals as hospital}
					<div
						class="flex cursor-pointer flex-col justify-center rounded border bg-white px-3 py-2 text-sm transition hover:bg-gray-100"
					>
						<p class="font-medium text-gray-800">{hospital.name}</p>
						<p class="text-xs text-gray-500">{hospital.address}</p>
						<p class="text-xs text-gray-500">{hospital.callNumber}</p>
					</div>
				{/each}
			</div>
		</div>

		<!-- Quick Schedules -->
		<div class="rounded-xl bg-white p-6 shadow-lg">
			<h3 class="mb-2 text-sm font-semibold text-gray-600">Quick Schedules</h3>
			<div class="grid grid-cols-1 gap-2 sm:grid-cols-3">
				{#each nearSchedules as schedule}
					<div
						class="flex cursor-pointer flex-col justify-center rounded border bg-white px-3 py-2 text-sm transition hover:bg-gray-100"
					>
						<p class="font-medium text-gray-800">{schedule.title}</p>
						<p class="text-xs text-gray-500">{schedule.date}</p>
					</div>
				{/each}
			</div>
		</div>
	</div>
</div>
