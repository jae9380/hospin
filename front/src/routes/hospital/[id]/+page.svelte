<script lang="ts">
	// 예시 데이터 (실제 데이터는 props나 store에서 받아오면 됨)
  export let safeBaseInfo = {
    name: "서울대학교병원",
    address: "서울특별시 종로구 대학로 101",
    callNumber: "02-2072-2114"
  };
</script>

<svelte:head>
	<title>{safeBaseInfo.name}</title>
</svelte:head>
<div class="flex flex-col items-center p-6 bg-white min-h-screen">
  <!-- 병원 기본 정보 -->
  <div
    class="rounded-lg shadow-md p-4 w-full max-w-lg"
    style="margin-bottom: 24px;"
  >
    <h2 class="text-xl font-semibold mb-4">병원 기본 정보</h2>

    <div class="font-bold text-lg mb-1">
      {safeBaseInfo.name}
    </div>

    <div class="mb-1">
      <span>{safeBaseInfo.address}</span>
    </div>

    <div>
      <span>
        전화 번호 : <span>{safeBaseInfo.callNumber}</span>
      </span>
    </div>
  </div>
</div>