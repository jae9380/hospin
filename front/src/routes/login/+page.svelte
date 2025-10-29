<script lang="ts">
	import { onMount } from 'svelte';
	import toast, { Toaster } from 'svelte-5-french-toast';
	import type { ApiResponse } from '$lib/types/apiResponse/apiResponse';
	import { goto } from '$app/navigation';
	import { au } from '$lib/au/au';

	// 상태 변수
	let identifierError = false;
	let passwordError = false;

	let identifier: string = '';
	let password: string = '';
	let errorMsg: string = '';

	// 각 input DOM 참조
	let identifierInput: HTMLInputElement;
	let passwordInput: HTMLInputElement;

	async function handleLogin() {
		identifierError = false;
		passwordError = false;
		errorMsg = '';

		// 아이디 검증
		if (identifier.length < 4 || identifier.length > 20) {
            identifierError = true;
			errorMsg = '아이디는 4자 이상 20자 이하이어야 합니다.';
			toasterError('✋ 아이디는 4자 이상 20자 이하로 입력해주세요.');
			identifierInput.focus(); // 문제 있는 input으로 포커스 이동
			return;
		}

		// 비밀번호 검증
		if (password.length < 8 || password.length > 20) {
            passwordError = true;
			errorMsg = '비밀번호는 8자 이상 20자 이하이어야 합니다.';
			toasterError('✋ 비밀번호는 8자 이상 20자 이하로 입력해주세요.');
			passwordInput.focus(); // 문제 있는 input으로 포커스 이동
			return;
		}

		try {
			const response = await au.api().POST('/api/member/login', { body: { identifier, password } })
			console.log(response.data);
			console.log(response.data.data);	

			if (response.data.statusCode > 399) {
				toasterError(response.data.message || '로그인에 실패했습니다.');

				// 예시: 서버에서 MEMBER_NOT_FOUND 에러면 아이디로 포커스
				if (response.data.errorCode === 'MEMBER_NOT_FOUND') {
                    identifierError = true;
					identifierInput.focus();
				}else if(response.data.errorCode === 'INVALID_PASSWORD') {
                    passwordError = true;
                    passwordInput.focus();
                }
				return;
			}else {
				au?.setLogined(response.data?.data)
			}
			toasterSuccess('👋 로그인에 성공했습니다.');

			goto('/');
		} catch (err: any) {
			errorMsg = err.message || '서버 오류가 발생했습니다.';
			toasterError(errorMsg);
		}
	}

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
			<h2 class="text-green6 my-3 text-center text-2xl font-bold">로그인</h2>
		</div>
	</div>

	<div class="flex justify-center">
		<div class="flex w-80 flex-col gap-4">
			<!-- 아이디 -->
			<div class="flex items-center gap-2">
				<span class="w-20">아이디</span>
				<input
					bind:this={identifierInput}
					type="text"
					bind:value={identifier}
					placeholder="최소 4자 ~ 최대 20자 이내"
					class="input flex-1 {identifierError ? 'input-error' : 'input-neutral'}"
				/>
			</div>

			<!-- 비밀번호 -->
			<div class="flex items-center gap-2">
				<span class="w-20">비밀번호</span>
				<input
					bind:this={passwordInput}
					type="password"
					bind:value={password}
					placeholder="최소 8자 ~ 최대 20자"
					class="input flex-1 {passwordError ? 'input-error' : 'input-neutral'}"
				/>
			</div>

			{#if errorMsg}
				<p class="text-sm text-red-500">{errorMsg}</p>
			{/if}

			<button on:click={handleLogin} class="btn w-full btn-neutral"> 로그인 </button>
		</div>
	</div>
</div>
