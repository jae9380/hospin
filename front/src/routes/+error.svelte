<!-- src/routes/+error.svelte -->
<script>
  export let error;
  export let status;
</script>

<svelte:head>
  <title>{status} — 페이지를 찾을 수 없습니다</title>
</svelte:head>

<h1>{status}</h1>
<p>{error?.message ?? '요청하신 페이지를 찾을 수 없습니다.'}</p>

<a href="/">메인 페이지로 이동</a>