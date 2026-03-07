<script lang="ts">
	import toast, { Toaster } from 'svelte-5-french-toast';
	import { au } from '$lib/au/au';

	function toasterSuccess(message: string) {
		toast.success(message);
	}

	function toasterError(message: string) {
		toast.error(message);
	}
</script>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-white p-6">
	<div class="container mx-auto px-4">
		<div class="mx-auto my-10 max-w-sm">
			<h2 class="text-green6 my-3 text-center text-2xl font-bold">비밀번호 찾기</h2>
		</div>
	</div>
</div>
