<script lang="ts">
	import { onMount } from 'svelte';
	import type { ApiResponse } from '$lib/types/apiResponse/apiResponse';
	import toast, { Toaster } from 'svelte-5-french-toast';
	import { genderMap } from '$lib/constants/gender';
	import { goto } from '$app/navigation';

	let identifier = '';
	let password = '';
	let confirmPassword = '';
	let name = '';
	let phoneNumber = '';
	let email = '';
	let gender: string = '';
	let birth = '';

	// identifier 관련
	let identifierInput: HTMLInputElement;
	let checkedDuplicate = false;
	let isIdDuplicate = false;

	// password 관련
	let passwordInput: HTMLInputElement;
	let passwordMismatch = false;
	let showPassword = false;
	let showConfirmPassword = false;

	// email 관련
	let isEmailValid = true;

	// select에서 선택된 값 ("male", "female", "other")
	let genderCode: number | null = null; // 숫자 코드 값

	// 아이디 중복 확인
	async function checkIdDuplicate() {
		if (!identifier) {
			toast.error('아이디를 입력해주세요');
			return;
		}

		try {
			// Au의 api() 사용
			const { data, error } =
				(await au?.api().GET('/api/member/check-duplicate', {
					params: { query: { identifier } }
				})) ?? {};

			if (error) {
				toast.error('서버 오류 발생');
				return;
			}

			if (data?.statusCode === 200) {
				isIdDuplicate = true;
				checkedDuplicate = true;
				toast.success('사용 가능한 아이디입니다 👍');
			} else {
				isIdDuplicate = false;
				checkedDuplicate = true;
				identifierInput.focus();
				toast.error('이미 존재하는 아이디입니다 ❌');
			}
		} catch (err: any) {
			toast.error('서버 오류 발생');
		}
	}

	// 회원가입 처리
	async function handleSignUp() {
		if (!checkedDuplicate) {
			toast.error('아이디 중복 확인이 필요합니다 ❌');
			identifierInput.focus();
			return;
		}

		if (!isIdDuplicate) {
			toast.error('이미 존재하는 아이디입니다 ❌');
			identifierInput.focus();
			return;
		}

		if (passwordMismatch) {
			toast.error('비밀번호가 일치하지 않습니다 ❌');
			passwordInput.focus();
			return;
		}

		const payload = { identifier, password, name, phoneNumber, email, genderCode, birth };

		try {
			const { data, error } =
				(await au?.api().POST('/api/member/join', {
					body: payload
				})) ?? {};

			if (error || !data || data.statusCode !== 200) {
				toast.error('회원가입 요청 실패 ❌');
				return;
			}

			toast.success('회원가입 성공 🎉');
			goto('/login');
		} catch (err: any) {
			console.error('회원가입 에러:', err);
			toast.error('서버 오류 발생 ❌');
		}
	}

	// 이메일 정규식 검증 함수
	function validateEmail(value: string) {
		const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return regex.test(value);
	}

	function handleEmailInput(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		email = value;
		isEmailValid = validateEmail(value) || value === ''; // 빈 값일 때는 valid 처리
	}

	// gender 값이 바뀔 때마다 자동으로 genderCode 업데이트
	$: genderCode = gender ? genderMap[gender] : null;
</script>

<svelte:head>
	<title>HosPin : 회원가입</title>
</svelte:head>

<Toaster />
<div class="flex min-h-screen items-center justify-center bg-gray-100">
	<div class="w-full max-w-md rounded-lg bg-white p-8 shadow-md">
		<h2 class="mb-6 text-center text-2xl font-bold text-blue-600">회원 가입</h2>

		<form on:submit|preventDefault={handleSignUp}>
			<!-- 아이디 -->
			<div class="relative mb-4">
				<label class="block text-gray-700">아이디</label>
				<div class="flex">
					<input
						bind:this={identifierInput}
						type="text"
						bind:value={identifier}
						placeholder="4 ~ 20자"
						class="flex-1 rounded-l-md border px-4 py-2 focus:outline-none
                {isIdDuplicate
							? 'border-gray-300 focus:ring-blue-400'
							: 'border-red-500 focus:ring-red-400'}"
						required
					/>
					<button
						type="button"
						class="rounded-r-md bg-gray-200 px-4 py-2 hover:bg-gray-300"
						on:click={checkIdDuplicate}
					>
						중복체크
					</button>
				</div>
			</div>

			<!-- Password -->
			<div class="mb-4">
				<label class="block text-gray-700">비밀번호</label>
				<input
					type={showPassword ? 'text' : 'password'}
					bind:value={password}
					placeholder="8 ~ 20자"
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none {passwordMismatch
						? 'border-red-500'
						: ''}"
					required
				/>
				<label class="mt-1 inline-flex items-center">
					<input type="checkbox" bind:checked={showPassword} class="mr-2" />
					비밀번호 표시
				</label>
			</div>

			<!-- Confirm Password -->
			<div class="mb-4">
				<label class="block text-gray-700">비밀번호 확인</label>
				<input
					type={showConfirmPassword ? 'text' : 'password'}
					bind:value={confirmPassword}
					placeholder="8 ~ 20자"
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none {passwordMismatch
						? 'border-red-500'
						: ''}"
					required
				/>
				<label class="mt-1 inline-flex items-center">
					<input type="checkbox" bind:checked={showConfirmPassword} class="mr-2" />
					비밀번호 표시
				</label>
				{#if passwordMismatch}
					<p class="mt-1 text-sm text-red-500">비밀번호가 일치하지 않습니다</p>
				{/if}
			</div>

			<!-- Name -->
			<div class="mb-4">
				<label class="block text-gray-700">이름</label>
				<input
					type="text"
					bind:value={name}
					placeholder="이름"
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					required
				/>
			</div>

			<!-- Phone Number -->
			<div class="mb-4">
				<label class="block text-gray-700">전화번호</label>
				<input
					type="number"
					bind:value={phoneNumber}
					placeholder="전화번호"
					inputmode="numeric"
					pattern="[0-9]*"
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					required
				/>
			</div>

			<!-- Email -->
			<div class="mb-4">
				<label class="block text-gray-700">이메일</label>
				<input
					type="email"
					bind:value={email}
					on:input={handleEmailInput}
					placeholder="you@example.com"
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none
        {isEmailValid ? 'border-gray-300' : 'border-red-500 focus:ring-red-400'}"
					required
				/>
				{#if !isEmailValid}
					<p class="mt-1 text-sm text-red-500">올바른 이메일 형식을 입력해주세요.</p>
				{/if}
			</div>

			<!-- Gender -->
			<div class="mb-4">
				<label class="block text-gray-700">성별</label>
				<select
					bind:value={gender}
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					required
				>
					<option value="" disabled selected>선택해주세요</option>
					<option value="male">남성</option>
					<option value="female">여성</option>
					<option value="other">기타</option>
				</select>
			</div>

			<!-- Birth -->
			<div class="mb-4">
				<label class="block text-gray-700">생일</label>
				<input
					type="date"
					bind:value={birth}
					class="w-full rounded-md border px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					required
				/>
			</div>

			<button
				type="submit"
				class="w-full rounded-md bg-blue-500 py-2 text-white transition duration-300 hover:bg-blue-600"
			>
				Sign Up
			</button>
		</form>

		<p class="mt-4 text-center text-sm text-gray-600">
			Already have an account? <a href="#" class="text-blue-500 hover:underline">Login</a>
		</p>
	</div>
</div>
