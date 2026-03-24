<script lang="ts">
	import toast, { Toaster } from 'svelte-5-french-toast';
	import { au } from '$lib/au/au';

	// 상태 변수
	let identifierError = false;
	let passwordError = false;
	let showFindIdResultModal = false;
	let showFindPwResultModal = false;

	let foundIdentifier: string = '';
	let identifier: string = '';
	let password: string = '';
	let errorMsg: string = '';
	let findIdName: string = '';
	let findIdEmail: string = '';
	let findPwName: string = '';
	let findPwId: string = '';
	let findPwEmail: string = '';
	let newPassword: string = '';
	let confirmNewPassword: string = '';
	let authCode: string = '';
	let showAuthCodeInput = false;
	let isEmailVerified = false;
	let showPassword = false;
	let showConfirmPassword = false;

	let activeModal: 'findId' | 'findPassword' | null = null;
	let passwordStatus: 'idle' | 'invalid' | 'mismatch' | 'valid' = 'idle';

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
			const response = await au.api().POST('/api/member/login', { body: { identifier, password } });

			if (response.data.statusCode > 399) {
				toasterError(response.data.message || '로그인에 실패했습니다.');

				// 예시: 서버에서 MEMBER_NOT_FOUND 에러면 아이디로 포커스
				if (response.data.errorCode === 'MEMBER_NOT_FOUND') {
					identifierError = true;
					identifierInput.focus();
				} else if (response.data.errorCode === 'INVALID_PASSWORD') {
					passwordError = true;
					passwordInput.focus();
				}
				return;
			} else {
				au?.setLogined(response.data?.data);
			}
			toasterSuccess('👋 로그인에 성공했습니다.');

			// goto('/');
			au?.goTo('/');
		} catch (err: any) {
			errorMsg = err.message || '서버 오류가 발생했습니다.';
			toasterError(errorMsg);
		}
	}

	async function handleFindIdSubmit() {
		if (!findIdName.trim() || !findIdEmail.trim()) {
			toasterError('이름과 이메일을 모두 입력해주세요.');
			return;
		}

		try {
			const res = await au.api().GET('/api/member/findId', {
				params: {
					query: {
						name: findIdName.trim(),
						email: findIdEmail.trim()
					}
				}
			});

			const apiResponse = res.data;

			if (!apiResponse || apiResponse.statusCode > 399) {
				toasterError(apiResponse?.message || '아이디 찾기에 실패했습니다.');
				return;
			}

			foundIdentifier = apiResponse.data;
			activeModal = null;
			showFindIdResultModal = true;
		} catch (e: any) {
			toasterError(e.message || '서버 오류가 발생했습니다.');
		}
	}

	async function handleSendAuthCode() {
		if (!findPwName.trim() || !findPwEmail.trim() || !findPwId.trim()) {
			toasterError('이름, 아이디, 이메일을 모두 입력해주세요.');
			return;
		}

		try {
			const res = await au.api().POST('/api/member/findPw', {
				params: {
					query: {
						name: findPwName.trim(),
						id: findPwId.trim(),
						email: findPwEmail.trim()
					}
				}
			});

			const apiResponse = res.data;

			if (!apiResponse || apiResponse.statusCode > 399) {
				toasterError(apiResponse?.message || '인증번호 발송에 실패했습니다.');
				return;
			}

			toasterSuccess('인증번호가 이메일로 발송되었습니다.');
			showAuthCodeInput = true;
		} catch (e: any) {
			toasterError(e.message || '서버 오류가 발생했습니다.');
		}
	}

	async function handleVerifyAuthCode() {
		if (!authCode.trim()) {
			toasterError('인증번호를 입력해주세요.');
			return;
		}

		try {
			const res = await au.api().PATCH('/api/member/join/verifyCode', {
				params: {
					query: {
						email: findPwEmail.trim(),
						code: authCode.trim()
					}
				}
			});

			const apiResponse = res.data;

			if (!apiResponse || apiResponse.statusCode > 399) {
				toasterError(apiResponse?.message || '인증번호가 올바르지 않습니다.');
				return;
			}

			toasterSuccess('이메일 인증이 완료되었습니다.');

			isEmailVerified = true;
			activeModal = null;
			showFindPwResultModal = true;
		} catch (e: any) {
			toasterError(e.message || '서버 오류가 발생했습니다.');
		}
	}

	async function handleResetPassword() {
		if (!newPassword || !confirmNewPassword) {
			toasterError('새 비밀번호를 모두 입력해주세요.');
			return;
		}

		if (!validatePasswordRule(newPassword)) {
			toasterError('비밀번호는 8~20자이며 공백 없이 영문, 숫자, 특수문자만 사용 가능합니다.');
			return;
		}

		if (newPassword !== confirmNewPassword) {
			toasterError('비밀번호가 일치하지 않습니다.');
			return;
		}

		try {
			const res = await au.api().PUT('/api/member/resetPassword', {
				body: {
					email: findPwEmail,
					newPassword: newPassword,
					confirmNewPassword: confirmNewPassword
				}
			});

			const apiResponse = res.data;

			if (!apiResponse || apiResponse.statusCode > 399) {
				toasterError(apiResponse?.message || '비밀번호 변경에 실패했습니다.');
				return;
			}

			toasterSuccess('비밀번호가 성공적으로 변경되었습니다.');

			showFindPwResultModal = false;

			// 상태 초기화
			findPwName = '';
			findPwId = '';
			findPwEmail = '';
			newPassword = '';
			confirmNewPassword = '';
		} catch (e: any) {
			toasterError(e.message || '서버 오류가 발생했습니다.');
		}
	}

	function validatePasswordRule(value: string) {
		// 8~20자, 공백 불가, 허용문자: 영문/숫자/특수문자
		const regex = /^[A-Za-z0-9!@#$%^&*()_+{}\[\]:;<>,.?~\\/-]{8,20}$/;
		return regex.test(value);
	}

	function handleFindId() {
		activeModal = 'findId';
	}

	function handleFindPassword() {
		activeModal = 'findPassword';
	}

	function handleSignup() {
		au?.goTo('/signup');
	}

	function closeModal() {
		activeModal = null;
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
	<div class="mt-8 w-80 border-t pt-4">
		<div class="flex justify-between text-sm text-gray-500">
			<button type="button" class="hover:text-black" on:click={handleFindId}> 아이디 찾기 </button>

			<button type="button" class="hover:text-black" on:click={handleFindPassword}>
				비밀번호 찾기
			</button>

			<button type="button" class="hover:text-black" on:click={handleSignup}> 회원가입 </button>
		</div>
	</div>
	{#if activeModal === 'findId'}
		<div class="fixed inset-0 flex items-center justify-center bg-black/40">
			<div class="w-96 rounded-lg bg-white p-6 shadow-lg">
				<h3 class="mb-4 text-lg font-bold">아이디 찾기</h3>

				<!-- 🔥 이름 bind 추가 -->
				<input
					type="text"
					placeholder="가입한 회원 이름"
					bind:value={findIdName}
					class="input-bordered input mb-3 w-full"
				/>

				<!-- 🔥 이메일 bind 추가 -->
				<input
					type="text"
					placeholder="가입한 이메일"
					bind:value={findIdEmail}
					class="input-bordered input w-full"
				/>

				<div class="mt-4 flex justify-end gap-2">
					<button class="btn btn-ghost" on:click={closeModal}>닫기</button>

					<!-- 🔥 함수 연결 -->
					<button class="btn btn-neutral" on:click={handleFindIdSubmit}> 확인 </button>
				</div>
			</div>
		</div>
	{/if}
	{#if showFindIdResultModal}
		<div class="fixed inset-0 flex items-center justify-center bg-black/40">
			<div class="w-96 rounded-lg bg-white p-6 shadow-lg">
				<h3 class="mb-4 text-lg font-bold">아이디 찾기 결과</h3>

				<p class="mb-2 text-sm text-gray-600">회원님의 아이디는</p>

				<p class="text-lg font-semibold text-black">
					{foundIdentifier}
				</p>

				<div class="mt-6 flex justify-end">
					<button
						class="btn btn-neutral"
						on:click={() => {
							showFindIdResultModal = false;
							findIdName = '';
							findIdEmail = '';
						}}
					>
						확인
					</button>
				</div>
			</div>
		</div>
	{/if}

	{#if activeModal === 'findPassword'}
		<div class="fixed inset-0 flex items-center justify-center bg-black/40">
			<div class="w-96 rounded-lg bg-white p-6 shadow-lg">
				<h3 class="mb-4 text-lg font-bold">비밀번호 찾기</h3>

				<input
					type="text"
					placeholder="이름 입력"
					bind:value={findPwName}
					class="input-bordered input mb-3 w-full"
				/>

				<input
					type="text"
					placeholder="아이디 입력"
					bind:value={findPwId}
					class="input-bordered input mb-3 w-full"
				/>

				<input
					type="text"
					placeholder="가입한 이메일 입력"
					bind:value={findPwEmail}
					class="input-bordered input mb-3 w-full"
				/>

				<!-- 인증번호 요청 버튼 -->
				<button class="btn mb-3 w-full btn-neutral" on:click={handleSendAuthCode}>
					인증번호 요청
				</button>

				{#if showAuthCodeInput}
					<input
						type="text"
						placeholder="이메일로 받은 인증번호 입력"
						bind:value={authCode}
						class="input-bordered input mb-3 w-full"
					/>

					<button class="btn w-full btn-success" on:click={handleVerifyAuthCode}>
						인증 확인
					</button>
				{/if}

				<div class="mt-4 flex justify-end">
					<button class="btn btn-ghost" on:click={closeModal}> 닫기 </button>
				</div>
			</div>
		</div>
	{/if}
	{#if showFindPwResultModal}
		<div class="fixed inset-0 flex items-center justify-center bg-black/40">
			<div class="w-96 rounded-lg bg-white p-6 shadow-lg">
				<h3 class="mb-4 text-lg font-bold">비밀번호 찾기</h3>

				<p class="mb-2 text-sm text-gray-600">비밀번호 재설정</p>

				<input
					type={showPassword ? 'text' : 'password'}
					bind:value={newPassword}
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
				<input
					type={showConfirmPassword ? 'text' : 'password'}
					bind:value={confirmNewPassword}
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

				<div class="mt-6 flex justify-end">
					<button class="btn btn-neutral" on:click={handleResetPassword}> 비밀번호 변경 </button>
				</div>
			</div>
		</div>
	{/if}
</div>
