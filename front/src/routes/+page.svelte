<script>
	import '../app.css';
	import { au } from '$lib/au/au';

	let hospitalName = '';
</script>

<svelte:head>
	<title>HosPin</title>
</svelte:head>

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<div class="mb-6 w-full max-w-3xl">
		<div class="rounded-xl bg-white p-6 shadow-lg">
			<h2 class="mb-3 text-xl font-bold">병원 이름으로 검색</h2>

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
		<div class="rounded-xl bg-white p-6 shadow-lg">Search</div>
		<div class="rounded-xl bg-white p-6 shadow-lg">Map</div>
		<div class="rounded-xl bg-white p-6 shadow-lg">Schedule</div>
		<div class="rounded-xl bg-white p-6 shadow-lg">Any..</div>
	</div>
</div>
