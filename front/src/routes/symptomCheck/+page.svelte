<script lang="ts">
	import { onMount } from 'svelte';
	import toast, { Toaster } from 'svelte-5-french-toast';
	import { au } from '$lib/au/au';
	import type { RecommendedSpecialty } from '$lib/types/apiResponse/symptomcheck/recommendedSpecialty';
	import { lastResult, lastSpecialties } from '$lib/stores';

	let lat: number | null = null;
	let lng: number | null = null;
	let error: string | null = null;
	let symptom = '';
	let loading = false;
	let submitted = false;
	let result: any = null;
	let specialties: RecommendedSpecialty[] = [];

	// store 구독
	lastResult.subscribe((value) => (result = value)); // 추가 작성
	lastSpecialties.subscribe((value) => (specialties = value)); // 추가 작성

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
					console.log(lat, lng); // temp
					resolve();
				},
				(err) => {
					error = '위치 정보를 가져오는데 실패했습니다: ' + err.message;
					resolve();
				}
			);
		});
	});

	const submit = async () => {
		if (!symptom.trim()) {
			toast.error('증상을 입력해주세요');
			return;
		}

		if (lat === null || lng === null) {
			toast.error('위치 정보를 불러오지 못했습니다');
			return;
		}

		submitted = true;
		loading = true;
		result = null;

		const payload = {
			str: symptom,
			latitude: lat,
			longitude: lng
		};

		try {
			const res = await au.api().POST('/api/symptomcheck', {
				headers: { 'Content-Type': 'application/json' },
				body: payload
			});

			const data = await res.data;
			console.log('response:', data);

			if (data.resultType === 'SUCCESS') {
				result = data.data;
				specialties = result.recommended_specialties;

				// 추가 작성: 이전 결과를 store에 저장
				lastResult.set(result);
				lastSpecialties.set(specialties);
			} else {
				toast.error('응답 처리 중 오류가 발생했습니다');
			}
		} catch (e) {
			toast.error('서버와 통신할 수 없습니다');
			console.error(e);
		} finally {
			loading = false;
		}
	};

	const gotoHospital = (hospitalCode: string) => {
		au.goTo(`/hospital/${hospitalCode}`);
	};

	const scrollToTop = () => {
		window.scrollTo({
			top: 0,
			behavior: 'smooth'
		});
	}; // 추가 작성
</script>

<svelte:head>
	<title>증상 기반 진료과 안내</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<p class="mt-3 mb-3 text-xs text-gray-400">
		※ 본 결과는 참고용 정보이며, 정확한 진단은 의료진 상담이 필요합니다.
	</p>

	<div class="w-full max-w-xl rounded-2xl bg-base-100 p-6 shadow-xl">
		<h1 class="mb-2 text-center text-2xl font-bold">증상 기반 진료과 안내</h1>

		<p class="mb-3 text-center text-sm text-gray-500">
			사용자가 입력한 증상을 바탕으로<br />
			방문하면 좋은 진료과를 참고용으로 안내합니다.<br />
			<br />
			추천 병원은 3km 이내 <br />
			진료 가능한 병원 중 3개를 안내합니다.
		</p>

		<h4 class="mb-4 text-sm font-semibold text-red-600">
			※ 본 결과는 전문가의 진단이 아닌, 참고용 정보 입니다.
		</h4>

		<!-- 결과 박스 -->
		{#if result}
			<!-- 수정: 결과가 있을 때만 보여줌 -->
			<div class="mb-4 rounded-xl bg-base-200 p-4">
				<h2 class="mb-4 text-lg font-semibold">안내 결과</h2>
				{#each specialties as specialty, index}
					<!-- 각 추천 진료과 박스 -->
					<div class="mb-4 rounded-xl border bg-base-100 p-4 shadow-md">
						<h2 class="mb-2 text-lg font-semibold">
							추천 진료과 {index + 1}. {specialty.name}
						</h2>

						<ul class="mt-2 list-disc pl-5 text-sm">
							추천 사유 :
							{#each specialty.reasons ?? [] as reason}
								<li>{reason}</li>
							{/each}
						</ul>

						{#each specialty.hospitals as hospital, idx (hospital.hospitalCode ?? idx)}
							<div class="mt-2 rounded-lg border p-3">
								<div
									class="cursor-pointer font-semibold hover:underline"
									on:click={() => gotoHospital(hospital.hospitalCode)}
								>
									{hospital.name}
								</div>
							</div>
						{/each}
					</div>
				{/each}
			</div>
		{/if}

		<!-- 사용자 입력 박스: result가 없을 때만 표시 -->
		{#if !result}
			<!-- 수정: 결과 없을 때만 보여줌 -->
			<textarea
				class="textarea-bordered textarea w-full resize-none"
				rows="4"
				placeholder="예) 목이 따끔거리고 열이 나며 기침이 계속 나요"
				bind:value={symptom}
				disabled={submitted}
			/>

			<!-- 제출 버튼 -->
			<button class="btn mt-4 w-full btn-primary" on:click={submit} disabled={loading || submitted}>
				{#if loading}
					<span class="loading loading-sm loading-spinner"></span>
					확인 중...
				{:else}
					증상 확인하기
				{/if}
			</button>
		{/if}

		<!-- 재입력 버튼: 결과와 UI 분리, 기본 버튼 스타일만 적용 -->
		{#if result}
			<!-- 추가 작성 -->
			<button
				class="btn mt-4 w-full btn-primary"
				on:click={() => {
					result = null;
					specialties = [];
					symptom = '';
					submitted = false;
					lastResult.set(null);
					lastSpecialties.set([]);
					scrollToTop();
				}}
			>
				다시 입력하기
			</button>
		{/if}
	</div>
</div>
