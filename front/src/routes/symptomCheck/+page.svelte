<script lang="ts">
	import { onMount } from 'svelte';
	import { au } from '$lib/au/au';
	import toast, { Toaster } from 'svelte-5-french-toast';

	let symptom = '';
	let loading = false;
	let result = '';

	const submit = async () => {
		if (!symptom.trim()) {
			toast.error('증상을 입력해주세요');
			return;
		}

		loading = true;
		result = '';

		try {
			// const { data, error } = await au?.api().GET('/api/hospital/search', {
			const res = await au?.api().POST('/api/symptomcheck', {
				method: 'POST',
				headers: {
					'Content-Type': 'text/plain'
				},
				body: symptom
			});
			console.log(res);
			const data = await res.data;

			if (data.resultType === 'SUCCESS') {
				console.log('1');
				result = data.data;
				console.log(result);
			} else {
				toast.error('응답 처리 중 오류가 발생했습니다');
			}
		} catch (e) {
			toast.error('서버와 통신할 수 없습니다');
		} finally {
			loading = false;
		}
	};
</script>

<svelte:head>
	<title>증상 기반 진료과 안내</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<div class="w-full max-w-xl rounded-2xl bg-base-100 p-6 shadow-xl">
		<h1 class="mb-2 text-center text-2xl font-bold">증상 기반 진료과 안내</h1>

		<p class="mb-6 text-center text-sm text-gray-500">
			입력한 증상을 바탕으로<br />
			방문하면 좋은 진료과와 의심 가능한 질환을 참고용으로 안내합니다.
		</p>

		<textarea
			class="textarea-bordered textarea w-full resize-none"
			rows="4"
			placeholder="예) 목이 따끔거리고 열이 나며 기침이 계속 나요"
			bind:value={symptom}
			disabled={loading}
		/>

		<button class="btn mt-4 w-full btn-primary" on:click={submit} disabled={loading}>
			{#if loading}
				<span class="loading loading-sm loading-spinner"></span>
				확인 중...
			{:else}
				증상 확인하기
			{/if}
		</button>

		{#if result}
			<div class="mt-6 rounded-xl bg-base-200 p-4">
				<h2 class="mb-2 font-semibold">안내 결과</h2>
				<p class="text-sm leading-relaxed whitespace-pre-line">
					{result}
				</p>
			</div>
		{/if}

		<p class="mt-6 text-xs text-gray-400">
			※ 본 결과는 참고용 정보이며, 정확한 진단은 의료진 상담이 필요합니다.
		</p>
	</div>
</div>
