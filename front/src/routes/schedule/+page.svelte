<!--
    수정해야 할 부분
    1. 일정 생성 시 반복 여부에 따라 ui 드래그 기능이 있어야 한다.
    2. 반복 일정으로 값을 입력하고, 단일 일정으로 수정 후 일정 생성
        -> 단일 일정에서 불필요한 반복 일정 데이터가 작성 되는 문제점 발생
-->

<script lang="ts">
	import { onMount } from 'svelte';
	import { Calendar } from '@fullcalendar/core';
	import dayGridPlugin from '@fullcalendar/daygrid';
	import timeGridPlugin from '@fullcalendar/timegrid';
	import interactionPlugin from '@fullcalendar/interaction';
	import { au } from '$lib/au/au';
	import { scheduleCategoryMap } from '$lib/constants/schedule/scheduleCategoryMap';
	import toast, { Toaster } from 'svelte-5-french-toast';

	let calendarEl;
	let showModal = false;
	let newEvent = {
		title: '',
		memo: '',
		category: 0,
		type: 0, // ScheduleType: ONE_TIME(0), RECURRING(1)
		recurringType: 0, // RecurringType: NONE(0), DAILY(1), WEEKLY(2), MONTHLY(3)
		recurrenceRule: null,
		startDatetime: '',
		endDatetime: ''
	};
	let showDetailModal = false;
	let selectedEvent: any = null;
	let isEditMode = false;
	let calendar;
	let isLogined = true;

	// (임시 데이터) 서버에서 받아올 일정 목록

	onMount(async () => {
		await au.initAuth();

		if (!au?.isLogin()) {
			console.log(au.isLogin());
			toast.error('회원 유저에게 제공되는 서비스 입니다.');
			isLogined = false;
		}
		try {
			const { data, error } = await au.api().GET('/api/schedule');

			if (error) {
				console.error('❌ 일정 데이터를 불러오지 못했습니다:', error);
				return;
			}

			// ✅ 서버 응답 구조 확인 후 변환
			const schedules = data?.data || []; // 백엔드가 ApiResponse 형식으로 응답한다고 가정

			const headerButtons = isLogined ? 'addEventButton,dayGridMonth,timeGridWeek,timeGridDay' : '';

			console.log(headerButtons);
			const events = schedules.map((s: any) => ({
				id: s.id.toString(),
				title: s.title,
				start: s.startDatetime,
				end: s.endDatetime,
				extendedProps: {
					memo: s.memo,
					category: s.category,
					type: s.type,
					recurringType: s.recurringType,
					recurrenceRule: s.recurrenceRule,
					createdAt: s.createdAt,
					updatedAt: s.updatedAt
				}
			}));

			calendar = new Calendar(calendarEl, {
				plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
				initialView: 'dayGridMonth',
				locale: 'ko',
				timeZone: 'Asia/Seoul',
				weekends: true,
				height: 'auto',
				aspectRatio: 1.5,
				firstDay: 0,
				selectable: true,
				customButtons: {
					addEventButton: {
						text: '+ 일정 추가',
						click: () => {
							showModal = true;
						}
					}
				},
				eventClick: (info) => {
					console.log(info.event);
					selectedEvent = {
						id: info.event.id,
						title: info.event.title,
						start: info.event.startStr,
						end: info.event.endStr,
						memo: info.event.extendedProps.memo,
						category: info.event.extendedProps.category,
						createdAt: info.event.extendedProps.createdAt,
						updatedAt: info.event.extendedProps.updatedAt
					};
					showDetailModal = true;
				},
				events,

				headerToolbar: {
					left: 'prev,next today',
					center: 'title',
					right: headerButtons
				},
				buttonText: {
					today: '현재 날짜',
					month: '달 단위',
					week: '주 단위',
					day: '일 단위'
				},
				events: events
			});

			calendar.render();
		} catch (err) {
			console.error('⚠️ 요청 중 오류 발생:', err);
		}
	});

	async function createEvent() {
		if (!newEvent.title.trim()) {
			toasterError('📢 제목은 필수 입력 항목입니다.');
			return;
		}
		if (!newEvent.startDatetime || !newEvent.endDatetime) {
			toasterError('📢 시작 및 종료 일시는 필수 입력 항목입니다.');
			return;
		}

		const body = {
			title: newEvent.title,
			memo: newEvent.memo,
			category: newEvent.category,
			type: newEvent.type,
			startDatetime: newEvent.startDatetime,
			endDatetime: newEvent.endDatetime,
			recurringType: newEvent.recurringType,
			recurrenceRule: newEvent.recurrenceRule
		};

		const { data, error } = await au.api().POST('/api/schedule', { body });
		if (error) {
			toasterError('🚨 등록에 실패했습니다.');
			return;
		}

		calendar.addEvent({
			title: newEvent.title,
			start: newEvent.startDatetime,
			end: newEvent.endDatetime
		});

		showModal = false;
		resetForm();
	}

	async function updateEvent() {
		if (!selectedEvent) return;

		if (!selectedEvent.title.trim()) {
			toasterError('📢 제목은 필수 입력 항목입니다.');
			return;
		}
		if (!selectedEvent.start || !selectedEvent.end) {
			toasterError('📢 시작 및 종료 일시는 필수 입력 항목입니다.');
			return;
		}

		const body = {
			title: selectedEvent.title,
			memo: selectedEvent.memo,
			category: selectedEvent.category,
			type: selectedEvent.type,
			startDatetime: selectedEvent.start,
			endDatetime: selectedEvent.end,
			recurringType: selectedEvent.recurringType,
			recurrenceRule: selectedEvent.recurrenceRule
		};

		const { error } = await au.api().PUT(`/api/schedule/${selectedEvent.id}`, { body });
		if (error) {
			toasterError('⛔️ 일정 수정 과정에서 문제가 발생 했습니다.');
			return;
		}

		// 캘린더 이벤트 업데이트
		const event = calendar.getEventById(selectedEvent.id);
		if (event) {
			event.setProp('title', selectedEvent.title);
			event.setStart(selectedEvent.start);
			event.setEnd(selectedEvent.end);
			event.setExtendedProp('memo', selectedEvent.memo);
			event.setExtendedProp('category', selectedEvent.category);
			event.setExtendedProp('type', selectedEvent.type);
			event.setExtendedProp('recurringType', selectedEvent.recurringType);
			event.setExtendedProp('recurrenceRule', selectedEvent.recurrenceRule);
		}

		showDetailModal = false;
		toasterSuccess('✅ 일정 수정에 성공 하였습니다.');
	}

	async function deleteEvent() {
		if (!selectedEvent) return;

		const confirmDelete = confirm('정말 이 일정을 삭제하시겠습니까?');
		if (!confirmDelete) return;

		const { error } = await au.api().DELETE(`/api/schedule/${selectedEvent.id}`);
		if (error) {
			toasterError('⛔️ 삭제 과정에서 문제가 발생 했습니다.');
			return;
		}

		// 캘린더 이벤트 삭제
		const event = calendar.getEventById(selectedEvent.id);
		if (event) event.remove();

		showDetailModal = false;
		toasterSuccess('🙂 성공적으로 삭제 하였습니다.');
	}

	function resetForm() {
		newEvent = {
			title: '',
			memo: '',
			category: 0,
			type: 0,
			recurringType: 0,
			recurrenceRule: null,
			startDatetime: '',
			endDatetime: ''
		};
	}

	function formatDatetime(dt: string | null) {
		if (!dt) return '-';
		const date = new Date(dt);
		const y = date.getFullYear();
		const m = String(date.getMonth() + 1).padStart(2, '0');
		const d = String(date.getDate()).padStart(2, '0');
		const hh = String(date.getHours()).padStart(2, '0');
		const mm = String(date.getMinutes()).padStart(2, '0');
		return `${y}.${m}.${d} ${hh}:${mm}`;
	}

	function toasterSuccess(message: string) {
		toast.success(message);
	}

	function toasterError(message: string) {
		toast.error(message);
	}
</script>

<svelte:head>
	<title>달력</title>
</svelte:head>

<Toaster />

<div class="flex min-h-screen flex-col items-center bg-white p-6">
	<div bind:this={calendarEl}></div>
	<!-- 로그인되지 않은 경우 -->
	{#if !isLogined}
		<div class="mt-4">
			<button
				class="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
				on:click={() => (window.location.href = '/login')}
			>
				로그인 후 일정 관리 이용하기
			</button>
		</div>
	{/if}
	<!-- 신규 일정 등록 모달 -->
	{#if showModal}
		<div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
			<div class="w-96 rounded-xl bg-white p-6 shadow-lg">
				<h2 class="mb-6 text-center text-xl font-semibold text-blue-700">📅 새 일정 추가</h2>

				<!-- 제목 -->
				<label class="mb-1 block font-medium text-gray-700">일정 제목</label>
				<input
					class="mb-3 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					placeholder="예: 업무 미팅"
					bind:value={newEvent.title}
				/>

				<!-- 내용 -->
				<label class="mb-1 block font-medium text-gray-700">일정 내용</label>
				<textarea
					class="mb-3 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					rows="3"
					placeholder="예: 팀 회의 또는 병원 진료 등"
					bind:value={newEvent.memo}
				></textarea>

				<!-- 시작 일시 -->
				<label class="mb-1 block font-medium text-gray-700">시작 일시</label>
				<input
					type="datetime-local"
					class="mb-3 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					bind:value={newEvent.startDatetime}
				/>

				<!-- 종료 일시 -->
				<label class="mb-1 block font-medium text-gray-700">종료 일시</label>
				<input
					type="datetime-local"
					class="mb-4 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					bind:value={newEvent.endDatetime}
				/>

				<!-- 카테고리 -->
				<label class="mb-1 block font-medium text-gray-700">카테고리</label>
				<select
					class="mb-4 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					bind:value={newEvent.category}
				>
					{#each Object.entries(scheduleCategoryMap) as [code, label]}
						<option value={+code}>{label}</option>
					{/each}
				</select>

				<!-- 일정 유형 -->
				<label class="mb-1 block font-medium text-gray-700">일정 유형</label>
				<select
					class="mb-4 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
					bind:value={newEvent.type}
				>
					<option value="0">단일 일정</option>
					<option value="1">반복 일정</option>
				</select>

				<!-- 반복 일정 설정 -->
				{#if newEvent.type == 1}
					<div class="mt-3 rounded-lg border border-gray-200 bg-gray-50 p-4">
						<label class="mb-1 block font-medium text-gray-700">반복 주기</label>
						<select
							class="mb-3 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
							bind:value={newEvent.recurringType}
						>
							<option value="1">매일</option>
							<option value="2">매주</option>
							<option value="3">매월</option>
						</select>

						<label class="mb-1 block font-medium text-gray-700">반복 횟수</label>
						<input
							type="number"
							min="1"
							class="mb-3 w-full rounded border border-gray-300 p-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
							placeholder="예: 5 (5회 반복)"
							bind:value={newEvent.recurrenceRule}
						/>
					</div>
				{/if}

				<!-- 버튼 -->
				<div class="mt-5 flex justify-end space-x-2">
					<button
						class="rounded bg-gray-300 px-4 py-2 text-sm font-medium hover:bg-gray-400"
						on:click={() => (showModal = false)}
					>
						취소
					</button>
					<button
						class="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
						on:click={createEvent}
					>
						등록
					</button>
				</div>
			</div>
		</div>
	{/if}

	{#if showDetailModal}
		<div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
			<div class="w-[420px] rounded-xl bg-white p-6 shadow-xl">
				<h2 class="mb-4 text-center text-xl font-semibold text-blue-700">
					📅 {isEditMode ? '일정 수정' : '일정 상세'}
				</h2>

				<!-- 제목 -->
				<label class="mb-1 block font-medium text-gray-700">제목</label>
				{#if isEditMode}
					<input
						class="mb-2 w-full rounded border border-gray-300 p-2"
						bind:value={selectedEvent.title}
					/>
				{:else}
					<p class="mb-2">{selectedEvent.title}</p>
				{/if}

				<!-- 내용 -->
				<label class="mb-1 block font-medium text-gray-700">내용</label>
				{#if isEditMode}
					<textarea
						class="mb-2 w-full rounded border border-gray-300 p-2"
						bind:value={selectedEvent.memo}
					></textarea>
				{:else}
					<p class="mb-2">{selectedEvent.memo || '-'}</p>
				{/if}

				<!-- 시작 일시 -->
				<label class="mb-1 block font-medium text-gray-700">시작 일시</label>
				{#if isEditMode}
					<input
						type="datetime-local"
						class="mb-2 w-full rounded border border-gray-300 p-2"
						bind:value={selectedEvent.start}
					/>
				{:else}
					<p class="mb-2">{formatDatetime(selectedEvent.start)}</p>
				{/if}

				<!-- 종료 일시 -->
				<label class="mb-1 block font-medium text-gray-700">종료 일시</label>
				{#if isEditMode}
					<input
						type="datetime-local"
						class="mb-2 w-full rounded border border-gray-300 p-2"
						bind:value={selectedEvent.end}
					/>
				{:else}
					<p class="mb-2">{formatDatetime(selectedEvent.end)}</p>
				{/if}

				<!-- 카테고리 -->
				<label class="mb-1 block font-medium text-gray-700">카테고리</label>
				{#if isEditMode}
					<select
						class="mb-2 w-full rounded border border-gray-300 p-2"
						bind:value={selectedEvent.category}
					>
						{#each Object.entries(scheduleCategoryMap) as [code, label]}
							<option value={+code}>{label}</option>
						{/each}
					</select>
				{:else}
					<p class="mb-2">{scheduleCategoryMap[selectedEvent.category] || '-'}</p>
				{/if}

				<!-- 버튼 -->
				<div class="mt-4 flex justify-between">
					<!-- 왼쪽 -->

					<div class="flex space-x-2">
						{#if !isEditMode}
							<button
								class="rounded bg-blue-600 px-3 py-2 text-white hover:bg-blue-700"
								on:click={() => (isEditMode = true)}
							>
								수정
							</button>
							<button
								class="rounded bg-red-600 px-3 py-2 text-white hover:bg-red-700"
								on:click={deleteEvent}
							>
								삭제
							</button>
						{:else}
							<button
								class="rounded bg-green-600 px-3 py-2 text-white hover:bg-green-700"
								on:click={updateEvent}
							>
								저장
							</button>
							<button
								class="rounded bg-gray-400 px-3 py-2 text-white hover:bg-gray-500"
								on:click={() => (isEditMode = false)}
							>
								취소
							</button>
						{/if}
					</div>
					<!-- 오른쪽 -->
					<button
						class="rounded bg-gray-300 px-3 py-2 hover:bg-gray-400"
						on:click={() => {
							showDetailModal = false;
							isEditMode = false;
						}}
					>
						닫기
					</button>
				</div>
			</div>
		</div>
	{/if}
</div>
