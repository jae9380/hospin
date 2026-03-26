<script lang="ts">
	import { onMount } from 'svelte';
	import type { ApiResponse } from '$lib/shared/types/apiResponse';
	import toast, { Toaster } from 'svelte-5-french-toast';
	import { genderMap } from '$lib/shared/constants/gender';
	import { au } from '$lib/au/au';
	import { validatePasswordRule } from '$lib/shared/validation';

	let identifier = '';
	let password = '';
	let confirmPassword = '';
	let name = '';
	let phoneNumber = '';
	let email = '';
	let gender: string = '';
	let birth = '';

	// identifier 관련
	let isIdAvailable = false;
	let identifierInput: HTMLInputElement;
	let checkedDuplicate = false;
	let isIdDuplicate = false;

	// password 관련
	let passwordInput: HTMLInputElement;
	let passwordMismatch = false;
	let showPassword = false;
	let showConfirmPassword = false;
	let passwordStatus: 'idle' | 'invalid' | 'mismatch' | 'valid' = 'idle';

	// email 관련
	let isEmailValid = true;

	// 이메일 인증 관련
	let authCode = '';
	let emailSent = false;
	let emailVerified = false;
	let isVerifying = false;

	// select에서 선택된 값 ("male", "female", "other")
	let genderCode: number | null = null; // 숫자 코드 값

	// 아이디 중복 확인
	async function checkIdDuplicate() {
		if (!identifier) {
			toast.error('아이디를 입력해주세요');
			return;
		}

		// 수정
		if (isIdAvailable) return; // 이미 사용 가능이면 재클릭 방지

		try {
			const res = await au!.api().GET('/api/member/check-duplicate', {
				params: { query: { identifier } }
			});

			const apiResponse: ApiResponse<string> | undefined = res?.data;

			if (!apiResponse) {
				toast.error('서버 응답이 올바르지 않습니다.');
				return;
			}

			if (apiResponse.resultType === 'SUCCESS') {
				isIdDuplicate = false;
				checkedDuplicate = true;
				isIdAvailable = true;
				toast.success(apiResponse.data ?? '사용 가능한 아이디입니다 👍');
			} else {
				// 수정
				isIdDuplicate = true;
				checkedDuplicate = true;
				isIdAvailable = false;
				identifierInput?.focus();
				toast.error(apiResponse.data ?? '이미 존재하는 아이디입니다 ❌');
			}
		} catch (err: any) {
			console.log(err);
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

		if (isIdDuplicate) {
			toast.error('이미 존재하는 아이디입니다 ❌');
			identifierInput.focus();
			return;
		}

		if (passwordStatus !== 'valid') {
			toast.error('비밀번호를 확인해주세요 ❌');
			passwordInput?.focus();
			return;
		}

		if (!emailVerified) {
			toast.error('이메일 인증이 필요합니다 ❌');
			return;
		}

		const payload = { identifier, password, name, phoneNumber, email, genderCode, birth };

		try {
			const { data, error } =
				(await au!.api().POST('/api/member/join', {
					body: payload
				})) ?? {};

			if (error || !data || data.statusCode !== 201) {
				toast.error('회원가입 요청 실패 ❌');
				return;
			}

			toast.success('회원가입 성공 🎉');
			au?.goTo('/login');
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

	async function handleSendAuthCode() {
		if (!email || !isEmailValid) {
			toast.error('올바른 이메일을 입력해주세요 ❌');
			return;
		}

		try {
			const res = await au!.api().POST('/api/member/join/sendAuthEmail', {
				params: { query: { email } }
			});

			if (!res?.data || res.data.statusCode > 399) {
				toast.error(res?.data?.message ?? '코드 발송 실패 ❌');
				return;
			}

			emailSent = true;
			toast.success('📨 인증 코드가 발송되었습니다.');
		} catch (err) {
			toast.error('서버 오류 발생 ❌');
		}
	}

	// 인증 코드 확인
	async function handleVerifyAuthCode() {
		if (!authCode.trim()) {
			toast.error('인증 코드를 입력해주세요 ❌');
			return;
		}

		try {
			isVerifying = true;

			const res = await au!.api().PATCH('/api/member/join/verifyCode', {
				params: {
					query: {
						email,
						code: authCode
					}
				}
			});

			if (!res?.data || res.data.statusCode > 399) {
				toast.error(res?.data?.message ?? '인증 실패 ❌');
				return;
			}

			emailVerified = true;
			toast.success('✅ 이메일 인증 완료');
		} catch (err) {
			toast.error('서버 오류 발생 ❌');
		} finally {
			isVerifying = false;
		}
	}

	$: genderCode = gender ? genderMap[gender] : null;
	$: if (identifier) {
		checkedDuplicate = false;
		isIdAvailable = false;
	}
	$: {
		if (!password && !confirmPassword) {
			passwordStatus = 'idle';
		} else if (!validatePasswordRule(password)) {
			passwordStatus = 'invalid';
		} else if (password !== confirmPassword) {
			passwordStatus = 'mismatch';
		} else {
			passwordStatus = 'valid';
		}
	}
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
	{isIdAvailable ? 'border-green-500 focus:ring-green-400' : 'border-red-500 focus:ring-red-400'}"
						required
					/>
					<button
						type="button"
						disabled={isIdAvailable}
						class="rounded-r-md px-4 py-2
						{isIdAvailable
							? 'cursor-not-allowed border border-green-600 bg-green-500 text-white'
							: 'bg-gray-200 hover:bg-gray-300'}"
						on:click={checkIdDuplicate}
					>
						{isIdAvailable ? '사용 가능' : '중복체크'}
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
					class="w-full rounded-md border px-4 py-2 focus:outline-none
						{passwordStatus === 'valid'
						? 'border-green-500 focus:ring-green-400'
						: passwordStatus === 'idle'
							? 'border-gray-300 focus:ring-blue-400'
							: 'border-red-500 focus:ring-red-400'}"
					required
				/>
				<label class="mt-1 inline-flex items-center">
					<input type="checkbox" bind:checked={showPassword} class="mr-2" />
					비밀번호 표시
				</label>
				<p>* 8자 이상, 공백/한글/이모지 불가능</p>
				<p>* 또한, 일부 특수문자는 불가능</p>
			</div>

			<!-- Confirm Password -->
			<div class="mb-4">
				<label class="block text-gray-700">비밀번호 확인</label>
				<input
					type={showConfirmPassword ? 'text' : 'password'}
					bind:value={confirmPassword}
					placeholder="8 ~ 20자"
					class="w-full rounded-md border px-4 py-2 focus:outline-none
						{passwordStatus === 'valid'
						? 'border-green-500 focus:ring-green-400'
						: passwordStatus === 'idle'
							? 'border-gray-300 focus:ring-blue-400'
							: 'border-red-500 focus:ring-red-400'}"
					required
				/>
				<label class="mt-1 inline-flex items-center">
					<input type="checkbox" bind:checked={showConfirmPassword} class="mr-2" />
					비밀번호 표시
				</label>
				{#if passwordStatus === 'invalid'}
					<p class="mt-1 text-sm text-red-500">
						비밀번호는 8자 이상이며 허용된 문자만 사용할 수 있습니다.
					</p>
				{:else if passwordStatus === 'mismatch'}
					<p class="mt-1 text-sm text-red-500">비밀번호가 일치하지 않습니다.</p>
				{:else if passwordStatus === 'valid'}
					<p class="mt-1 text-sm text-green-500">사용 가능한 비밀번호입니다.</p>
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

				<div class="flex">
					<input
						type="email"
						bind:value={email}
						on:input={handleEmailInput}
						placeholder="you@example.com"
						disabled={emailVerified}
						class="flex-1 rounded-l-md border px-4 py-2 focus:outline-none
			{isEmailValid ? 'border-gray-300' : 'border-red-500 focus:ring-red-400'}"
						required
					/>

					<button
						type="button"
						on:click={handleSendAuthCode}
						disabled={!isEmailValid || emailVerified}
						class="rounded-r-md px-4 py-2
			{emailVerified ? 'cursor-not-allowed bg-green-500 text-white' : 'bg-gray-200 hover:bg-gray-300'}"
					>
						{emailVerified ? '인증 완료' : '코드 발송'}
					</button>
				</div>

				{#if !isEmailValid}
					<p class="mt-1 text-sm text-red-500">올바른 이메일 형식을 입력해주세요.</p>
				{/if}

				<!-- 인증 코드 입력 -->
				{#if emailSent && !emailVerified}
					<div class="mt-3 flex">
						<input
							type="text"
							bind:value={authCode}
							placeholder="인증 코드 입력"
							class="flex-1 rounded-l-md border border-gray-300 px-4 py-2 focus:outline-none"
						/>

						<button
							type="button"
							on:click={handleVerifyAuthCode}
							disabled={isVerifying}
							class="rounded-r-md bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
						>
							인증하기
						</button>
					</div>
				{/if}

				{#if emailVerified}
					<p class="mt-2 text-sm text-green-600">✅ 이메일 인증이 완료되었습니다.</p>
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
